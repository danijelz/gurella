package com.gurella.engine.base.model;

import com.gurella.engine.base.serialization.Input;

public class CreationContext {
	private Input input;
	private CopyContext context;
	
	public <T> T deserialize(Class<T> expectedType, T template, Input input) {
		
	}
}
