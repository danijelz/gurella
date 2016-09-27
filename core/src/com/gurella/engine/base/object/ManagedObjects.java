package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.event.Event0;
import com.gurella.engine.event.Event1;
import com.gurella.engine.event.Event2;
import com.gurella.engine.event.Event3;
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

final class ManagedObjects {
	private static final ObjectsActivatedEvent objectsActivatedEvent = new ObjectsActivatedEvent();
	private static final ObjectsDeactivatedEvent objectsDeactivatedEvent = new ObjectsDeactivatedEvent();
	private static final ObjectActivatedEvent objectActivatedEvent = new ObjectActivatedEvent();
	private static final ObjectDeactivatedEvent objectDeactivatedEvent = new ObjectDeactivatedEvent();

	private static final ChildrenAddedEvent childrenAddedEvent = new ChildrenAddedEvent();
	private static final ChildAddedEvent childAddedEvent = new ChildAddedEvent();
	private static final ChildrenRemovedEvent childrenRemovedEvent = new ChildrenRemovedEvent();
	private static final ChildRemovedEvent childRemovedEvent = new ChildRemovedEvent();

	private static final ParentsChangedEvent parentsChangedEvent = new ParentsChangedEvent();
	private static final ParentChangedEvent parentChangedEvent = new ParentChangedEvent();

	private static final ObjectsDestoyedEvent objectsDestoyedEvent = new ObjectsDestoyedEvent();
	private static final ObjectDestoyedEvent objectDestoyedEvent = new ObjectDestoyedEvent();

	private static final Cleaner cleaner = new Cleaner();
	private static final Object mutex = new Object();

	//TODO private static pool with initial objects
	private static Array<ObjectOperation> operations = new Array<ObjectOperation>(64);
	private static Array<ObjectOperation> workingOperations = new Array<ObjectOperation>(64);

	static {
		EventService.subscribe(cleaner);
	}

	private ManagedObjects() {
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

	// TODO are this notifications needed?
	static void activated(ManagedObject object) {
		EventService.post(objectsActivatedEvent, object);
		EventService.post(object.instanceId, objectActivatedEvent);
	}

	static void deactivated(ManagedObject object) {
		EventService.post(objectsDeactivatedEvent, object);
		EventService.post(object.instanceId, objectDeactivatedEvent);
	}

	static void destroyed(ManagedObject object) {
		EventService.post(objectsDestoyedEvent, object);
		EventService.post(object.instanceId, objectDestoyedEvent);
	}

	static void childAdded(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		EventService.post(childrenAddedEvent, parent, child);
		EventService.post(parent.instanceId, childAddedEvent, child);
	}

	static void childRemoved(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		EventService.post(childrenRemovedEvent, parent, child);
		EventService.post(parent.instanceId, childRemovedEvent, child);
	}

	static void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		if (!object.isActive()) {
			return;
		}

		EventService.post(parentsChangedEvent, object, oldParent, newParent);
		EventService.post(object.instanceId, parentChangedEvent, oldParent, newParent);
	}

	@TypePriorities({
			@TypePriority(priority = CommonUpdatePriority.cleanupPriority, type = ApplicationUpdateListener.class),
			@TypePriority(priority = CommonUpdatePriority.preRenderPriority, type = ApplicationDebugUpdateListener.class) })
	private static class Cleaner implements ApplicationUpdateListener, ApplicationDebugUpdateListener {
		@Override
		public void update() {
			doUpdate();
		}

		@Override
		public void debugUpdate() {
			doUpdate();
		}

		private static void doUpdate() {
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
	}

	private static class ObjectsActivatedEvent implements Event1<ObjectsActivityListener, ManagedObject> {
		@Override
		public Class<ObjectsActivityListener> getSubscriptionType() {
			return ObjectsActivityListener.class;
		}

		@Override
		public void notify(ObjectsActivityListener listener, ManagedObject data) {
			listener.objectActivated(data);
		}
	}

	private static class ObjectsDeactivatedEvent implements Event1<ObjectsActivityListener, ManagedObject> {
		@Override
		public Class<ObjectsActivityListener> getSubscriptionType() {
			return ObjectsActivityListener.class;
		}

