package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.Values;

public class ShaderTemplateLoader
		extends SynchronousAssetLoader<ShaderTemplate, AssetLoaderParameters<ShaderTemplate>> {
	private ShaderTemplate result;

	public ShaderTemplateLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		result = new ShaderTemplate(file);
		Array<AssetDescriptor> dependencies = Values.cast(result.getDependencies());
		return dependencies;
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
