package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class ObjectOperation implements Poolable, Comparable<ObjectOperation> {
	ManagedObject object;
	OperationType operationType;
	ManagedObject newParent;

	private static ObjectOperation obtain() {
		return PoolService.obtain(ObjectOperation.class);
	}

	void free() {
		PoolService.free(this);
	}

	static ObjectOperation activate(ManagedObject object) {
		ObjectOperation operation = obtain();
		operation.object = object;
		operation.operationType = OperationType.activate;
		return operation;
	}

	static ObjectOperation deactivate(ManagedObject object) {
		ObjectOperation operation = obtain();
		operation.object = object;
		operation.operationType = OperationType.deactivate;
		return operation;
	}

	static ObjectOperation destroy(ManagedObject object) {
		ObjectOperation operation = obtain();
		operation.object = object;
		operation.operationType = OperationType.destroy;
		return operation;
	}

	static ObjectOperation reparent(ManagedObject object, ManagedObject newParent) {
		ObjectOperation operation = obtain();
		operation.object = object;
		operation.newParent = newParent;
		operation.operationType = OperationType.reparent;
		return operation;
	}

	void execute() {
		switch (operationType) {
		case activate:
			object.activate();
			break;
		case deactivate:
			object.deactivate();
			break;
		case reparent:
			object.setParent(newParent);
			break;
		case destroy:
			object.destroy();
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		free();
	}

	@Override
	public void reset() {
		object = null;
		operationType = null;
		newParent = null;
	}

	@Override
	public int compareTo(ObjectOperation other) {
		return operationType.compareTo(other.operationType);
	}

	private enum OperationType {
		activate, reparent, deactivate, destroy
	}
}
