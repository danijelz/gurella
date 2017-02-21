package com.gurella.engine.asset;

import static com.badlogic.gdx.graphics.Pixmap.Format.Alpha;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;

class MockAssetManager extends AssetManager {
	private static final GlContextInvalidatedEvent glContextInvalidatedEvent = new GlContextInvalidatedEvent();
	private static final String listenerTextureFileName = MockAssetManager.class.getName()
			+ "______listenerTexture______";

	private Texture listenerTexture;

	MockAssetManager() {
		super(null, false);
		listenerTexture = new Texture(new PixmapTextureData(new Pixmap(1, 1, Alpha), Alpha, false, true, true));
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
		if (listenerTextureFileName.equals(fileName)) {
			EventService.post(glContextInvalidatedEvent);
		}
	}

	@Override
	public synchronized <T> boolean containsAsset(T asset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized <T> String getAssetFileName(T asset) {
		return asset == listenerTexture ? listenerTextureFileName : "dummy";
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

	@Override
	public synchronized void dispose() {
		listenerTexture.dispose();
		listenerTexture = null;
	}
	
	private static final class GlContextInvalidatedEvent implements Event<GlContextInvalidatedListener> {
		@Override
		public void dispatch(GlContextInvalidatedListener subscriber) {
			subscriber.onGlContextInvalidated();
		}

		@Override
		public Class<GlContextInvalidatedListener> getSubscriptionType() {
			return GlContextInvalidatedListener.class;
		}
	}
}
