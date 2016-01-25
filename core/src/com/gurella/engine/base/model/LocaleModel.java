package com.gurella.engine.base.model;

import java.util.Locale;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Input;
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
	public void serialize(Locale value, Object template, Output output) {
		if (ValueUtils.isEqual(value, template)) {
			return;
		} else if (value == null) {
			output.writeNull();
		} else {
			String language = value.getLanguage();
			output.writeStringProperty("language", language);

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

	@Override
	public Locale deserialize(Input input) {
		String language = input.readStringProperty("language");
		String country = input.hasProperty("country") ? input.readStringProperty("country") : "";
		String variant = input.hasProperty("variant") ? input.readStringProperty("variant") : "";
		return new Locale(language, country, variant);
	}

	@Override
	public Locale copy(Locale original, CopyContext context) {
		return original;
	}
}
