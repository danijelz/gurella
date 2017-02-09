package com.gurella.engine.asset.loader.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.utils.UBJsonReader;

public class UbJsonG3dModelLoader extends ModelLoader<ObjModelProperties> {
	//TODO make poolable
	private UBJsonReader reader = new UBJsonReader();
	private G3dModelLoader objLoader = new G3dModelLoader(reader);

	@Override
	public Class<ObjModelProperties> getPropertiesType() {
		return ObjModelProperties.class;
	}

	@Override
	protected ModelData loadModelData(FileHandle fileHandle) {
		return objLoader.parseModel(fileHandle);
	}
}
