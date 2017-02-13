package com.gurella.engine.asset.loader.pixmap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PixmapLoader implements AssetLoader<Pixmap, PixmapProperties> {
	private Pixmap pixmap;

	@Override
	public Class<PixmapProperties> getPropertiesType() {
		return PixmapProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {

	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, PixmapProperties properties) {
		pixmap = new Pixmap(file);
	}

	@Override
	public Pixmap finish(DependencySupplier provider, FileHandle file, PixmapProperties properties) {
		return pixmap;
	}
}
