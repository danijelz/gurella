package com.gurella.engine.base.model;

import java.util.Locale;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ValueUtils;

public class LocaleModel implements Model<Locale> {
	public static final LocaleModel instance = new LocaleModel();

	private LocaleModel() {
	}

	@Override
	public Class<Locale> getType() {
		return Locale.class;
	}

	@Override
	public String getName() {
		return Locale.class.getName();
	}

	@Override
	public Locale createInstance(InitializationContext context) {
		if (context == null) {
			return null;
		}

		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			Locale template = context.template();
			return template == null ? null : (Locale) template.clone();
		} else if (serializedValue.isNull()) {
			return null;
		} else {
			return new Locale(serializedValue.getString("language", ""), serializedValue.getString("country", ""),
					serializedValue.getString("variant", ""));
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
	public void serialize(Locale value, Class<?> knownType, Archive archive) {
		if (value == null) {
			archive.writeValue(null, null);
		} else {
			archive.writeObjectStart(value, value.getClass());
			String language = value.getLanguage();
			if (ValueUtils.isNotEmpty(language)) {
				archive.writeValue("language", language, String.class);
			}

			String country = value.getCountry();
			if (ValueUtils.isNotEmpty(country)) {
				archive.writeValue("country", country, String.class);
			}

			String variant = value.getVariant();
			if (ValueUtils.isNotEmpty(language)) {
				archive.writeValue("variant", variant, String.class);
			}
			archive.writeObjectEnd();
		}
	}

	@Override
	public void serialize(Locale value, Output output) {
		if (value == null) {
			output.writeNull();
		} else {
			String language = value.getLanguage();
			if (ValueUtils.isNotEmpty(language)) {
				output.writeStringProperty("language", language);
			}

			String country = value.getCountry();
			if (ValueUtils.isNotEmpty(country)) {
				output.writeStringProperty("country", country);
			}

			String variant = value.getVariant();
			if (ValueUtils.isNotEmpty(language)) {
				output.writeStringProperty("variant", variant);
			}
		}
	}
}
