package com.gurella.engine.asset.loader.shadertemplate;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.graphics.render.shader.parser.ShaderTemplateParser;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.utils.ImmutableArray;

public class ShaderTemplateLoader extends BaseAssetLoader<ShaderTemplate, AssetProperties> {
	private ShaderTemplateParser parser = new ShaderTemplateParser();

	@Override
	public Class<AssetProperties> getPropertiesType() {
		return null;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		ShaderTemplate shaderTemplate = parser.parse(assetFile.reader());
		FileType fileType = assetFile.type();
		ImmutableArray<String> dependencies = shaderTemplate.dependencies;
		for (int i = 0, n = dependencies.size(); i < n; i++) {
			String dependency = dependencies.get(i);
			collector.addDependency(dependency, fileType, ShaderTemplate.class);
		}
		put(assetFile, shaderTemplate);
	}

	@Override
	public void processAsync(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		ShaderTemplate shaderTemplate = get(assetFile);
		FileType fileType = assetFile.type();
		ImmutableArray<String> dependencies = shaderTemplate.dependencies;
		for (int i = 0, n = dependencies.size(); i < n; i++) {
			String dependency = dependencies.get(i);
			ShaderTemplate template = supplier.getDependency(dependency, fileType, ShaderTemplate.class, null);
			template.addPieces(shaderTemplate);
		}
	}

	@Override
	public ShaderTemplate finish(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		return remove(assetFile);
	}
}
