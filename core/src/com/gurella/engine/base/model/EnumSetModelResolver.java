package com.gurella.engine.base.model;

import java.util.EnumSet;

import com.badlogic.gdx.utils.reflect.ClassReflection;

public class EnumSetModelResolver implements ModelResolver {
	public static final EnumSetModelResolver instance = new EnumSetModelResolver();

	private EnumSetModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (ClassReflection.isAssignableFrom(EnumSet.class, type)) {
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) EnumSetModel.instance;
			return casted;
		} else {
			return null;
		}
	}
}
