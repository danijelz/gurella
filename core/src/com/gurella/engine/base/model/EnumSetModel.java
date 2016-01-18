package com.gurella.engine.base.model;

import java.util.EnumSet;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class EnumSetModel implements Model<EnumSet<?>> {
	public static final EnumSetModel instance = new EnumSetModel();

	private EnumSetModel() {
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class<EnumSet<?>> getType() {
		return (Class) EnumSet.class;
	}

	@Override
	public String getName() {
		return EnumSet.class.getName();
	}

	@Override
	public EnumSet<?> createInstance(InitializationContext context) {
		if (context == null) {
			return null;
		}

		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			EnumSet<?> template = context.template();
			return template == null ? null : template.clone();
		} else if (serializedValue.isNull()) {
			return null;
		} else {
			@SuppressWarnings("rawtypes")
			Class<Enum> enumType = ReflectionUtils.forName(serializedValue.getString("type"));
			@SuppressWarnings({ "unchecked", "rawtypes" })
			EnumSet enumSet = EnumSet.noneOf(enumType);

			Enum<?>[] constants = enumType.getEnumConstants();
			JsonValue values = serializedValue.get("values");
			for (JsonValue value = values.child; value != null; value = value.next) {
				enumSet.add(find(constants, value.asString()));
			}

			return enumSet;
		}
	}

	private static Enum<?> find(Enum<?>[] constants, String name) {
		for (int i = 0; i < constants.length; i++) {
			Enum<?> constant = constants[i];
			if (name.equals(constant.name())) {
				return constant;
			}
		}

		throw new GdxRuntimeException("Invalid enum name: " + name);
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
	public void serialize(EnumSet<?> value, Class<?> knownType, Archive archive) {
		if (value == null) {
			archive.writeValue(null, null);
		} else {
			archive.writeObjectStart(value, value.getClass());
			if (value.isEmpty()) {
				EnumSet<?> complement = EnumSet.complementOf(value);
				if (complement.isEmpty()) {
					throw new GdxRuntimeException("An EnumSet must have a defined Enum to be serialized.");
				}
				Enum<?> e = complement.iterator().next();
				archive.writeValue("type", e.getClass().getName(), String.class);
			} else {
				Enum<?> e = value.iterator().next();
				archive.writeValue("type", e.getClass().getName(), String.class);
			}
			archive.writeArrayStart("values");
			for (@SuppressWarnings("rawtypes")
			Enum e : value) {
				archive.writeValue(e.name(), String.class);
			}
			archive.writeArrayEnd();
			archive.writeObjectEnd();
		}
	}
}
