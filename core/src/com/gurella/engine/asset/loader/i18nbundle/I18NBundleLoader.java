package com.gurella.engine.asset.loader.i18nbundle;

import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class I18NBundleLoader implements AssetLoader<I18NBundle, I18NBundleProperties> {
	private I18NBundle i18NBundle;

	@Override
	public Class<I18NBundleProperties> getPropertiesType() {
		return I18NBundleProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, I18NBundleProperties properties) {
		Locale locale = properties == null ? Locale.getDefault() : properties.locale;
		String encoding = properties == null ? null : properties.encoding;

		if (encoding == null) {
			i18NBundle = I18NBundle.createBundle(file, locale);
		} else {
			i18NBundle = I18NBundle.createBundle(file, locale, encoding);
		}
	}

	@Override
	public I18NBundle finish(DependencySupplier provider, FileHandle file, I18NBundleProperties properties) {
		try {
			return i18NBundle;
		} finally {
			i18NBundle = null;
		}
	}
}
