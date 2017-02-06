package com.gurella.engine.asset.loader.i18nbundle;

import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencyProvider;

public class I18NBundleLoader implements AssetLoader<I18NBundle, I18NBundle, I18NBundleProperties> {
	@Override
	public Class<I18NBundleProperties> getAssetPropertiesType() {
		return I18NBundleProperties.class;
	}

	@Override
	public I18NBundle init(DependencyCollector collector, FileHandle assetFile) {
		return null;
	}

	@Override
	public I18NBundle processAsync(DependencyProvider provider, FileHandle file, I18NBundle asyncData,
			I18NBundleProperties properties) {
		Locale locale = properties == null ? Locale.getDefault() : properties.locale;
		String encoding = properties == null ? null : properties.encoding;

		if (encoding == null) {
			return I18NBundle.createBundle(file, locale);
		} else {
			return I18NBundle.createBundle(file, locale, encoding);
		}
	}

	@Override
	public I18NBundle finish(DependencyProvider provider, FileHandle file, I18NBundle asyncData,
			I18NBundleProperties properties) {
		return asyncData;
	}
}
