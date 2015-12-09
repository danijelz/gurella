package com.gurella.engine.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.application.Application;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.resource.ResourceMap.ResourceMapEntry;

//TODO Pools class is not thread safe
public class ResourceContext {
	private ResourceContext parent;
	private IntMap<ResourceReference<?>> references = new IntMap<ResourceReference<?>>();
	private ObjectMap<Object, ObtainedResource> obtainedResources = new ObjectMap<Object, ObtainedResource>();

	private ObjectMap<String, AssetResourceReference<?>> assetsByFileName = new ObjectMap<String, AssetResourceReference<?>>();
	private OrderedMap<String, AssetResourceDescriptor<?>> assetDescriptorsByFileName = new OrderedMap<String, AssetResourceDescriptor<?>>();
	
	private int lastId = -1;

	public ResourceContext(ResourceContext parent) {
		this.parent = parent;
	}

	public ResourceContext getParent() {
		return parent;
	}

	public IntMap<ResourceReference<?>> getReferences() {
		return references;
	}

	public void add(ResourceReference<?> reference) {
		int resourceId = reference.getId();
		if (references.containsKey(resourceId)) {
			throw new IllegalArgumentException("Duplicate resourceId: " + resourceId);
		}

		reference.owningContext = this;
		references.put(resourceId, reference);

		if (reference instanceof AssetResourceReference<?>) {
			AssetResourceReference<?> asset = (AssetResourceReference<?>) reference;
			assetsByFileName.put(asset.getResourceFactory().getFileName(), asset);
		}
	}

	public ResourceReference<?> remove(int resourceId) {
		return references.remove(resourceId);
	}

	public <T> T obtainResource(int resourceId) {
		ResourceReference<T> resourceReference = getReference(resourceId);
		return ObtainResourceCallback.run(this, resourceReference);
	}

	//TODO must be implemented vithout callback
	public void obtainResources(IntArray resourceIds, AsyncResourceCallback<ResourceMap> callback) {
		ConcurrentResolver.resolve(this, resourceIds, callback);
	}

	public <T> void obtainResourceAsync(int resourceId, AsyncResourceCallback<T> callback) {
		try {
			ResourceReference<T> resourceReference = getReference(resourceId);
			ObtainResourceTask.run(this, resourceReference, callback);
		} catch (Exception exception) {
			callback.handleException(exception);
		}
	}

	public void obtainResourcesAsync(IntArray resourceIds, AsyncResourceCallback<ResourceMap> callback) {
		AsyncResolver.resolve(this, resourceIds, callback);
	}

	private synchronized <T> void addObtainedResource(T resource, ResourceReference<T> reference) {
		ObtainedResource obtainedResource = obtainedResources.get(resource);
		if (obtainedResource == null) {
			obtainedResource = ObtainedResource.getInstance();
			obtainedResource.reference = reference;
			obtainedResources.put(resource, obtainedResource);
		}

		obtainedResource.count++;
	}

	// TODO make private
	public void rollback(ResourceMap resourceMap) {
		Array<ResourceMapEntry<?>> entries = resourceMap.entries;
		for (int i = 0; i < entries.size; i++) {
			ResourceMapEntry<?> entry = entries.get(i);
			Array<?> initializedResources = entry.initializedResources;
			for (int j = 0; j < initializedResources.size; j++) {
				releaseResource(initializedResources.get(j));
			}
		}
	}

	public void releaseResources() {
		for (Entry<Object, ObtainedResource> entry : obtainedResources.entries()) {
			Object resource = entry.key;
			ObtainedResource obtainedResource = entry.value;
			@SuppressWarnings("unchecked")
			ResourceReference<Object> resourceReference = (ResourceReference<Object>) obtainedResource.reference;
			for (int i = 0; i < obtainedResource.count; i++) {
				resourceReference.release(resource);
			}
			obtainedResource.free();
		}

		obtainedResources.clear();
	}

	public boolean releaseResource(Object resource) {
		ObtainedResource obtainedResource = obtainedResources.get(resource);
		if (obtainedResource != null) {
			@SuppressWarnings("unchecked")
			ResourceReference<Object> resourceReference = (ResourceReference<Object>) obtainedResource.reference;
			resourceReference.release(resource);
			if (--obtainedResource.count == 0) {
				obtainedResources.remove(resource);
				obtainedResource.free();
			}
			return true;
		}

		return false;
	}

	public int getNextId() {
		while (++lastId < 1000000) {
			if (!references.containsKey(lastId) && (parent == null || !parent.references.containsKey(lastId))) {
				return lastId;
			}
		}
		throw new IllegalStateException("Too many resources!");
	}

