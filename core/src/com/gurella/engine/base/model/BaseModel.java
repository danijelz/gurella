package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

//TODO unused
public abstract class BaseModel<T> implements Model<T> {
	private final Class<T> type;
	private final String name;
	
	private final boolean innerClass;
	private final Constructor constructor;

	private final ArrayExt<Property<?>> properties;
	private final ObjectMap<String, Property<?>> propertiesByName = new ObjectMap<String, Property<?>>();

	public BaseModel(Class<T> type) {
		this.type = type;

		ModelDescriptor resourceAnnotation = Reflection.getAnnotation(type, ModelDescriptor.class);
		if (resourceAnnotation == null) {
			name = type.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			name = Values.isBlank(descriptiveName) ? type.getSimpleName() : descriptiveName;
		}

		innerClass = Reflection.isInnerClass(type);
		constructor = null;
		properties = resolveProperties();
	}

	protected abstract ArrayExt<Property<?>> resolveProperties();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return properties.immutable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> Property<P> getProperty(String name) {
		return (Property<P>) propertiesByName.get(name);
	}
}
