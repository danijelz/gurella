package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject implements Comparable<ManagedObject> {
	public transient final int instanceId;

	@PropertyDescriptor(copyable = false)
	String uuid;
	String templateId;

	private ManagedObjectState state = ManagedObjectState.ready;

	private final IdentityMap<Object, Attachment<?>> attachments = new IdentityMap<Object, Attachment<?>>();

	public ManagedObject() {
		instanceId = SequenceGenerator.next();
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

	public boolean isValid() {
		return state != ManagedObjectState.disposed;
	}

	public boolean isRegistered() {
		return state != ManagedObjectState.ready && state != ManagedObjectState.disposed;
	}

	public boolean isActive() {
		return state == ManagedObjectState.active;
	}

	public boolean isInitialized() {
		return state != ManagedObjectState.ready;
	}

	protected void init() {
	}

	void register() {
		if(state != ManagedObjectState.ready) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		state = ManagedObjectState.inactive;
		init();
	}

	void activate() {
		if(state != ManagedObjectState.inactive) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		state = ManagedObjectState.active;
		attachAll();
	}

	void deactivate() {
		if(state != ManagedObjectState.inactive) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		state = ManagedObjectState.active;
		detachAll();
	}

	void unregister() {
		if(state != ManagedObjectState.inactive) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		state = ManagedObjectState.disposed;
		
		if (this instanceof Poolable) {
			PoolService.free(this);
		} else {
			DisposablesService.tryDispose(this);
		}
	}

	protected void reset() {
		if(isRegistered()) {
			throw new GdxRuntimeException("Invalid state: " + state);
		}
		
		clearAttachments();
		state = ManagedObjectState.ready;
		templateId = null;
		uuid = null;
	}

	//// ATTACHMENTS

	protected void attach(Attachment<?> attachment) {
		attachments.put(attachment.value, attachment);
		if (isActive()) {
			attachment.attach();
		}
	}

	protected void detach(Object value) {
		Attachment<?> attachment = attachments.remove(value);
		if (attachment != null && isActive()) {
			attachment.detach();
		}
	}
	
	private void attachAll() {
		for (Attachment<?> attachment : attachments.values()) {
			attachment.attach();
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
