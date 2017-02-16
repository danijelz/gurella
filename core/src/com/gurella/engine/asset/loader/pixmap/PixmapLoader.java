package com.gurella.engine.asset.loader.pixmap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PixmapLoader extends BaseAssetLoader<Pixmap, PixmapProperties> {
	@Override
	public Class<PixmapProperties> getPropertiesType() {
		return PixmapProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {

	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, PixmapProperties properties) {
		put(assetFile, new Pixmap(assetFile));
	}

	@Override
	public Pixmap finish(DependencySupplier provider, FileHandle assetFile, PixmapProperties properties) {
		return remove(assetFile);
	}
}
