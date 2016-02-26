package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.base.object.ObjectsActivityListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public class ManagedObject implements Comparable<ManagedObject> {
	private transient int instanceId;
	@PropertyDescriptor(copyable = false)
	String uuid;

	PrefabReference prefab;

	private ManagedObjectState state = ManagedObjectState.idle;

	private transient ManagedObject parent;
	private final Array<ManagedObject> childrenPrivate = new Array<ManagedObject>();
	public transient final ImmutableArray<ManagedObject> children = new ImmutableArray<ManagedObject>(childrenPrivate);

	private final IdentityMap<Object, Attachment<?>> attachments = new IdentityMap<Object, Attachment<?>>();

	public ManagedObject() {
		instanceId = SequenceGenerator.next();
	}

	public int getInstanceId() {
		return instanceId;
	}

	public String getUuid() {
		return uuid;
	}

	public String ensureUuid() {
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
		}
		return uuid;
	}

	public ManagedObject getTemplate() {
		return null;
	}

	//// STATE

	public ManagedObjectState getState() {
		return state;
	}

	public boolean isDisposed() {
		return state == ManagedObjectState.disposed;
	}

	public boolean isActive() {
		return state == ManagedObjectState.active;
	}

	public boolean isInactive() {
		return state == ManagedObjectState.inactive;
	}

	public boolean isInitialized() {
		return state != ManagedObjectState.idle;
	}

	public void activate() {
		ObjectOperationPool.activate(this);
	}

	void handleActivation() {
		if (isActivationAllowed()) {
			activateHierarchy();
		}
	}

	protected boolean isActivationAllowed() {
		return parent == null || parent.isActive();
	}

	private void activateHierarchy() {
		if (state == ManagedObjectState.idle) {
			init();
		}

		state = ManagedObjectState.active;
		attachAll();
		activated();

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			child.activateHierarchy();
		}
	}

	protected void init() {
	}

	protected void activated() {
		Array<ObjectsActivityListener> listeners = new Array<ObjectsActivityListener>();
		EventService.getSubscribers(ObjectsActivityListener.class, listeners);
		for(int i = 0; i < listeners.size; i++) {
			listeners.get(i).objectActivated(this);
		}
		// TODO Auto-generated method stub
	}

	public void deactivate() {
		ObjectOperationPool.deactivate(this);
	}

	void handleDeactivation() {
		if (state != ManagedObjectState.active) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		deactivateHierarchy();
	}

	private void deactivateHierarchy() {
		state = ManagedObjectState.inactive;
		detachAll();
		deactivated();

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			if (child.state == ManagedObjectState.active) {
				child.deactivateHierarchy();
			}
		}
	}

	protected void deactivated() {
		// TODO Auto-generated method stub
	}

	public void destroy() {
		ObjectOperationPool.destroy(this);
	}

	void handleDestruction() {
		if (state == ManagedObjectState.active) {
			deactivateHierarchy();
		}

		state = ManagedObjectState.disposed;
		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			child.handleDestruction();
		}

		if (this instanceof Poolable) {
			PoolService.free(this);
		} else {
			DisposablesService.tryDispose(this);
		}
	}

	protected void reset() {
		if (state != ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}

		instanceId = SequenceGenerator.next();
		uuid = null;
		prefab = null;
		state = ManagedObjectState.idle;
		childrenPrivate.clear();
		clearAttachments();
	}

	//// HIERARCHY

	public ManagedObject getParent() {
		return parent;
	}

	public void setParent(ManagedObject newParent) {
		if (isValidNewParent(newParent)) {
			ObjectOperationPool.reparent(this, newParent);
		}
	}

	protected boolean isValidNewParent(@SuppressWarnings("unused") ManagedObject newParent) {
		return true;
	}

	void reparent(ManagedObject newParent) {
		if (parent == newParent) {
			return;
		}

		validateNewParent(newParent);

		ManagedObject oldParent = parent;
		if (oldParent != null) {
			oldParent.removeChild(this);
		}

		parent = newParent;
		if (newParent != null) {
			updateStateByParent();
			newParent.addChild(this);
		}

		parentChanged(oldParent, newParent);
	}

	private void updateStateByParent() {
		ManagedObjectState parentState = parent.state;
		if (state == ManagedObjectState.active && parentState != ManagedObjectState.active) {
			deactivateHierarchy();
		} else if (state == ManagedObjectState.inactive && parentState == ManagedObjectState.active) {
			activateHierarchy();
		}
	}

	protected void validateNewParent(ManagedObject newParent) {
		if (state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Object is disposed.");
		}

		if (newParent != null && newParent.state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Parent is disposed.");
		}
	}

	protected void parentChanged(ManagedObject oldParent, ManagedObject newParent) {
		// TODO Auto-generated method stub
	}

	private void addChild(ManagedObject child) {
		childrenPrivate.add(child);
		childAdded(child);
	}

	protected void childAdded(ManagedObject child) {
		// TODO Auto-generated method stub
	}

	private void removeChild(ManagedObject child) {
		childrenPrivate.removeValue(child, true);
		childRemoved(child);
	}

	protected void childRemoved(ManagedObject child) {
		// TODO Auto-generated method stub
	}

	//// ATTACHMENTS

	public void attach(Attachment<?> attachment) {
		attachments.put(attachment.value, attachment);
		if (isActive()) {
			attachment.attach();
		}
	}

	private void attachAll() {
		for (Attachment<?> attachment : attachments.values()) {
			attachment.attach();
		}
	}

	public void detach(Object value) {
		Attachment<?> attachment = attachments.remove(value);
		if (attachment != null && isActive()) {
			attachment.detach();
		}
	}

	private void detachAll() {
		for (Attachment<?> attachment : attachments.values()) {
			attachment.detach();
		}
	}

	private void clearAttachments() {
		for (Attachment<?> attachment : attachments.values()) {
			if (attachment instanceof Poolable) {
				PoolService.free(attachment);
			} else {
				DisposablesService.tryDispose(attachment);
			}
		}

		attachments.clear();
	}

	@Override
	public int hashCode() {
		return instanceId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}

		return instanceId == ((ManagedObject) other).instanceId;
	}

	@Override
	public int compareTo(ManagedObject other) {
		return Values.compare(instanceId, other.instanceId);
	}
}
