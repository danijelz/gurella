package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.TypePriorities;
import com.gurella.engine.event.TypePriority;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationDebugUpdateListener;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;
import com.gurella.engine.subscriptions.application.CommonUpdatePriority;
import com.gurella.engine.subscriptions.base.object.ObjectActivityListener;
import com.gurella.engine.subscriptions.base.object.ObjectCompositionListener;
import com.gurella.engine.subscriptions.base.object.ObjectDestroyedListener;
import com.gurella.engine.subscriptions.base.object.ObjectParentChangeListener;
import com.gurella.engine.subscriptions.base.object.ObjectsActivityListener;
import com.gurella.engine.subscriptions.base.object.ObjectsCompositionListener;
import com.gurella.engine.subscriptions.base.object.ObjectsDestroyedListener;
import com.gurella.engine.subscriptions.base.object.ObjectsParentListener;
import com.gurella.engine.utils.Values;

final class Objects {
	private static final Array<ObjectOperation> operations = new Array<ObjectOperation>(64);
	private static final Cleaner cleaner = new Cleaner();

	private static final Array<Object> tempListeners = new Array<Object>(64);
	private static final Object mutex = new Object();

	static {
		EventService.subscribe(cleaner);
	}

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
		ObjectOperation operation = PoolService.obtain(ObjectOperation.class);
		operation.object = object;
		operation.operationType = operationType;
		operation.newParent = newParent;

		synchronized (mutex) {
			operations.add(operation);
		}
	}

	// TODO notifications not needed
	static void activated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectActivated(object);
		}
		tempListeners.clear();

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).activated();
		}
		tempListeners.clear();
	}

	static void deactivated(ManagedObject object) {
		Array<ObjectsActivityListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsActivityListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectDeactivated(object);
		}
		tempListeners.clear();

		Array<ObjectActivityListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectActivityListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).deactivated();
		}
		tempListeners.clear();
	}

	static void destroyed(ManagedObject object) {
		Array<ObjectsDestroyedListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsDestroyedListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).objectDestroyed(object);
		}
		tempListeners.clear();

		Array<ObjectDestroyedListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectDestroyedListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).objectDestroyed();
		}
		tempListeners.clear();
	}

	static void childAdded(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		Array<ObjectsCompositionListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsCompositionListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).childAdded(parent, child);
		}
		tempListeners.clear();

		Array<ObjectCompositionListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(parent.instanceId, ObjectCompositionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).childAdded(parent);
		}
		tempListeners.clear();
	}

	static void childRemoved(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		Array<ObjectsCompositionListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsCompositionListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).childRemoved(parent, child);
		}
		tempListeners.clear();

		Array<ObjectCompositionListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(parent.instanceId, ObjectCompositionListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).childRemoved(parent);
		}
		tempListeners.clear();
	}

	static void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		if (!object.isActive()) {
			return;
		}

		Array<ObjectsParentListener> globalListeners = Values.cast(tempListeners);
		EventService.getSubscribers(ObjectsParentListener.class, globalListeners);
		for (int i = 0; i < globalListeners.size; i++) {
			globalListeners.get(i).parentChanged(object, oldParent, newParent);
		}
		tempListeners.clear();

		Array<ObjectParentChangeListener> listeners = Values.cast(tempListeners);
		EventService.getSubscribers(object.instanceId, ObjectParentChangeListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).parentChanged(oldParent, newParent);
		}
		tempListeners.clear();
	}

	@TypePriorities({
			@TypePriority(priority = CommonUpdatePriority.cleanupPriority, type = ApplicationUpdateListener.class),
			@TypePriority(priority = CommonUpdatePriority.preRenderPriority, type = ApplicationDebugUpdateListener.class) })
	private static class Cleaner implements ApplicationUpdateListener, ApplicationDebugUpdateListener {
		@Override
		public void update() {
			synchronized (mutex) {
				for (int i = 0, n = operations.size; i < n; i++) {
					operations.get(i).execute();
				}
				operations.clear();
			}
		}

		@Override
		public void debugUpdate() {
			synchronized (mutex) {
				for (int i = 0, n = operations.size; i < n; i++) {
					operations.get(i).execute();
				}
				operations.clear();
			}
		}
	}
}
