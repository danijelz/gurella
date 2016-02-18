package com.gurella.engine.base.resource;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

class MockAssetManager extends AssetManager {
	MockAssetManager() {
		super(null, false);
	}

	@Override
	public FileHandleResolver getFileHandleResolver() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> T get(String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> T get(String fileName, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> Array<T> getAll(Class<T> type, Array<T> out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> T get(AssetDescriptor<T> assetDescriptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void unload(String fileName) {
	}

	@Override
	public synchronized <T> boolean containsAsset(T asset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> String getAssetFileName(T asset) {
		return "dummy";
	}

	@Override
	public synchronized boolean isLoaded(String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized boolean isLoaded(String fileName, Class type) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <T> AssetLoader getLoader(Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <T> AssetLoader getLoader(Class<T> type, String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> void load(String fileName, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized void load(AssetDescriptor desc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean update() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean update(int millis) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void finishLoading() {
	}

	@Override
	public void finishLoadingAsset(String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected <T> void addAsset(String fileName, Class<T> type, T asset) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void taskFailed(AssetDescriptor assetDesc, RuntimeException ex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type,
			AssetLoader<T, P> loader) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix,
			AssetLoader<T, P> loader) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int getLoadedAssets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int getQueuedAssets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized float getProgress() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void setErrorListener(AssetErrorListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void dispose() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLogger(Logger logger) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized int getReferenceCount(String fileName) {
		return 1;
	}

	@Override
	public synchronized void setReferenceCount(String fileName, int refCount) {
	}

	@Override
	public synchronized String getDiagnostics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Array<String> getAssetNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Array<String> getDependencies(String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized Class getAssetType(String fileName) {
		throw new UnsupportedOperationException();
	}
}
