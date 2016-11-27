package com.gurella.engine.serialization.json;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;

class ObjectInfo implements Poolable {
	int ordinal;
	Class<?> expectedType;
	Object object;
	Object template;

	public static ObjectInfo obtain(int ordinal, Class<?> expectedType, Object template, Object object) {
		ObjectInfo objectInfo = PoolService.obtain(ObjectInfo.class);
		objectInfo.ordinal = ordinal;
		objectInfo.expectedType = expectedType;
		objectInfo.template = template;
		objectInfo.object = object;
		return objectInfo;
	}

	public void free() {
		PoolService.free(this);
	}

	@Override
	public void reset() {
		ordinal = 0;
		expectedType = null;
		template = null;
		object = null;
	}
}