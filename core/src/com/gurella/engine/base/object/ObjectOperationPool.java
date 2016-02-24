package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;

//TODO unused
public class ObjectOperationPool {
	private final Array<ObjectOperation> pendingOperations = new Array<ObjectOperation>();

	private void execute() {
		pendingOperations.sort();

		for (ObjectOperation operation : pendingOperations) {
			operation.execute();
		}

		pendingOperations.clear();
	}
}
