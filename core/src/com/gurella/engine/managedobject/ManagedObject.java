package com.gurella.engine.managedobject;

import static com.gurella.engine.managedobject.ManagedObjectState.active;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.Bundle;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.managedobject.ObjectSubscriptionAttachment.ObjectSubscription;
import com.gurella.engine.metatype.PropertyDescriptor;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;
import com.gurella.engine.utils.Sequence;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public abstract class ManagedObject implements Bundle, Comparable<ManagedObject> {
	transient int instanceId;
	transient ManagedObjectState state = ManagedObjectState.idle;// TODO convert to int

	@PropertyDescriptor(property = ManagedObjectUuidProperty.class)
	@PropertyEditorDescriptor(editable = false)
	String uuid;

	@PropertyDescriptor(property = ManagedObjectPrefabProperty.class)
	@PropertyEditorDescriptor(editable = false)
	PrefabReference prefab;

	private transient ManagedObject parent;
	private transient final OrderedIdentitySet<ManagedObject> _children = new OrderedIdentitySet<ManagedObject>();
	public transient final ImmutableArray<ManagedObject> children = _children.orderedItems();

	// TODO move attachments to static map so it consumes less memory
	private final transient IdentityMap<Object, Attachment<?>> attachments = new IdentityMap<Object, Attachment<?>>();

	public ManagedObject() {
		instanceId = Sequence.next();
	}

	public int getInstanceId() {
		return instanceId;
	}

	public String getUuid() {
		return uuid;
	}

	public String ensureUuid() {
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
		}
		return uuid;
	}

	public PrefabReference getPrefab() {
		return prefab;
	}

	//// STATE

	public ManagedObjectState getState() {
		return state;
	}

	public boolean isDisposed() {
		return state == ManagedObjectState.disposed;
	}

	public boolean isActive() {
		return state == ManagedObjectState.active;
	}

	public boolean isInactive() {
		return state == ManagedObjectState.inactive;
	}

	public boolean isInitialized() {
		return state != ManagedObjectState.idle;
	}

	public final void activate() {
		ManagedObjects.activate(this);
	}

	void handleActivation() {
		if (!isActivationAllowed()) {
			return;
		}

		preActivation();

		if (state == ManagedObjectState.idle) {
			init();
		}

		if (this instanceof ApplicationEventSubscription) {
			EventService.subscribe((EventSubscription) this);
		}

		state = ManagedObjectState.active;
		activated();
		ManagedObjects.activated(this);
		attachAll();

		for (int i = 0; i < _children.size; i++) {
			ManagedObject child = _children.get(i);
			child.handleActivation();
		}

		postActivation();
	}

	protected boolean isActivationAllowed() {
		return parent == null || parent.isActive();
	}

	protected void init() {
	}

	protected void preActivation() {
	}

	protected void activated() {
	}

	protected void postActivation() {
	}

	public final void deactivate() {
		ManagedObjects.deactivate(this);
	}

	void handleDeactivation() {
		preDeactivation();

		for (int i = 0, n = _children.size; i < n; i++) {
			ManagedObject child = _children.get(i);
			if (child.state == ManagedObjectState.active) {
				child.handleDeactivation();
			}
		}

		state = ManagedObjectState.inactive;

		deactivated();
		ManagedObjects.deactivated(this);

		if (this instanceof ApplicationEventSubscription) {
			EventService.unsubscribe((EventSubscription) this);
		}

		detachAll();
		postDeactivation();
	}

	protected void preDeactivation() {
	}

	protected void deactivated() {
	}

	protected void postDeactivation() {
	}

	public void destroy() {
		ManagedObjects.destroy(this);
	}

	void handleDestruction() {
		if (state == ManagedObjectState.active) {
			handleDeactivation();
		}

		preDestruction();

		while (_children.size > 0) {
			ManagedObject child = _children.get(_children.size - 1);
			child.handleDestruction();
		}

		if (parent != null) {
			parent._children.remove(this);
			parent.childRemoved(this);
			ManagedObjects.childRemoved(parent, this);
		}

		state = ManagedObjectState.disposed;
		destroyed();
		ManagedObjects.destroyed(this);
		clear();
		postDestruction();
		reset();

		if (AssetService.isManaged(this)) {
			AssetService.unload(this);
		} else if (this instanceof Poolable) {
			PoolService.free(this);
		} else {
			DisposablesService.tryDispose(this);
		}
	}

	private void clear() {
		clearAttachments();
		_children.reset();

		if (prefab != null) {
			prefab.free();
			prefab = null;
		}

		// TODO EventService.removeChannel(instanceId);
	}

	private void reset() {
		if (this instanceof Poolable) {
			instanceId = Sequence.next();
			state = ManagedObjectState.idle;
			parent = null;
			uuid = null;
			resetPoolable();
		}
	}

	protected void preDestruction() {
	}

	protected void destroyed() {
	}

	protected void postDestruction() {
	}

	protected void resetPoolable() {
	}

	//// HIERARCHY

	public ManagedObject getParent() {
		return parent;
	}

	protected final void setParent(ManagedObject newParent) {
		ManagedObjects.reparent(this, newParent);
	}

	void reparent(ManagedObject newParent) {
		if (parent == newParent) {
			return;
		}

		validateReparent(newParent);

		ManagedObject oldParent = parent;
		parent = newParent;

		boolean activationAllowed = isActivationAllowed();
		if (state == active && !activationAllowed) {
			handleDeactivation();
		}

		if (oldParent != null) {
			oldParent._children.remove(this);
			oldParent.childRemoved(this);
			ManagedObjects.childRemoved(oldParent, this);
		}

		if (newParent != null) {
			newParent._children.add(this);
			newParent.childAdded(this);
			ManagedObjects.childAdded(newParent, this);
		}

		else if (state != active && activationAllowed) {
			handleActivation();
		}

		parentChanged(oldParent, newParent);
		ManagedObjects.parentChanged(this, oldParent, newParent);
	}

	protected void validateReparent(ManagedObject newParent) {
		if (state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Object is disposed.");
		}

		if (newParent != null && newParent.state == ManagedObjectState.disposed) {
			throw new GdxRuntimeException("Parent is disposed.");
		}

		// TODO detect cycles
		if (newParent == this) {
			throw new GdxRuntimeException("Parent can't be 'this'.");
		}
	}

	protected void childRemoved(@SuppressWarnings("unused") ManagedObject child) {
	}

	protected void childAdded(@SuppressWarnings("unused") ManagedObject child) {
	}

	@SuppressWarnings("unused")
	protected void parentChanged(ManagedObject oldParent, ManagedObject newParent) {
	}

	//// ATTACHMENTS
	public void attach(Attachment<?> attachment) {
		Object value = attachment.value;
		if (value == null) {
			throw new GdxRuntimeException("Attachment value must be non null.");
		}
		if (attachments.containsKey(value)) {
			throw new GdxRuntimeException("Attachment value must be unique.");
		}

		attachments.put(value, attachment);
		if (isActive()) {
			attachment.attach();
		}
	}

	private void attachAll() {
		for (Attachment<?> attachment : attachments.values()) {
			attachment.attach();
		}
	}

	public boolean detach(Attachment<?> attachment) {
		return detach(attachment.value);
	}

	protected boolean detach(Object value) {
		Attachment<?> attachment = attachments.remove(value);
		if (attachment == null) {
			return false;
		}

		if (isActive()) {
			attachment.detach();
		}

		if (attachment instanceof Poolable) {
			PoolService.free(attachment);
		} else {
			DisposablesService.tryDispose(attachment);
		}

		return true;
	}

	private void detachAll() {
		for (Attachment<?> attachment : attachments.values()) {
			attachment.detach();
		}
	}

	private void clearAttachments() {
		for (Attachment<?> attachment : attachments.values()) {
			if (attachment instanceof Poolable) {
				PoolService.free(attachment);
			} else {
				DisposablesService.tryDispose(attachment);
			}
		}
		attachments.clear();
	}

	protected void subscribe(EventSubscription subscriber) {
		if (subscriber == null) {
			throw new NullPointerException("subscriber is null.");
		}

		attach(SubscriptionAttachment.obtain(subscriber));
	}

	protected boolean unsubscribe(EventSubscription subscriber) {
		if (subscriber == null) {
			throw new NullPointerException("subscriber is null.");
		}
		return detach(subscriber);
	}

	protected void subscribeTo(ManagedObject object, EventSubscription subscriber) {
		if (object == null || subscriber == null) {
			throw new NullPointerException("object or subscriber is null.");
		}
		attach(ObjectSubscriptionAttachment.obtain(object.instanceId, subscriber));
	}

	protected boolean unsubscribeFrom(ManagedObject object, EventSubscription subscriber) {
		if (object == null || subscriber == null) {
			throw new NullPointerException("object or subscriber is null.");
		}
		ObjectSubscription objectSubscription = ObjectSubscription.obtain(object.instanceId, subscriber);
		boolean result = detach(objectSubscription);
		objectSubscription.free();
		return result;
	}

	public void subscribeTo(EventSubscription subscriber) {
		if (subscriber == null) {
			throw new NullPointerException("subscriber is null.");
		}
		attach(ObjectSubscriptionAttachment.obtain(instanceId, subscriber));
	}

	public boolean unsubscribeFrom(EventSubscription subscriber) {
		if (subscriber == null) {
			throw new NullPointerException("subscriber is null.");
		}
		ObjectSubscription objectSubscription = ObjectSubscription.obtain(instanceId, subscriber);
		boolean result = detach(objectSubscription);
		objectSubscription.free();
		return result;
	}

	public <T> T load(String fileName, Class<T> assetType) {
		LoadAssetAttachment<T> attachment = LoadAssetAttachment.obtain(fileName, assetType);
		attach(attachment);
		return attachment.value;
	}

	public <T> void loadAsync(String fileName, Class<T> assetType, AsyncCallback<T> callback) {
		if (callback == null) {
			throw new NullPointerException("callback is null.");
		}
		ManageAssetAttachment.loadAsync(this, fileName, assetType, callback);
	}

	public <T> boolean unload(T asset) {
		return detach(asset);
	}

	public <T> void bindAsset(T asset) {
		attach(ManageAssetAttachment.obtain(asset));
	}

	@Override
	public ObjectMap<String, Object> getBundledAssets() {
		ObjectMap<String, Object> bundledAssets = new ObjectMap<String, Object>();
		appendBundledAssets(bundledAssets, this);
		return bundledAssets;
	}

	private static void appendBundledAssets(ObjectMap<String, Object> bundledAssets, ManagedObject object) {
		if (object.uuid != null) {
			bundledAssets.put(object.uuid, object);
		}
		OrderedIdentitySet<ManagedObject> children = object._children;
		for (int i = 0, n = children.size; i < n; i++) {
			appendBundledAssets(bundledAssets, children.get(i));
		}
	}

	@Override
	public int hashCode() {
		return instanceId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}

		return instanceId == ((ManagedObject) other).instanceId;
	}

	@Override
	public int compareTo(ManagedObject other) {
		return Values.compare(instanceId, other.instanceId);
	}
}
