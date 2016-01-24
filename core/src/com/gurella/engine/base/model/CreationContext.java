package com.gurella.engine.base.model;

import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;

public class CreationContext {
	private Input input;
	private CopyContext context;
	
	private ArrayExt<Object> templateStack = new ArrayExt<Object>();
	private ArrayExt<Object> objectStack = new ArrayExt<Object>();
	
	public void pushObject(Object object) {
		objectStack.add(object);
	}

	public void popObject() {
		objectStack.pop();
	}

	public ImmutableArray<Object> getObjectStack() {
		return objectStack.immutable();
	}
	
	public <T> T create(Class<T> expectedType, T template, Input input) {
		
	}
}
