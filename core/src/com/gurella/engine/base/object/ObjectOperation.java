package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class ObjectOperation implements Poolable {
	ManagedObject object;
	OperationType operationType;
	ManagedObject newParent;

	void free() {
		PoolService.free(this);
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

	enum OperationType {
		activate, reparent, deactivate, destroy
	}
}
