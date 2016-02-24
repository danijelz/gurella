package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject implements Comparable<ManagedObject> {
	private transient int instanceId;
	@PropertyDescriptor(copyable = false)
	String uuid;
	String prefabId;

	private ManagedObjectState state = ManagedObjectState.ready;

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
	
	public ManagedObjectState getState() {
		return state;
	}

	//// HIERARCHY

	public ManagedObject getParent() {
		return parent;
	}

	public void setParent(ManagedObject parent) {
		if (this.parent == parent) {
			return;
		}

		if (state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Object is disposed.");
		}

		if (parent != null && parent.state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Parent is disposed.");
		}

		ManagedObject oldParent = this.parent;
		if (oldParent != null) {
			oldParent.removeChild(this);
		}

		this.parent = parent;
		if (parent != null) {
			if(state == ManagedObjectState.active && parent.state != ManagedObjectState.active) {
				deactivateHierarchy();
			}
			parent.addChild(this);
		}
		// TODO notify parent changed
	}

	private void addChild(ManagedObject child) {
		childrenPrivate.add(child);
		// TODO notify childAdded()
	}

	private void removeChild(ManagedObject child) {
		childrenPrivate.removeValue(child, true);
		// TODO notify childRemoved()
	}

	//// STATE

	public boolean isValid() {
		return state != ManagedObjectState.disposed;
	}

	public boolean isDisposed() {
		return state == ManagedObjectState.disposed;
	}

	public boolean isActive() {
		return state == ManagedObjectState.active;
	}

	public boolean isInitialized() {
		return state != ManagedObjectState.ready;
	}

	void activate() {
		if (isActivationAllowed()) {
			activateHierarchy();
		}
	}

	protected boolean isActivationAllowed() {
		return parent == null || parent.isActive();
	}

	private void activateHierarchy() {
		if (state == ManagedObjectState.ready) {
			init();
		}
		
		state = ManagedObjectState.active;
		attachAll();
		//TODO notify activated()

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			child.activateHierarchy();
		}
	}

	protected void init() {
	}

	void deactivate() {
		if (state != ManagedObjectState.active) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}

		deactivateHierarchy();
	}

	private void deactivateHierarchy() {
		state = ManagedObjectState.inactive;
		detachAll();
		//TODO notify deactivated()

		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			if (child.state == ManagedObjectState.active) {
				child.deactivateHierarchy();
			}
		}
	}

	public void destroy() {
		if (state == ManagedObjectState.active) {
			deactivateHierarchy();
		}

		state = ManagedObjectState.disposed;
		for (int i = 0; i < childrenPrivate.size; i++) {
			ManagedObject child = childrenPrivate.get(i);
			child.destroy();
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
		prefabId = null;
		state = ManagedObjectState.ready;
		childrenPrivate.clear();
		clearAttachments();
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
		return ValueUtils.compare(instanceId, other.instanceId);
	}
}
