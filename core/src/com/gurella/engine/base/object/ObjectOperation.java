package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class ObjectOperation implements Poolable {
	ManagedObject object;
	OperationType operationType;
	ManagedObject newParent;

	void execute() {
		ManagedObjectState state = object.getState();
		switch (operationType) {
		case activate:
			if (state == ManagedObjectState.inactive) {
				object.activate();
			}
			break;
		case deactivate:
			if (state == ManagedObjectState.active) {
				object.deactivate();
			}
			break;
		case reparent:
			if (state != ManagedObjectState.disposed) {
				object.setParent(newParent);
			}
			break;
		case destroy:
			if (state != ManagedObjectState.disposed) {
				object.destroy();
			}
			break;
		default:
			throw new IllegalArgumentException();
		}

		free();
	}

	void free() {
		PoolService.free(this);
	}

	@Override
	public void reset() {
		object = null;
		operationType = null;
		newParent = null;
	}

	enum OperationType {
		activate, reparent, deactivate, destroy
	}
}