		@Override
		public void notify(ObjectsActivityListener listener, ManagedObject data) {
			listener.objectDeactivated(data);
		}
	}

	private static class ObjectActivatedEvent implements Event0<ObjectActivityListener> {
		@Override
		public Class<ObjectActivityListener> getSubscriptionType() {
			return ObjectActivityListener.class;
		}

		@Override
		public void notify(ObjectActivityListener listener) {
			listener.activated();
		}
	}

	private static class ObjectDeactivatedEvent implements Event0<ObjectActivityListener> {
		@Override
		public Class<ObjectActivityListener> getSubscriptionType() {
			return ObjectActivityListener.class;
		}

		@Override
		public void notify(ObjectActivityListener listener) {
			listener.deactivated();
		}
	}

	///////////////////

	private static class ChildrenAddedEvent
			implements Event2<ObjectsCompositionListener, ManagedObject, ManagedObject> {
		@Override
		public Class<ObjectsCompositionListener> getSubscriptionType() {
			return ObjectsCompositionListener.class;
		}

		@Override
		public void notify(ObjectsCompositionListener listener, ManagedObject data1, ManagedObject data2) {
			listener.childAdded(data1, data2);
		}
	}

	private static class ChildrenRemovedEvent
			implements Event2<ObjectsCompositionListener, ManagedObject, ManagedObject> {
		@Override
		public Class<ObjectsCompositionListener> getSubscriptionType() {
			return ObjectsCompositionListener.class;
		}

		@Override
		public void notify(ObjectsCompositionListener listener, ManagedObject data1, ManagedObject data2) {
			listener.childRemoved(data1, data2);
		}
	}

	private static class ChildAddedEvent implements Event1<ObjectCompositionListener, ManagedObject> {
		@Override
		public Class<ObjectCompositionListener> getSubscriptionType() {
			return ObjectCompositionListener.class;
		}

		@Override
		public void notify(ObjectCompositionListener listener, ManagedObject data) {
			listener.childAdded(data);
		}
	}

	private static class ChildRemovedEvent implements Event1<ObjectCompositionListener, ManagedObject> {
		@Override
		public Class<ObjectCompositionListener> getSubscriptionType() {
			return ObjectCompositionListener.class;
		}

		@Override
		public void notify(ObjectCompositionListener listener, ManagedObject data) {
			listener.childRemoved(data);
		}
	}

	private static class ParentsChangedEvent
			implements Event3<ObjectsParentListener, ManagedObject, ManagedObject, ManagedObject> {
		@Override
		public Class<ObjectsParentListener> getSubscriptionType() {
			return ObjectsParentListener.class;
		}

		@Override
		public void notify(ObjectsParentListener listener, ManagedObject child, ManagedObject oldParent,
				ManagedObject newParent) {
			listener.parentChanged(child, oldParent, newParent);
		}
	}

	private static class ParentChangedEvent
			implements Event2<ObjectParentChangeListener, ManagedObject, ManagedObject> {
		@Override
		public Class<ObjectParentChangeListener> getSubscriptionType() {
			return ObjectParentChangeListener.class;
		}

		@Override
		public void notify(ObjectParentChangeListener listener, ManagedObject data1, ManagedObject data2) {
			listener.parentChanged(data1, data2);
		}
	}

	private static class ObjectsDestoyedEvent implements Event1<ObjectsDestroyedListener, ManagedObject> {
		@Override
		public Class<ObjectsDestroyedListener> getSubscriptionType() {
			return ObjectsDestroyedListener.class;
		}

		@Override
		public void notify(ObjectsDestroyedListener listener, ManagedObject data) {
			listener.objectDestroyed(data);
		}
	}

	private static class ObjectDestoyedEvent implements Event0<ObjectDestroyedListener> {
		@Override
		public Class<ObjectDestroyedListener> getSubscriptionType() {
			return ObjectDestroyedListener.class;
		}

		@Override
		public void notify(ObjectDestroyedListener listener) {
			listener.objectDestroyed();
		}
	}
}
