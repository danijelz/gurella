package com.gurella.engine.graphics.vector.svg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public interface ExternalFileResolver {
	Texture resolveImage(String imageName);

	static final ExternalFileResolver defaultInstance = new ExternalFileResolver() {
		private ObjectMap<String, Texture> resolvedTextures = new ObjectMap<String, Texture>();

		@Override
		public Texture resolveImage(String imageName) {
			Texture image = resolvedTextures.get(imageName);
			if (image == null) {
				image = new Texture(Gdx.files.internal(imageName));
				resolvedTextures.put(imageName, image);
			}
			return image;
		}
	};
}
