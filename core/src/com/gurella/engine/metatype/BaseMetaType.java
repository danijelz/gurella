package com.gurella.engine.metatype;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

//TODO unused
public abstract class BaseMetaType<T> implements MetaType<T> {
	// TODO private final MetaType<? super T> superType;
	private final Class<T> type;
	private final String name;

	private final boolean innerClass;
	private final Constructor constructor;

	private final ArrayExt<Property<?>> properties = new ArrayExt<Property<?>>();
	private final ObjectMap<String, Property<?>> propertiesByName = new ObjectMap<String, Property<?>>();

	public BaseMetaType(Class<T> type) {
		this.type = type;

		MetaTypeDescriptor resourceAnnotation = Reflection.getAnnotation(type, MetaTypeDescriptor.class);
		if (resourceAnnotation == null) {
			name = type.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			name = Values.isBlank(descriptiveName) ? type.getSimpleName() : descriptiveName;
		}

		innerClass = Reflection.isInnerClass(type);
		constructor = null; // TODO resolve constructor
	}

	protected void registerReflectionProperty(String name) {
		registerProperty(ReflectionProperty.newInstance(this, name));
	}

	protected void registerProperty(Property<?> property) {
		properties.add(property);
		propertiesByName.put(property.getName(), property);
	}

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
	public <P> Property<P> getProperty(String name) {
		@SuppressWarnings("unchecked")
		Property<P> casted = (Property<P>) propertiesByName.get(name);
		return casted;
	}
}
