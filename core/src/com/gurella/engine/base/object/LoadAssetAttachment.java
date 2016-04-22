package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.pool.PoolService;

public class LoadAssetAttachment<T> extends Attachment<T> implements Poolable {
	static <T> LoadAssetAttachment<T> obtain(String fileName, Class<T> assetType) {
		@SuppressWarnings("unchecked")
		LoadAssetAttachment<T> attachment = PoolService.obtain(LoadAssetAttachment.class);
		attachment.value = ResourceService.load(fileName, assetType);
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
		ResourceService.unload(value);
		value = null;
	}
}
