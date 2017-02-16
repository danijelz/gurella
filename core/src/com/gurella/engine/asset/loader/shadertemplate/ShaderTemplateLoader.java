package com.gurella.engine.asset.loader.shadertemplate;

import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.graphics.render.shader.parser.ShaderTemplateParser;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;

public class ShaderTemplateLoader extends BaseAssetLoader<ShaderTemplate, AssetProperties> {
	private ShaderTemplateParser parser = new ShaderTemplateParser();

	@Override
	public Class<AssetProperties> getPropertiesType() {
		return null;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		ShaderTemplate shaderTemplate = parser.parse(assetFile.reader());
		shaderTemplate.collectDependencies(collector);
		put(assetFile, shaderTemplate);
	}

	@Override
	public void processAsync(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		ShaderTemplate shaderTemplate = get(assetFile);
		shaderTemplate.initDependencies(supplier);
	}

	@Override
	public ShaderTemplate finish(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		return remove(assetFile);
	}
}
