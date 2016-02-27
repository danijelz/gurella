package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.pool.PoolService;
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
		ObjectOperations.activate(this);
	}

	void handleActivation() {
		if (!isActivationAllowed()) {
			return;
		}

		if (state == ManagedObjectState.idle) {
			init();
		}

		state = ManagedObjectState.active;
		attachAll();
		EventService.subscribe(this);
		activated();
		ObjectOperations.notifyActivated(this);

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			child.handleActivation();
		}
	}

	protected boolean isActivationAllowed() {
		return parent == null || parent.isActive();
	}

	protected void init() {
	}

	protected void activated() {
	}

	public void deactivate() {
		ObjectOperations.deactivate(this);
	}

	void handleDeactivation() {
		state = ManagedObjectState.inactive;

		deactivated();
		ObjectOperations.notifyDeactivated(this);
		EventService.unsubscribe(this);
		detachAll();

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			if (child.state == ManagedObjectState.active) {
				child.handleDeactivation();
			}
		}
	}

	protected void deactivated() {
	}

	public void destroy() {
		ObjectOperations.destroy(this);
	}

	void handleDestruction() {
		if (state == ManagedObjectState.active) {
			handleDeactivation();
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
			ObjectOperations.reparent(this, newParent);
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
			handleDeactivation();
		} else if (state == ManagedObjectState.inactive && parentState == ManagedObjectState.active) {
			handleActivation();
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
