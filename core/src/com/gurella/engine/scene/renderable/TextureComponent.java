package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.metatype.MetaTypeDescriptor;

@MetaTypeDescriptor(descriptiveName = "Sprite")
public class TextureComponent extends RenderableComponent2d {
	private Texture texture;

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		if (this.texture == texture) {
			return;
		}

		this.texture = texture;
		sprite.setTexture(texture);
		if (texture != null) {
			int tWidth = texture.getWidth();
			int tHeight = texture.getHeight();
			sprite.setRegion(0, 0, tWidth, tHeight);
			sprite.setOriginCenter();
			setDirty();
		}
	}

	public void setTexture(Texture texture, float width, float height) {
		if (this.texture == texture) {
			return;
		}

		this.texture = texture;
		sprite.setTexture(texture);
		if (texture != null) {
			int tWidth = texture.getWidth();
			int tHeight = texture.getHeight();
			sprite.setRegion(0, 0, tWidth, tHeight);
			sprite.setSize(width, height);
			sprite.setOriginCenter();
			setDirty();
		}
	}

	@Override
	public void reset() {
		super.reset();
		texture = null;
		sprite.setTexture(null);
	}
}
