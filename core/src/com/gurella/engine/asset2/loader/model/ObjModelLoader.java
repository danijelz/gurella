package com.gurella.engine.asset2.loader.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;

//TODO handle ObjModelProperties.flipV
public class ObjModelLoader extends ModelLoader<ObjModelProperties> {
	private ObjLoader objLoader = new ObjLoader();
	
	@Override
	public Class<ObjModelProperties> getAssetPropertiesType() {
		return ObjModelProperties.class;
	}

	@Override
	protected ModelData loadModelData(FileHandle fileHandle) {
		return objLoader.loadModelData(fileHandle);
	}
}
