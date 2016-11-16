package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Texture;

public class TextureRegionComponent extends RenderableComponent2d {
	private Texture texture;
	private float u, v;
	private float u2, v2;

	public TextureRegionComponent() {
	}

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
			sprite.setRegion(u, v, u2, v2);
			sprite.setOriginCenter();
			setDirty();
		}
	}

	public float getU() {
		return u;
	}

	public void setU(float u) {
		if (this.u != u) {
			this.u = u;
			if (texture != null) {
				sprite.setRegion(u, v, u2, v2);
				sprite.setOriginCenter();
				setDirty();
			}
		}
	}

	public float getV() {
		return v;
	}

	public void setV(float v) {
		if (this.v != v) {
			this.v = v;
			if (texture != null) {
				sprite.setRegion(u, v, u2, v2);
				sprite.setOriginCenter();
				setDirty();
			}
		}
	}

	public float getU2() {
		return u2;
	}

	public void setU2(float u2) {
		if (this.u2 != u2) {
			this.u2 = u2;
			if (texture != null) {
				sprite.setRegion(u, v, u2, v2);
				sprite.setOriginCenter();
				setDirty();
			}
		}
	}

	public float getV2() {
		return v2;
	}

	public void setV2(float v2) {
		if (this.v2 != v2) {
			this.v2 = v2;
			if (texture != null) {
				sprite.setRegion(u, v, u2, v2);
				sprite.setOriginCenter();
				setDirty();
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		texture = null;
		u = 0;
		v = 0;
		u2 = 0;
		v2 = 0;
	}
}