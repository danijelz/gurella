package com.gurella.engine.asset.manager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class AssetManagerTest extends ApplicationAdapter {
	private com.gurella.engine.asset.manager.AssetManager manager;

	@Override
	public void create() {
		super.create();
		InternalFileHandleResolver resolver = new InternalFileHandleResolver();
		manager = new com.gurella.engine.asset.manager.AssetManager(resolver);
		manager.setLoader(TestAssetType1.class, new TestAssetType1Loader(resolver));
		manager.setLoader(TestAssetType2.class, new TestAssetType2Loader(resolver));
		
		manager.load("TestAssetType1", TestAssetType1.class);
	}
	
	@Override
	public void render() {
		super.render();
		manager.update();
		
		if(!manager.isLoaded("TestAssetType1")) {
			return;
		}
		
		manager.getDiagnostics();
		
		manager.unload("TestAssetType1");
		// manager.load("TestAssetType2", TestAssetType2.class);
		manager.getDiagnostics();
	}

	public static class TestAssetType1 {

	}

	public static class TestAssetType2 {

	}

	public static class TestAssetType1Loader
			extends AsynchronousAssetLoader<TestAssetType1, AssetLoaderParameters<TestAssetType1>> {
		TestAssetType1 testAsset;

		public TestAssetType1Loader(FileHandleResolver resolver) {
			super(resolver);
		}

		@Override
		public void loadAsync(AssetManager manager, String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType1> parameter) {
			testAsset = new TestAssetType1();
		}

		@Override
		public TestAssetType1 loadSync(AssetManager manager, String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType1> parameter) {
			TestAssetType1 temp = testAsset;
			testAsset = null;
			return temp;
		}

		@Override
		public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType1> parameter) {
			return Array.<AssetDescriptor> with(
					new AssetDescriptor<TestAssetType2>("TestAssetType2", TestAssetType2.class));
		}
	}

	public static class TestAssetType2Loader
			extends AsynchronousAssetLoader<TestAssetType2, AssetLoaderParameters<TestAssetType2>> {
		TestAssetType2 testAsset;

		public TestAssetType2Loader(FileHandleResolver resolver) {
			super(resolver);
		}

		@Override
		public void loadAsync(AssetManager manager, String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType2> parameter) {
			testAsset = new TestAssetType2();
		}

		@Override
		public TestAssetType2 loadSync(AssetManager manager, String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType2> parameter) {
			TestAssetType2 temp = testAsset;
			testAsset = null;
			return temp;
		}

		@Override
		public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType2> parameter) {
			return null;
		}
	}
}
