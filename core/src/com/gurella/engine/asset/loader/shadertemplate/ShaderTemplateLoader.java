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
	private ShaderTemplate result;

	@Override
	public Class<AssetProperties> getPropertiesType() {
		return null;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		result = parser.parse(assetFile.reader());
		// TODO Auto-generated method stub

	}

	@Override
	public void processAsync(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public ShaderTemplate finish(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		ShaderTemplate temp = result;
		result = null;
		return temp;
	}
}
