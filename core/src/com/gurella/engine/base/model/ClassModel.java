package com.gurella.engine.base.model;

import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

//TODO unused
public class ClassModel implements Model<Class<?>> {
	private static final String name = "class";

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Class<?>> getType() {
		return (Class) Class.class;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> createInstance(InitializationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initInstance(InitializationContext context) {
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return ImmutableArray.empty();
	}

	@Override
	public <P> Property<P> getProperty(String name) {
		return null;
	}

	@Override
	public void serialize(Class<?> object, Class<?> knownType, Archive archive) {
		// TODO Auto-generated method stub

	}
}
