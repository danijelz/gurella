package com.gurella.engine.metatype;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class CopyContext implements Poolable {
	private ArrayExt<Object> objectStack = new ArrayExt<Object>();
	private ArrayExt<Object> copiedObjectStack = new ArrayExt<Object>();
	private IdentityMap<Object, Object> copiedObjects = new IdentityMap<Object, Object>();

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
		// TODO check if value is external asset and return original
		if (original == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		T duplicate = (T) copiedObjects.get(original);
		if (duplicate != null) {
			return duplicate;
		}

		copiedObjectStack.add(original);
		copiedObjects.put(original, null);
		MetaType<T> metaType = MetaTypes.getMetaType(original);
		duplicate = metaType.copy(original, this);
		copiedObjects.put(original, duplicate);
		copiedObjectStack.pop();
		return duplicate;
	}

	public static <T> T copyObject(T original) {
		if (original == null) {
			return null;
		}

		CopyContext context = PoolService.obtain(CopyContext.class);
		T duplicate = context.copy(original);
		PoolService.free(context);
		return duplicate;
	}

	@Override
	public void reset() {
		objectStack.clear();
		copiedObjectStack.clear();
		copiedObjects.clear();
	}
}