	public <T> ResourceReference<T> getReference(int resourceId) {
		@SuppressWarnings("unchecked")
		ResourceReference<T> resourceReference = (ResourceReference<T>) references.get(resourceId);
		if (resourceReference != null) {
			return resourceReference;
		} else if (parent != null) {
			return parent.getReference(resourceId);
		} else {
			throw new IllegalArgumentException("Invalid resource id: " + resourceId);
		}
	}

	public <T> ResourceReference<T> findReference(int resourceId) {
		@SuppressWarnings("unchecked")
		ResourceReference<T> resourceReference = (ResourceReference<T>) references.get(resourceId);
		if (resourceReference != null) {
			return resourceReference;
		} else if (parent != null) {
			return parent.getReference(resourceId);
		} else {
			return null;
		}
	}

	public boolean containsAsset(String fileName) {
		return assetsByFileName.containsKey(fileName);
	}

	@SuppressWarnings("unchecked")
	public <T> AssetResourceReference<T> getAssetReference(String fileName) {
		AssetResourceReference<T> assetReference = (AssetResourceReference<T>) assetsByFileName.get(fileName);
		if (assetReference != null) {
			return assetReference;
		} else if (parent != null) {
			return parent.getAssetReference(fileName);
		} else {
			throw new IllegalArgumentException("Invalid asset fileName: " + fileName);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> AssetResourceReference<T> findAssetReference(String fileName) {
		AssetResourceReference<T> assetReference = (AssetResourceReference<T>) assetsByFileName.get(fileName);
		if (assetReference != null) {
			return assetReference;
		} else if (parent != null) {
			return parent.findAssetReference(fileName);
		} else {
			return null;
		}
	}

	public AssetResourceDescriptor<?> addAssetDescriptor(AssetResourceDescriptor<?> descriptor) {
		return assetDescriptorsByFileName.put(descriptor.getBaseFileName(), descriptor);
	}

	@SuppressWarnings("unchecked")
	public <T> AssetResourceDescriptor<T> findAssetDescriptor(String fileName) {
		AssetResourceDescriptor<T> assetResourceDescriptor = (AssetResourceDescriptor<T>) assetDescriptorsByFileName
				.get(fileName);
		if (assetResourceDescriptor != null) {
			return assetResourceDescriptor;
		} else if (parent != null) {
			return parent.findAssetDescriptor(fileName);
		} else {
			return null;
		}
	}

	public synchronized <T> T load(String fileName, Class<T> type) {
		AssetResourceReference<T> assetReference = findOrCreateAssetReference(fileName, type);
		validateAssetType(fileName, type, assetReference.getResourceFactory().getResourceType());
		return obtainResource(assetReference.getId());
	}

	public synchronized <T> AssetResourceReference<T> findOrCreateAssetReference(String fileName, Class<T> type) {
		AssetResourceReference<T> assetReference = findAssetReference(fileName);
		if (assetReference != null) {
			return assetReference;
		}

		assetReference = findOrCreateAssetReferenceByDescriptor(fileName);
		if (assetReference != null) {
			return assetReference;
		}

		return createAssetReferenceByFile(fileName, type);
	}

	@SuppressWarnings("unchecked")
	private <T> AssetResourceReference<T> findOrCreateAssetReferenceByDescriptor(String fileName) {
		AssetResourceDescriptor<T> descriptor = (AssetResourceDescriptor<T>) assetDescriptorsByFileName.get(fileName);
		if (descriptor != null) {
			AssetResourceReference<T> assetReference = new AssetResourceReference<T>(getNextId(), descriptor);
			add(assetReference);
			return assetReference;
		} else if (parent != null) {
			return parent.findOrCreateAssetReferenceByDescriptor(fileName);
		} else {
			return null;
		}
	}

	private <T> AssetResourceReference<T> createAssetReferenceByFile(String fileName, Class<T> type) {
		FileHandle file = Gdx.files.internal(fileName);
		if (!file.exists()) {
			throw new GdxRuntimeException("File not found: " + file + " (" + type + ")");
		}

		AssetResourceReference<T> assetReference = new AssetResourceReference<T>(getNextId(), false, false, file, type);
		add(assetReference);
		return assetReference;
	}

	private static <T> void validateAssetType(String fileName, Class<T> type, Class<T> resourceType) {
		if (resourceType != type) {
			throw new GdxRuntimeException(
					"Asset with name '" + fileName + "' already in preload queue, but has different type (expected: "
							+ ClassReflection.getSimpleName(type) + ", found: "
							+ ClassReflection.getSimpleName(resourceType) + ")");
		}
	}

	public synchronized <T> void loadAsync(String fileName, Class<T> type, AsyncResourceCallback<T> callback) {
		AssetResourceReference<T> assetReference = findOrCreateAssetReference(fileName, type);
		validateAssetType(fileName, type, assetReference.getResourceFactory().getResourceType());
		obtainResourceAsync(assetReference.getId(), callback);
	}

	public OrderedMap<String, AssetResourceDescriptor<?>> getAssetDescriptors() {
		return assetDescriptorsByFileName;
	}

	private static class ObtainResourceCallback<T> implements AsyncResourceCallback<T>, Poolable {
		private ResourceContext context;
		private ResourceReference<T> resourceReference;

		private boolean done;
		private T obtainedResource;
		private Throwable resourceException;

		public static <T> T run(ResourceContext context, ResourceReference<T> resourceReference) {
			@SuppressWarnings("unchecked")
			ObtainResourceCallback<T> instance = SynchronizedPools.obtain(ObtainResourceCallback.class);
			instance.context = context;
			instance.resourceReference = resourceReference;

			try {
				return instance.getResource();
			} finally {
				SynchronizedPools.free(instance);
			}
		}

		private T getResource() {
			resourceReference.obtain(this);
			while (!done) {
				try {
					synchronized (this) {
						wait(5);
					}
				} catch (InterruptedException e) {
					continue;
				}
			}

			if (resourceException != null) {
				// TODO specialized exception
				throw new GdxRuntimeException("Error obtaining resource: " + resourceReference.getId(),
						resourceException);
			} else {
				context.addObtainedResource(obtainedResource, resourceReference);
				return obtainedResource;
			}
		}

		@Override
		public void handleResource(T resource) {
			if (resource == null) {
				resourceException = new GdxRuntimeException("Resource is null: " + resourceReference.getId());
			} else {
				this.obtainedResource = resource;
			}
			done = true;
		}

		@Override
		public void handleException(Throwable throwable) {
			this.resourceException = throwable;
			done = true;
		}

		@Override
		public void handleProgress(float progress) {
		}

		@Override
		public void reset() {
			context = null;
			resourceReference = null;

			done = false;
			obtainedResource = null;
			resourceException = null;
		}
	}

	private static class ObtainResourceTask<T> implements AsyncTask<Void>, AsyncResourceCallback<T>, Poolable {
		private ResourceContext context;
		private ResourceReference<T> resourceReference;
		private AsyncResourceCallback<T> callback;

		public static <T> void run(ResourceContext context, ResourceReference<T> resourceReference,
				AsyncResourceCallback<T> callback) {
			@SuppressWarnings("unchecked")
			ObtainResourceTask<T> instance = SynchronizedPools.obtain(ObtainResourceTask.class);
			instance.context = context;
			instance.resourceReference = resourceReference;
			instance.callback = callback;
			Application.ASYNC_EXECUTOR.submit(instance);
		}

		@Override
		public Void call() throws Exception {
			resourceReference.obtain(callback);
			return null;
		}

		@Override
		public void handleResource(T resource) {
			context.addObtainedResource(resource, resourceReference);
			callback.handleResource(resource);
			SynchronizedPools.free(this);
		}

		@Override
		public void handleException(Throwable throwable) {
			callback.handleException(throwable);
			SynchronizedPools.free(this);
		}

		@Override
		public void handleProgress(float progress) {
			callback.handleProgress(progress);
		}

		@Override
		public void reset() {
			context = null;
			resourceReference = null;
			callback = null;
		}
	}

	private static class ObtainedResource implements Poolable {
		int count;
		ResourceReference<?> reference;

		static ObtainedResource getInstance() {
			return SynchronizedPools.obtain(ObtainedResource.class);
		}

		@Override
		public void reset() {
			count = 0;
			reference = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}

	private static class ConcurrentResolver implements Poolable {
		private ResourceContext context;
		private ResourceMap resourceMap;
		private AsyncResourceCallback<ResourceMap> callback;

		public static void resolve(ResourceContext context, IntArray resourceIds,
				AsyncResourceCallback<ResourceMap> callback) {
			ConcurrentResolver resolver = SynchronizedPools.obtain(ConcurrentResolver.class);
			resolver.context = context;
			// TODO resourceMap is never freed
			resolver.resourceMap = ResourceMap.obtain(context, resourceIds);
			resolver.callback = callback;
			resolver.resolve();
			SynchronizedPools.free(resolver);
		}

		private void resolve() {
			try {
				resolveSafely();
			} catch (Exception exception) {
				handleException(exception);
			}
		}

		private void resolveSafely() {
			int requestedResourcesCount = resourceMap.getRequestedResourcesCount();
			if (requestedResourcesCount == 0) {
				handleResource();
				return;
			}

			for (int i = 0; i < requestedResourcesCount; i++) {
				int resourceId = resourceMap.getRequestedResourceId(i);

				try {
					resolve(requestedResourcesCount, resourceId);
				} catch (Exception exception) {
					handleException(exception);
					return;
				}
			}

			handleResource();
		}

		private void handleResource() {
			try {
				callback.handleResource(resourceMap);
			} catch (Exception ignored) {
			}
		}

		private void resolve(int requestedResourcesCount, int resourceId) {
			Object resolvedResource = context.obtainResource(resourceId);
			resourceMap.addResolvedResource(resourceId, resolvedResource);
			int resolvedResourcesCount = resourceMap.resolvedResourcesCount;
			float proportionalProgress = resolvedResourcesCount == 0 ? 0
					: resolvedResourcesCount / requestedResourcesCount;
			callback.handleProgress(proportionalProgress);
		}

		private void handleException(Exception exception) {
			try {
				context.rollback(resourceMap);
				callback.handleException(exception);
				Gdx.app.debug(ConcurrentResolver.class.getName(), exception.toString());
			} catch (Exception ignored) {
			}
		}

		@Override
		public void reset() {
			context = null;
			resourceMap = null;
			callback = null;
		}
	}

	private static class AsyncResolver implements Poolable {
		private ResourceContext context;
		private ResourceMap resourceMap;
		private AsyncResourceCallback<ResourceMap> callback;

		private boolean consistent = true;
		private FloatArray progressArray = new FloatArray();

		public static void resolve(ResourceContext context, IntArray resourceIds,
				AsyncResourceCallback<ResourceMap> callback) {
			AsyncResolver resolver = SynchronizedPools.obtain(AsyncResolver.class);
			resolver.context = context;
			resolver.resourceMap = ResourceMap.obtain(context, resourceIds);
			resolver.callback = callback;
			resolver.resolve();
		}

		public void resolve() {
			try {
				resolveSafely();
			} catch (Exception exception) {
				addException(exception);
			}
		}

		public void resolveSafely() {
			int requestedResourcesCount = resourceMap.getRequestedResourcesCount();
			if (requestedResourcesCount == 0) {
				return;
			}

			for (int i = 0; i < requestedResourcesCount; i++) {
				progressArray.add(0);
				int resourceId = resourceMap.getRequestedResourceId(i);
				context.obtainResourceAsync(resourceId, InitCallback.obtain(i, resourceId, this));
			}
		}

		private synchronized void handleProgress(int index, float progress) {
			if (consistent) {
				progressArray.set(index, progress);
				updateProgress();
			}
		}

		private void updateProgress() {
			int size = progressArray.size;
			float totalProgress = 0;
			for (int i = 0; i < size; i++) {
				totalProgress += progressArray.get(i);
			}

			float proportionalProgress = totalProgress == 0 ? 0 : totalProgress / size;
			callback.handleProgress(proportionalProgress);
		}

		private synchronized void addResource(int index, int resourceId, Object resource) {
			resourceMap.addResolvedResource(resourceId, resource);

			if (consistent) {
				progressArray.set(index, 1);
				updateProgress();
			}

			finalizeIfFinished();
		}

		private void finalizeIfFinished() {
			int resolvedResourcesCount = resourceMap.resolvedResourcesCount;
			int requestedResourcesCount = resourceMap.getRequestedResourcesCount();
			if (requestedResourcesCount == resolvedResourcesCount) {
				if (consistent) {
					callback.handleResource(resourceMap);
				} else {
					context.rollback(resourceMap);
				}

				SynchronizedPools.free(this);
			}
		}

		private synchronized void addException(Throwable exception) {
			if (consistent) {
				consistent = false;
				callback.handleException(exception);
			}

			finalizeIfFinished();
		}

		@Override
		public void reset() {
			context = null;
			resourceMap = null;
			callback = null;

			consistent = true;
			progressArray.clear();
		}

		private static class InitCallback implements AsyncResourceCallback<Object>, Poolable {
			int index;
			int resourceId;
			private AsyncResolver resolver;

			public static InitCallback obtain(int index, int resourceId, AsyncResolver resolver) {
				InitCallback instance = SynchronizedPools.obtain(InitCallback.class);
				instance.index = index;
				instance.resourceId = resourceId;
				instance.resolver = resolver;
				return instance;
			}

			@Override
			public void handleResource(Object resource) {
				resolver.addResource(index, resourceId, resource);
				SynchronizedPools.free(this);
			}

			@Override
			public void handleException(Throwable exception) {
				resolver.addException(exception);
				SynchronizedPools.free(this);
			}

			@Override
			public void handleProgress(float progress) {
				resolver.handleProgress(index, progress);
			}

			@Override
			public void reset() {
				resolver = null;
			}
		}
	}
}
