package com.gurella.engine.asset.loader.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.serialization.json.PoolableJsonReader;

public class JsonG3dModelLoader extends ModelLoader<ObjModelProperties> {
	private PoolableJsonReader reader = new PoolableJsonReader();
	private G3dModelLoader objLoader = new G3dModelLoader(reader);

	@Override
	public Class<ObjModelProperties> getPropertiesType() {
		return ObjModelProperties.class;
	}

	@Override
	protected ModelData loadModelData(FileHandle fileHandle) {
		return objLoader.parseModel(fileHandle);
	}

	@Override
	public Model finish(DependencySupplier provider, FileHandle file, ModelData asyncData, ModelProperties properties) {
		Model model = super.finish(provider, file, asyncData, properties);
		reader.reset();
		return model;
	}
}
