package com.gurella.engine.managedobject;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.managedobject.ObjectOperation.OperationType;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.subscriptions.managedobject.ObjectActivityListener;
import com.gurella.engine.subscriptions.managedobject.ObjectCompositionListener;
import com.gurella.engine.subscriptions.managedobject.ObjectDestroyedListener;
import com.gurella.engine.subscriptions.managedobject.ObjectParentChangeListener;
import com.gurella.engine.subscriptions.managedobject.ObjectsActivityListener;
import com.gurella.engine.subscriptions.managedobject.ObjectsCompositionListener;
import com.gurella.engine.subscriptions.managedobject.ObjectsDestroyedListener;
import com.gurella.engine.subscriptions.managedobject.ObjectsParentListener;

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

	private static final IdentityMap<Application, PendingOperations> instances = new IdentityMap<Application, PendingOperations>();

	private static PendingOperations lastSelected;
	private static Application lastApp;

	private ManagedObjects() {
	}

	private static PendingOperations getOperations() {
		PendingOperations operations;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = AsyncService.getApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			operations = instances.get(app);
			if (operations == null) {
				operations = new PendingOperations();
				instances.put(app, operations);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = operations;
		}

		if (subscribe) {
			EventService.subscribe(operations);
			EventService.subscribe(new Cleaner());
		}

		return operations;
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
		getOperations().addOperation(object, operationType, newParent);
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

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			PendingOperations operations;

			synchronized (instances) {
				operations = instances.remove(AsyncService.getApplication());

				if (operations == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}

			EventService.unsubscribe(operations);
			operations.cleanAll();
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
