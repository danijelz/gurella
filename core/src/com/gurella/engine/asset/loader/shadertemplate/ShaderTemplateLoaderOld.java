package com.gurella.engine.asset.loader.shadertemplate;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.shader.parser.ShaderTemplateParser;
import com.gurella.engine.graphics.render.shader.template.ShaderTemplate;
import com.gurella.engine.utils.Values;

public class ShaderTemplateLoaderOld
		extends SynchronousAssetLoader<ShaderTemplate, AssetLoaderParameters<ShaderTemplate>> {
	private ShaderTemplateParser parser = new ShaderTemplateParser();
	private ShaderTemplate result;

	public ShaderTemplateLoaderOld(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		result = parser.parse(file.reader());
		parser.reset();
		return Values.cast(result.getDependenciesOld());
	}

	@Override
	public ShaderTemplate load(AssetManager assetManager, String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		result.initDependencies(assetManager);
		ShaderTemplate template = result;
		result = null;
		return template;
	}
}
