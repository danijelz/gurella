package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class ObjectOperation implements Poolable {
	ManagedObject object;
	OperationType operationType;
	ManagedObject newParent;
	int newIndex;

	void execute() {
		ManagedObjectState state = object.getState();
		if (state == ManagedObjectState.disposed) {
			free();
			return;
		}

		switch (operationType) {
		case activate:
			if (state.ordinal() < ManagedObjectState.active.ordinal()) {
				object.handleActivation();
			}
			break;
		case deactivate:
			if (state == ManagedObjectState.active) {
				object.handleDeactivation();
			}
			break;
		case reparent:
			object.reparent(newParent);
			break;
		case destroy:
			object.handleDestruction();
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
		activate, reparent, deactivate, destroy;
	}
}
