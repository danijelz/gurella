package com.gurella.engine.base.resource;

public class ManagedObject implements Comparable<ManagedObject> {
	private static int indexer = 0;

	int id;
	String templateId;

	String name;
	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
		id = instanceId;
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
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;

		return instanceId == ((ManagedObject) other).instanceId;
	}

	@Override
	public int compareTo(ManagedObject other) {
		return Integer.compare(instanceId, other.instanceId);
	}
}
