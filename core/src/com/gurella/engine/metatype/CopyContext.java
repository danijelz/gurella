package com.gurella.engine.metatype;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetId;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class CopyContext implements Poolable {
	private final ArrayExt<Object> objectStack = new ArrayExt<Object>();
	private final ArrayExt<Object> copiedObjectStack = new ArrayExt<Object>();
	private final IdentityMap<Object, Object> copiedObjects = new IdentityMap<Object, Object>();
	private final AssetId rootObjectAssetId = new AssetId();
	private final AssetId tempAssetId = new AssetId();

	public CopyContext() {
	}

	public CopyContext(AssetId rootObjectAssetId) {
		this.rootObjectAssetId.set(rootObjectAssetId);
	}

	public void init(AssetId rootObjectAssetId) {
		this.rootObjectAssetId.set(rootObjectAssetId);
	}

	public void init(Object rootObject) {
		AssetService.getAssetId(rootObject, rootObjectAssetId);
	}

	public void pushObject(Object duplicate) {
		objectStack.add(duplicate);
		if (copiedObjectStack.size > 0) {
			Object original = copiedObjectStack.peek();
			if (original.getClass() == duplicate.getClass()) {
				copiedObjects.put(original, duplicate);
			}
		}
	}

	public void popObject() {
		objectStack.pop();
	}

	public ImmutableArray<Object> getObjectStack() {
		return objectStack.immutable();
	}

	public <T> T copy(T original) {
		if (original == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		T duplicate = (T) copiedObjects.get(original);
		if (duplicate != null) {
			return duplicate;
		}

		if (isExternalAsset(original)) {
			return original;
		}

		copiedObjectStack.add(original);
		copiedObjects.put(original, null);
		MetaType<T> metaType = MetaTypes.getMetaType(original);
		duplicate = metaType.copy(original, this);
		copiedObjects.put(original, duplicate);
		copiedObjectStack.pop();
		return duplicate;
	}

	private <T> boolean isExternalAsset(T original) {
		AssetService.getAssetId(original, tempAssetId);
		return !tempAssetId.isEmpty() && tempAssetId.equalsFile(rootObjectAssetId);
	}

	public static <T> T copyObject(T original) {
		if (original == null) {
			return null;
		}

		CopyContext context = PoolService.obtain(CopyContext.class);
		try {
			context.init(original);
			return context.copy(original);
		} finally {
			PoolService.free(context);
		}

	}

	@Override
	public void reset() {
		objectStack.clear();
		copiedObjectStack.clear();
		copiedObjects.clear();
		rootObjectAssetId.reset();
		tempAssetId.reset();
	}
}
