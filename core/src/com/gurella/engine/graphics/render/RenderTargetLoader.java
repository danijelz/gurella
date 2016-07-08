package com.gurella.engine.graphics.render;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.gurella.engine.utils.Values;

public class RenderTargetLoader extends AsynchronousAssetLoader<RenderTarget, AssetLoaderParameters<RenderTarget>> {
	private static final Array<AssetDescriptor<?>> dependencies = new Array<AssetDescriptor<?>>();

	private Json json = new Json();
	private RenderTarget result;

	public RenderTargetLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<RenderTarget> parameter) {
		return Values.cast(dependencies);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<RenderTarget> parameter) {
		result = json.fromJson(RenderTarget.class, file);
	}

	@Override
	public RenderTarget loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<RenderTarget> parameter) {
		result.init();
		RenderTarget object = result;
		result = null;
		return object;
	}
}
