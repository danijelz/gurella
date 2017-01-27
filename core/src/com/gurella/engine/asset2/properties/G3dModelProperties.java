package com.gurella.engine.asset2.properties;

import com.badlogic.gdx.assets.loaders.ModelLoader.ModelParameters;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

public class G3dModelProperties extends ModelProperties {
	@Override
	public ModelParameters createLoaderParameters() {
		ModelParameters modelParameter = new ModelParameters();
		modelParameter.textureParameter.genMipMaps = genMipMaps;
		modelParameter.textureParameter.minFilter = minFilter == null ? TextureFilter.Nearest : minFilter;
		modelParameter.textureParameter.magFilter = magFilter == null ? TextureFilter.Nearest : magFilter;
		modelParameter.textureParameter.wrapU = wrapU == null ? TextureWrap.ClampToEdge : wrapU;
		modelParameter.textureParameter.wrapV = wrapV == null ? TextureWrap.ClampToEdge : wrapV;
		return modelParameter;
	}
}
