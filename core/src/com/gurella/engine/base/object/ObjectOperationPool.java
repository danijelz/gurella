package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.application.events.CommonUpdatePriority;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.base.object.ObjectActivityListener;
import com.gurella.engine.subscriptions.base.object.ObjectsActivityListener;
import com.gurella.engine.utils.Values;

//TODO unused
@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.CLEANUP, type = ApplicationUpdateListener.class) })
class ObjectOperationPool implements ApplicationUpdateListener {
	private static final Array<ObjectOperation> operations = new Array<ObjectOperation>();
	private static final Array<Object> tempListeners = new Array<Object>(64);

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

	static void notifyActivated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectActivated(object);
		}

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).activated();
		}
	}

	static void notifyDeactivated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectDeactivated(object);
		}

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).deactivated();
		}
	}

	@Override
	public void update() {
		while (operations.size > 0) {
			ObjectOperation operation = operations.removeIndex(0);
			operation.execute();
		}
		operations.clear();
	}
}
