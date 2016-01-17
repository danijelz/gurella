package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class ClassModel implements Model<Class<?>> {
	private static final String name = "Class";
	private static final String typeNameProperty = "typeName";

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
		if (context == null) {
			return null;
		}

		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			return context.template();
		} else if (serializedValue.isNull()) {
			return null;
		} else {
			JsonValue typeValue = serializedValue.child;
			return ReflectionUtils.forName(typeValue.asString());
		}
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
		if (object == null) {
			archive.writeValue(null, null);
		} else {
			archive.writeObjectStart(object, knownType);
			archive.writeValue(typeNameProperty, object.getName(), String.class);
			archive.writeObjectEnd();
		}
	}
}
