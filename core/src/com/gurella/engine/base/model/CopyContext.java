package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class CopyContext implements Poolable {
	private ArrayExt<Object> objectStack = new ArrayExt<Object>();
	private ArrayExt<Object> copiedObjectStack = new ArrayExt<Object>();
	private IdentityMap<Object, Object> copiedObjects = new IdentityMap<Object, Object>();

	@Override
	public void reset() {
		objectStack.clear();
		copiedObjects.clear();
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

		copiedObjectStack.add(original);
		copiedObjects.put(original, null);
		Model<T> model = Models.getModel(original);
		duplicate = model.copy(original, this);
		copiedObjects.put(original, duplicate);
		copiedObjectStack.pop();
		return duplicate;
	}

	public <T> T copyProperties(T source, T target) {
		if (source == null || target == null) {
			return null;
		}

		copiedObjectStack.add(source);
		copiedObjects.put(source, target);
		objectStack.add(target);
		Model<T> model = Models.getModel(source);
		ImmutableArray<Property<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).copy(source, target, this);
		}
		copiedObjectStack.pop();
		objectStack.pop();
		return target;
	}
}
