package com.gurella.engine.base.resource;

import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject implements Comparable<ManagedObject> {
	private static int indexer = 0;

	public transient final int instanceId;

	String uuid;
	String templateId;

	public ManagedObject() {
		instanceId = indexer++;
	}

	public void ensureUuid() {
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
		}
	}

	public ManagedObject getTemplate() {
		return null;
	}

	protected void init() {
	}

	protected void activate() {
	}

	protected void deactivate() {
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
