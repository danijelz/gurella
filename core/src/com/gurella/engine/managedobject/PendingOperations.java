package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.managedobject.ObjectOperation.OperationType;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationCleanupListener;
import com.gurella.engine.utils.priority.Priority;

@Priority(value = Integer.MIN_VALUE, type = ApplicationCleanupListener.class)
class PendingOperations implements ApplicationCleanupListener {
	private Array<ObjectOperation> operations = new Array<ObjectOperation>(64);
	private Array<ObjectOperation> workingOperations = new Array<ObjectOperation>(64);

	private final Object mutex = new Object();

	void addOperation(ManagedObject object, OperationType operationType, ManagedObject newParent) {
		ObjectOperation operation = PoolService.obtain(ObjectOperation.class);
		operation.object = object;
		operation.operationType = operationType;
		operation.newParent = newParent;

		synchronized (mutex) {
			operations.add(operation);
		}
	}

	@Override
	public void cleanup() {
		synchronized (mutex) {
			Array<ObjectOperation> temp = operations;
			operations = workingOperations;
			workingOperations = temp;
		}

		for (int i = 0, n = workingOperations.size; i < n; i++) {
			workingOperations.get(i).execute();
		}

		workingOperations.clear();
	}

	void cleanAll() {
		cleanup();
		synchronized (mutex) {
			if (operations.size == 0) {
				return;
			}
		}
		cleanAll();
	}
}
