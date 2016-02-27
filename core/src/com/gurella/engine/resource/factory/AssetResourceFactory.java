package com.gurella.engine.resource.factory;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.resource.AssetResourceDescriptor;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.resource.ResourceContext;
import com.gurella.engine.resource.ResourceFactory;
import com.gurella.engine.utils.Reflection;

public class AssetResourceFactory<T> implements ResourceFactory<T> {
	private static final AssetRegistry assetRegistry = DisposablesService.add(new AssetRegistry());

	static {
		//EventService.addListener(ApplicationUpdateEvent.class, assetRegistry);
	}

	private AssetDescriptor<T> assetDescriptor;
	private AssetResourceDescriptor<T> assetResourceDescriptor;

	protected AssetResourceFactory() {
	}

	public AssetResourceFactory(String fileName, Class<T> assetType) {
		this.assetDescriptor = new AssetDescriptor<T>(fileName, assetType);
	}

	public AssetResourceFactory(FileHandle file, Class<T> assetType) {
		this.assetDescriptor = new AssetDescriptor<T>(file, assetType);
	}

	public AssetResourceFactory(AssetDescriptor<T> assetDescriptor) {
		this.assetDescriptor = assetDescriptor;
	}

	public AssetResourceFactory(AssetResourceDescriptor<T> assetResourceDescriptor) {
		this.assetResourceDescriptor = assetResourceDescriptor;
	}

	@Override
	public IntArray getDependentResourceIds(ResourceContext context) {
		return null;
	}

	@Override
	public Class<T> getResourceType() {
		return assetDescriptor.type;
	}

	public String getFileName() {
		return assetDescriptor.fileName;
	}

	@Override
	public T create(DependencyMap dependencies) {
		T resource = assetRegistry.load(getAssetDescriptor());
		while (resource == null) {
			resource = assetRegistry.get(getAssetDescriptor());
			try {
				synchronized (this) {
					wait(5);
				}
			} catch (InterruptedException ignored) {
			}
		}
		return resource;
	}

	private AssetDescriptor<T> getAssetDescriptor() {
		if (assetDescriptor == null) {
			assetDescriptor = assetResourceDescriptor == null ? assetDescriptor
					: assetResourceDescriptor.getAssetDescriptor();
		}
		return assetDescriptor;
	}

	@Override
	public void init(T resource, DependencyMap dependencies) {
	}

	public void unload() {
		assetRegistry.unload(assetDescriptor);
	}

	@Override
	public synchronized void dispose() {
		unload();
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String fileName = jsonData.getString("fileName");
		String type = jsonData.getString("type");
		@SuppressWarnings("unchecked")
		Class<T> assetType = (Class<T>) Reflection.forNameSilently(type);
		assetDescriptor = new AssetDescriptor<T>(fileName, assetType);
	}

	@Override
	public void write(Json json) {
		json.writeValue("fileName", assetDescriptor.fileName, String.class);
		json.writeValue("type", assetDescriptor.type.getName(), String.class);
	}
}
