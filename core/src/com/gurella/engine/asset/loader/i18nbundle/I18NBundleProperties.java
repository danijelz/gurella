package com.gurella.engine.asset.loader.i18nbundle;

import java.util.Locale;

import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.metatype.PropertyDescriptor;

public class I18NBundleProperties extends AssetProperties {
	@PropertyDescriptor(nullable = false)
	public final Locale locale;
	public final String encoding;

	I18NBundleProperties() {
		this.locale = Locale.getDefault();
		this.encoding = null;
	}

	public I18NBundleProperties(Locale locale, String encoding) {
		this.locale = locale;
		this.encoding = encoding;
	}
}
