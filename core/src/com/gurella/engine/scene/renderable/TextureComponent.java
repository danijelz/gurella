package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Texture;

public class TextureComponent extends RenderableComponent2d {
	private Texture texture;
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		if (this.texture != texture) {
			this.texture = texture;
			sprite.setTexture(texture);
			
			if (texture != null) {
				sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());
				if (dimensionsFromTexture) {
					int tempPixelsPerUnit = pixelsPerUnit < 1 ? 1 : pixelsPerUnit;
					sprite.setSize(texture.getWidth() / tempPixelsPerUnit, texture.getHeight() / tempPixelsPerUnit);
					sprite.setOriginCenter();
					fireDirty();
				}
			}
		}
	}
	
	@Override
	void updateDimensionsFromTexture() {
		if (texture != null) {
			sprite.setSize(texture.getWidth(), texture.getHeight());
		}
	}
}
