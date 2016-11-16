package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyChangeListener;

@ModelDescriptor(descriptiveName = "Sprite")
public class TextureComponent extends RenderableComponent2d implements PropertyChangeListener {
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

	public void updateTexture(Texture texture) {
		if (this.texture == texture) {
			return;
		}

		this.texture = texture;
		sprite.setTexture(texture);
		if (texture != null) {
			int tWidth = texture.getWidth();
			int tHeight = texture.getHeight();
			sprite.setRegion(0, 0, tWidth, tHeight);
			setSize(tWidth / textureImportPixelsPerUnit, tHeight / textureImportPixelsPerUnit);
			sprite.setOriginCenter();
			setDirty();
		}
	}

	@Override
	public void propertyChanged(String propertyName, Object oldValue, Object newValue) {
		if ("texture".equals(propertyName) && texture != null) {
			int tWidth = texture.getWidth();
			int tHeight = texture.getHeight();
			sprite.setRegion(0, 0, tWidth, tHeight);
			setSize(tWidth / textureImportPixelsPerUnit, tHeight / textureImportPixelsPerUnit);
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
