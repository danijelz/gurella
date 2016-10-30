package com.gurella.engine.asset.properties;

import com.badlogic.gdx.assets.loaders.ModelLoader.ModelParameters;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader.ObjLoaderParameters;

public class ObjModelProperties extends ModelProperties {
	public boolean flipV;

	@Override
	public ModelParameters createLoaderParameters() {
		ObjLoaderParameters modelParameter = new ObjLoaderParameters();
		modelParameter.textureParameter.genMipMaps = genMipMaps;
		modelParameter.textureParameter.minFilter = minFilter == null ? TextureFilter.Nearest : minFilter;
		modelParameter.textureParameter.magFilter = magFilter == null ? TextureFilter.Nearest : magFilter;
		modelParameter.textureParameter.wrapU = wrapU == null ? TextureWrap.ClampToEdge : wrapU;
		modelParameter.textureParameter.wrapV = wrapV == null ? TextureWrap.ClampToEdge : wrapV;
		modelParameter.flipV = flipV;
		return modelParameter;
	}
}
