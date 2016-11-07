package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.object.ObjectOperation.OperationType;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
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
import com.gurella.engine.utils.priority.TypePriorities;
import com.gurella.engine.utils.priority.TypePriority;

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

	// TODO private static pool with initial objects
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
		objectsActivatedEvent.object = object;
		EventService.post(objectsActivatedEvent);
		objectsActivatedEvent.object = null;

		EventService.post(object.instanceId, objectActivatedEvent);
	}

	static void deactivated(ManagedObject object) {
		objectsDeactivatedEvent.object = object;
		EventService.post(objectsDeactivatedEvent);
		objectsDeactivatedEvent.object = null;

		EventService.post(object.instanceId, objectDeactivatedEvent);
	}

	static void destroyed(ManagedObject object) {
		objectsDestoyedEvent.object = object;
		EventService.post(objectsDestoyedEvent);
		objectsDestoyedEvent.object = null;

		EventService.post(object.instanceId, objectDestoyedEvent);
	}

	static void childAdded(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		childrenAddedEvent.parent = parent;
		childrenAddedEvent.child = child;
		EventService.post(childrenAddedEvent);
		childrenAddedEvent.parent = null;
		childrenAddedEvent.child = null;

		childAddedEvent.child = child;
		EventService.post(parent.instanceId, childAddedEvent);
		childAddedEvent.child = null;
	}

	static void childRemoved(ManagedObject parent, ManagedObject child) {
		if (!parent.isActive() || !child.isActive()) {
			return;
		}

		childrenRemovedEvent.parent = parent;
		childrenRemovedEvent.child = child;
		EventService.post(childrenRemovedEvent);
		childrenRemovedEvent.parent = null;
		childrenRemovedEvent.child = null;

		childRemovedEvent.child = child;
		EventService.post(parent.instanceId, childRemovedEvent);
		childRemovedEvent.child = null;
	}

	static void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		if (!object.isActive()) {
			return;
		}

		parentsChangedEvent.child = object;
		parentsChangedEvent.oldParent = oldParent;
		parentsChangedEvent.newParent = newParent;
		EventService.post(parentsChangedEvent);
		parentsChangedEvent.child = null;
		parentsChangedEvent.oldParent = null;
		parentsChangedEvent.newParent = null;

		parentChangedEvent.oldParent = oldParent;
		parentChangedEvent.newParent = newParent;
		EventService.post(object.instanceId, parentChangedEvent);
		parentChangedEvent.oldParent = null;
		parentChangedEvent.newParent = null;
	}

	@TypePriorities({
			@TypePriority(priority = CommonUpdatePriority.cleanupPriority, type = ApplicationUpdateListener.class),
			@TypePriority(priority = CommonUpdatePriority.cleanupPriority, type = ApplicationDebugUpdateListener.class) })
	private static class Cleaner implements ApplicationUpdateListener, ApplicationDebugUpdateListener {
		@Override
		public void update() {
			doUpdate();
		}

		@Override
		public void debugUpdate() {
			doUpdate();
			synchronized (mutex) {
				if (operations.size == 0) {
					return;
				}
			}
			debugUpdate();
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

	private static class ObjectsActivatedEvent implements Event<ObjectsActivityListener> {
		ManagedObject object;

		@Override
		public Class<ObjectsActivityListener> getSubscriptionType() {
			return ObjectsActivityListener.class;
		}

		@Override
		public void dispatch(ObjectsActivityListener listener) {
			listener.objectActivated(object);
		}
	}

	private static class ObjectsDeactivatedEvent implements Event<ObjectsActivityListener> {
		ManagedObject object;

		@Override
		public Class<ObjectsActivityListener> getSubscriptionType() {
			return ObjectsActivityListener.class;
		}

		@Override
		public void dispatch(ObjectsActivityListener listener) {
			listener.objectDeactivated(object);
		}
	}

	private static class ObjectActivatedEvent implements Event<ObjectActivityListener> {
		@Override
		public Class<ObjectActivityListener> getSubscriptionType() {
			return ObjectActivityListener.class;
		}

		@Override
		public void dispatch(ObjectActivityListener listener) {
			listener.activated();
		}
	}

	private static class ObjectDeactivatedEvent implements Event<ObjectActivityListener> {
		@Override
		public Class<ObjectActivityListener> getSubscriptionType() {
			return ObjectActivityListener.class;
		}

		@Override
		public void dispatch(ObjectActivityListener listener) {
			listener.deactivated();
		}
	}

	private static class ChildrenAddedEvent implements Event<ObjectsCompositionListener> {
		ManagedObject parent;
		ManagedObject child;

		@Override
		public Class<ObjectsCompositionListener> getSubscriptionType() {
			return ObjectsCompositionListener.class;
		}

		@Override
		public void dispatch(ObjectsCompositionListener listener) {
			listener.childAdded(parent, child);
		}
	}

	private static class ChildrenRemovedEvent implements Event<ObjectsCompositionListener> {
		ManagedObject parent;
		ManagedObject child;

		@Override
		public Class<ObjectsCompositionListener> getSubscriptionType() {
			return ObjectsCompositionListener.class;
		}

		@Override
		public void dispatch(ObjectsCompositionListener listener) {
			listener.childRemoved(parent, child);
		}
	}

	private static class ChildAddedEvent implements Event<ObjectCompositionListener> {
		ManagedObject child;

		@Override
		public Class<ObjectCompositionListener> getSubscriptionType() {
			return ObjectCompositionListener.class;
		}

		@Override
		public void dispatch(ObjectCompositionListener listener) {
			listener.childAdded(child);
		}
	}

	private static class ChildRemovedEvent implements Event<ObjectCompositionListener> {
		ManagedObject child;

		@Override
		public Class<ObjectCompositionListener> getSubscriptionType() {
			return ObjectCompositionListener.class;
		}

		@Override
		public void dispatch(ObjectCompositionListener listener) {
			listener.childRemoved(child);
		}
	}

	private static class ParentsChangedEvent implements Event<ObjectsParentListener> {
		ManagedObject child;
		ManagedObject oldParent;
		ManagedObject newParent;

		@Override
		public Class<ObjectsParentListener> getSubscriptionType() {
			return ObjectsParentListener.class;
		}

		@Override
		public void dispatch(ObjectsParentListener listener) {
			listener.parentChanged(child, oldParent, newParent);
		}
	}

	private static class ParentChangedEvent implements Event<ObjectParentChangeListener> {
		ManagedObject oldParent;
		ManagedObject newParent;

		@Override
		public Class<ObjectParentChangeListener> getSubscriptionType() {
			return ObjectParentChangeListener.class;
		}

		@Override
		public void dispatch(ObjectParentChangeListener listener) {
			listener.parentChanged(oldParent, newParent);
		}
	}

	private static class ObjectsDestoyedEvent implements Event<ObjectsDestroyedListener> {
		ManagedObject object;

		@Override
		public Class<ObjectsDestroyedListener> getSubscriptionType() {
			return ObjectsDestroyedListener.class;
		}

		@Override
		public void dispatch(ObjectsDestroyedListener listener) {
			listener.objectDestroyed(object);
		}
	}

	private static class ObjectDestoyedEvent implements Event<ObjectDestroyedListener> {
		@Override
		public Class<ObjectDestroyedListener> getSubscriptionType() {
			return ObjectDestroyedListener.class;
		}

		@Override
		public void dispatch(ObjectDestroyedListener listener) {
			listener.objectDestroyed();
		}
	}
}
