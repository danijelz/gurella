package com.gurella.engine.metatype;

import java.util.Locale;

import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class LocaleMetaType implements MetaType<Locale> {
	public static final LocaleMetaType instance = new LocaleMetaType();

	private LocaleMetaType() {
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
	public ImmutableArray<Property<?>> getProperties() {
		return ImmutableArray.empty();
	}

	@Override
	public <P> Property<P> getProperty(String name) {
		return null;
	}

	@Override
	public void serialize(Locale value, Object template, Output output) {
		if (Values.isEqual(value, template)) {
			return;
		} else if (value == null) {
			output.writeNull();
		} else {
			String language = value.getLanguage();
			output.writeStringProperty("language", language);

			String country = value.getCountry();
			if (Values.isNotBlank(country)) {
				output.writeStringProperty("country", country);
			}

			String variant = value.getVariant();
			if (Values.isNotBlank(language)) {
				output.writeStringProperty("variant", variant);
			}
		}
	}

	@Override
	public Locale deserialize(Object template, Input input) {
		if (!input.isValuePresent()) {
			return (Locale) template;
		} else if (input.isNull()) {
			return null;
		} else {
			String language = input.readStringProperty("language");
			String country = input.hasProperty("country") ? input.readStringProperty("country") : "";
			String variant = input.hasProperty("variant") ? input.readStringProperty("variant") : "";
			return new Locale(language, country, variant);
		}
	}

	@Override
	public Locale copy(Locale original, CopyContext context) {
		return original;
	}
}
