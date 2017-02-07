package com.gurella.engine.asset.loader.pixmap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PixmapLoader implements AssetLoader<Pixmap, Pixmap, PixmapProperties> {
	@Override
	public Class<PixmapProperties> getAssetPropertiesType() {
		return PixmapProperties.class;
	}

	@Override
	public Pixmap init(DependencyCollector collector, FileHandle assetFile) {
		return new Pixmap(assetFile);
	}

	@Override
	public Pixmap processAsync(DependencySupplier provider, FileHandle file, Pixmap asyncData,
			PixmapProperties properties) {
		return asyncData;
	}

	@Override
	public Pixmap finish(DependencySupplier provider, FileHandle file, Pixmap asyncData, PixmapProperties properties) {
		return asyncData;
	}
}
