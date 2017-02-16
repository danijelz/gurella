package com.gurella.engine.asset.loader.i18nbundle;

import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class I18NBundleLoader extends BaseAssetLoader<I18NBundle, I18NBundleProperties> {
	@Override
	public Class<I18NBundleProperties> getPropertiesType() {
		return I18NBundleProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, I18NBundleProperties properties) {
		Locale locale = properties == null ? Locale.getDefault() : properties.locale;
		String encoding = properties == null ? null : properties.encoding;

		I18NBundle i18NBundle;
		if (encoding == null) {
			i18NBundle = I18NBundle.createBundle(assetFile, locale);
		} else {
			i18NBundle = I18NBundle.createBundle(assetFile, locale, encoding);
		}

		put(assetFile, i18NBundle);
	}

	@Override
	public I18NBundle finish(DependencySupplier provider, FileHandle assetFile, I18NBundleProperties properties) {
		return remove(assetFile);
	}
}
