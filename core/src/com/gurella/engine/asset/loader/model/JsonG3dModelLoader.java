package com.gurella.engine.asset.loader.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.PoolableJsonReader;

public class JsonG3dModelLoader extends ModelLoader<ObjModelProperties> {
	private TrackingPoolableJsonReader reader = new TrackingPoolableJsonReader();
	private G3dModelLoader objLoader = new G3dModelLoader(reader);

	@Override
	public Class<ObjModelProperties> getPropertiesType() {
		return ObjModelProperties.class;
	}

	@Override
	protected ModelData loadModelData(FileHandle fileHandle) {
		ModelData modelData = objLoader.parseModel(fileHandle);
		reader.free();
		return modelData;
	}

	private static class TrackingPoolableJsonReader extends PoolableJsonReader {
		private JsonValue value;

		@Override
		public JsonValue parse(FileHandle file) {
			value = super.parse(file);
			return value;
		}

		void free() {
			free(value);
			value = null;
		}
	}
}
