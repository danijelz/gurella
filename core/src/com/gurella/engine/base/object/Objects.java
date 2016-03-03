package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.base.object.ObjectActivityListener;
import com.gurella.engine.subscriptions.base.object.ObjectCompositionListener;
import com.gurella.engine.subscriptions.base.object.ObjectParentListener;
import com.gurella.engine.subscriptions.base.object.ObjectsActivityListener;
import com.gurella.engine.subscriptions.base.object.ObjectsCompositionListener;
import com.gurella.engine.subscriptions.base.object.ObjectsParentListener;
import com.gurella.engine.utils.Values;

@TypePriorities({ @TypePriority(priority = CommonUpdatePriority.CLEANUP, type = ApplicationUpdateListener.class) })
final class Objects implements ApplicationUpdateListener {
	private static final Objects instance = new Objects();
	private static final Array<ObjectOperation> operations = new Array<ObjectOperation>();
	private static final Array<Object> tempListeners = new Array<Object>(64);

	private Objects() {
	}

	static void activate(ManagedObject object) {
		operation(object, OperationType.activate, null);
	}

	static void deactivate(ManagedObject object) {
		operation(object, OperationType.deactivate, null);
	}

	static void destroy(ManagedObject object) {
		if (object.state == ManagedObjectState.idle) {
			object.handleDestruction();
		} else {
			operation(object, OperationType.destroy, null);
		}
	}

	static void reparent(ManagedObject object, ManagedObject newParent) {
		if (object.state == ManagedObjectState.idle) {
			object.reparent(newParent);
		} else {
			operation(object, OperationType.reparent, newParent);
		}
	}

	private static void operation(ManagedObject object, OperationType operationType, ManagedObject newParent) {
		if (operations.size == 0) {
			EventService.subscribe(instance);
		}

		ObjectOperation operation = PoolService.obtain(ObjectOperation.class);
		operation.object = object;
		operation.operationType = operationType;
		operation.newParent = newParent;
		operations.add(operation);
	}

	static void activated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectActivated(object);
		}

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).activated();
		}
	}

	static void deactivated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectDeactivated(object);
		}

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).deactivated();
		}
	}

	static void childAdded(ManagedObject object, ManagedObject child) {
		Array<ObjectsCompositionListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsCompositionListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).childAdded(object, child);
		}

		Array<ObjectCompositionListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectCompositionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).childAdded(object);
		}
	}

	static void childRemoved(ManagedObject object, ManagedObject child) {
		Array<ObjectsCompositionListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsCompositionListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).childRemoved(object, child);
		}

		Array<ObjectCompositionListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectCompositionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).childRemoved(object);
		}
	}

	static void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		Array<ObjectsParentListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsParentListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).parentChanged(object, oldParent, newParent);
		}

		Array<ObjectParentListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectParentListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).parentChanged(oldParent, newParent);
		}
	}

	@Override
	public void update() {
		while (operations.size > 0) {
			ObjectOperation operation = operations.removeIndex(0);
			operation.execute();
		}
		EventService.unsubscribe(instance);
	}
}
