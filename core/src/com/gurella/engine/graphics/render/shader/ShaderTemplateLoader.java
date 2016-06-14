package com.gurella.engine.graphics.render.shader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ShaderTemplateLoader extends AsynchronousAssetLoader<ShaderTemplate, AssetLoaderParameters<ShaderTemplate>>{
	public ShaderTemplateLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ShaderTemplate loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<ShaderTemplate> parameter) {
		
		// TODO Auto-generated method stub
		return null;
	}
}
