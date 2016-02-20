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
import com.gurella.engine.asset.AssetDatabase;

public class AssetDatabaseTest extends ApplicationAdapter {
	private AssetDatabase database;

	@Override
	public void create() {
		super.create();
		InternalFileHandleResolver resolver = new InternalFileHandleResolver();
		database = new AssetDatabase(resolver);
		database.setLoader(TestAssetType1.class, new TestAssetType1Loader(resolver));
		database.setLoader(TestAssetType2.class, new TestAssetType2Loader(resolver));
		database.load("TestAssetType1", TestAssetType1.class);
		database.reload("TestAssetType1", null, 0);
	}
	
	@Override
	public void render() {
		super.render();
		database.update();
		
		if(!database.isLoaded("TestAssetType1")) {
			return;
		}
		
		database.getDiagnostics();
		
		database.reload("TestAssetType1", null, 0);
		// database.load("TestAssetType2", TestAssetType2.class);
		// database.unload("TestAssetType1");
		database.getDiagnostics();
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
		@SuppressWarnings("rawtypes")
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
		@SuppressWarnings("rawtypes")
		public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
				AssetLoaderParameters<TestAssetType2> parameter) {
			return null;
		}
	}
}
