package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.application.events.ApplicationUpdateSignal.ApplicationUpdateListener;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.pool.PoolService;

//TODO unused
class ObjectOperationPool implements ApplicationUpdateListener {
	private static final Array<ObjectOperation> operations = new Array<ObjectOperation>();

	private static ObjectOperation operation() {
		return PoolService.obtain(ObjectOperation.class);
	}

	static void activate(ManagedObject object) {
		ObjectOperation operation = operation();
		operation.object = object;
		operation.operationType = OperationType.activate;
		operations.add(operation);
	}

	static void deactivate(ManagedObject object) {
		ObjectOperation operation = operation();
		operation.object = object;
		operation.operationType = OperationType.deactivate;
		operations.add(operation);
	}

	static void destroy(ManagedObject object) {
		ObjectOperation operation = operation();
		operation.object = object;
		operation.operationType = OperationType.destroy;
		operations.add(operation);
	}

	static void reparent(ManagedObject object, ManagedObject newParent) {
		ObjectOperation operation = operation();
		operation.object = object;
		operation.newParent = newParent;
		operation.operationType = OperationType.reparent;
		operations.add(operation);
	}

	@Override
	public void update() {
		while (operations.size > 0) {
			ObjectOperation operation = operations.removeIndex(0);
			operation.execute();
		}

		operations.clear();
	}

	@Override
	public int getPriority() {
		return CommonUpdatePriority.CLEANUP;
	}
}
