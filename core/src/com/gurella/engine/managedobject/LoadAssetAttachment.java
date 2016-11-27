package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.pool.PoolService;

public class LoadAssetAttachment<T> extends Attachment<T> implements Poolable {
	static <T> LoadAssetAttachment<T> obtain(String fileName, Class<T> assetType) {
		@SuppressWarnings("unchecked")
		LoadAssetAttachment<T> attachment = PoolService.obtain(LoadAssetAttachment.class);
		attachment.value = AssetService.load(fileName, assetType);
		return attachment;
	}

	@Override
	protected void attach() {
	}

	@Override
	protected void detach() {
	}

	@Override
	public void reset() {
		AssetService.unload(value);
		value = null;
	}
}
