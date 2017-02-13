package com.gurella.engine.asset;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Clipboard;
import com.gurella.engine.asset.descriptor.AssetDescriptor;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.asset.resolver.FileHandleFactory;
import com.gurella.engine.asset.resolver.FileHandleResolver;
import com.gurella.engine.async.SimpleAsyncCallback;
import com.gurella.engine.utils.factory.ReflectionFactory;

public class AssetManagerLoadingTest {
	public static void main(String[] args) {
		Gdx.app = new TestApp();

		AssetDescriptor<TestAsset1> descriptor1 = new AssetDescriptor<TestAsset1>(TestAsset1.class, false, false);
		descriptor1.registerLoaderFactory(new ReflectionFactory<TestAsset1Loader>(TestAsset1Loader.class), "t1");
		AssetDescriptors.register(descriptor1);

		AssetDescriptor<TestAsset2> descriptor2 = new AssetDescriptor<TestAsset2>(TestAsset2.class, false, false);
		descriptor2.registerLoaderFactory(new ReflectionFactory<TestAsset2Loader>(TestAsset2Loader.class), "t2");
		AssetDescriptors.register(descriptor2);

		AssetsManager manager = new AssetsManager();
		manager.registerResolver(new TestResolver());
		
		SimpleAsyncCallback<TestAsset2> callback2 = new SimpleAsyncCallback<TestAsset2>();
		manager.loadAsync(callback2, "TestAsset2/1.t2", FileType.Internal, TestAsset2.class, 0);

		SimpleAsyncCallback<TestAsset1> callback = new SimpleAsyncCallback<TestAsset1>();
		manager.loadAsync(callback, "TestAsset1/1.t1", FileType.Internal, TestAsset1.class, 0);
		
		manager.finishLoading();
		System.out.println("Final: " + callback.getState() + " " + callback2.getState());
		System.out.println(manager.getDiagnostics());
	}

	private static class TestAsset1 {
	}

	private static class TestAsset2 {
	}

	private static class TestResolver implements FileHandleResolver {
		@Override
		public boolean accepts(AssetId assetId) {
			return true;
		}

		@Override
		public FileHandle resolve(FileHandleFactory factory, AssetId assetId) {
			return new FileHandle(assetId.fileName);
		}
	}

	private static class TestAsset1Loader implements AssetLoader<TestAsset1, AssetProperties> {
		@Override
		public Class<AssetProperties> getPropertiesType() {
			return null;
		}

		@Override
		public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
			collector.addDependency("TestAsset2/1.t2", FileType.Internal, TestAsset2.class);
			collector.addDependency("TestAsset2/2.t2", FileType.Internal, TestAsset2.class);
			/*for (int i = 0; i < 1000; i++) {
				assetFile.path();
			}*/
		}

		@Override
		public void processAsync(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
			supplier.getDependency("TestAsset2/1.t2", FileType.Internal, TestAsset2.class, null);
			supplier.getDependency("TestAsset2/2.t2", FileType.Internal, TestAsset2.class, null);
			for (int i = 0; i < 10000; i++) {
				assetFile.path();
			}
		}

		@Override
		public TestAsset1 finish(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
			return new TestAsset1();
		}
	}

	private static class TestAsset2Loader implements AssetLoader<TestAsset2, AssetProperties> {
		int i = 0;
		
		@Override
		public Class<AssetProperties> getPropertiesType() {
			return null;
		}

		@Override
		public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
			if (i++ == 1) {
				throw new NullPointerException();
			}
		}

		@Override
		public void processAsync(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
		}

		@Override
		public TestAsset2 finish(DependencySupplier supplier, FileHandle assetFile, AssetProperties properties) {
			return new TestAsset2();
		}
	}

	private static class TestApp implements Application {
		@Override
		public ApplicationListener getApplicationListener() {
			return null;
		}

		@Override
		public Graphics getGraphics() {
			return null;
		}

		@Override
		public Audio getAudio() {
			return null;
		}

		@Override
		public Input getInput() {
			return null;
		}

		@Override
		public Files getFiles() {
			return null;
		}

		@Override
		public Net getNet() {
			return null;
		}

		@Override
		public void log(String tag, String message) {

		}

		@Override
		public void log(String tag, String message, Throwable exception) {

		}

		@Override
		public void error(String tag, String message) {

		}

		@Override
		public void error(String tag, String message, Throwable exception) {

		}

		@Override
		public void debug(String tag, String message) {

		}

		@Override
		public void debug(String tag, String message, Throwable exception) {

		}

		@Override
		public void setLogLevel(int logLevel) {

		}

		@Override
		public int getLogLevel() {
			return 0;
		}

		@Override
		public void setApplicationLogger(ApplicationLogger applicationLogger) {

		}

		@Override
		public ApplicationLogger getApplicationLogger() {
			return null;
		}

		@Override
		public ApplicationType getType() {
			return null;
		}

		@Override
		public int getVersion() {
			return 0;
		}

		@Override
		public long getJavaHeap() {
			return 0;
		}

		@Override
		public long getNativeHeap() {
			return 0;
		}

		@Override
		public Preferences getPreferences(String name) {
			return null;
		}

		@Override
		public Clipboard getClipboard() {
			return null;
		}

		@Override
		public void postRunnable(Runnable runnable) {

		}

		@Override
		public void exit() {

		}

		@Override
		public void addLifecycleListener(LifecycleListener listener) {

		}

		@Override
		public void removeLifecycleListener(LifecycleListener listener) {

		}
	}
}
