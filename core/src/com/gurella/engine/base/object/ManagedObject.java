package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.utils.SequenceGenerator;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject implements Comparable<ManagedObject> {
	public transient final int instanceId;

	String uuid;
	String templateId;
	
	private final IdentityMap<Object, Attachment<?, ?>> releasables = new IdentityMap<Object, Attachment<?, ?>>();

	public ManagedObject() {
		instanceId = SequenceGenerator.next();
	}

	void ensureUuid() {
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
		}
	}

	public ManagedObject getTemplate() {
		return null;
	}

	protected void init() {
	}

	void activate() {
	}

	void deactivate() {
	}

	protected void reset() {
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
