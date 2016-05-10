package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionComponent extends RenderableComponent2d {
	private Texture texture;
	private float u, v;
	private float u2, v2;

	private final TextureRegion region = new TextureRegion();

	public TextureRegionComponent() {
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		if (this.texture != texture) {
			this.texture = texture;
			region.setTexture(texture);
			sprite.setRegion(region);
		}
	}

	public float getU() {
		return u;
	}

	public void setU(float u) {
		if (this.u != u) {
			this.u = u;
			if (texture != null) {
				region.setU(u);
				sprite.setRegion(region);
				if (dimensionsFromTexture) {
					sprite.setSize(u2 - u, v2 - v);
					setDirty();
				} else {
					sprite.setSize(width, height);
				}
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
				region.setV(v);
				sprite.setRegion(region);
				if (dimensionsFromTexture) {
					sprite.setSize(u2 - u, v2 - v);
					setDirty();
				} else {
					sprite.setSize(width, height);
				}
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
				region.setU2(u2);
				sprite.setRegion(region);
				if (dimensionsFromTexture) {
					sprite.setSize(u2 - u, v2 - v);
					setDirty();
				} else {
					sprite.setSize(width, height);
				}
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
				region.setV2(v2);
				sprite.setRegion(region);
				if (dimensionsFromTexture) {
					sprite.setSize(u2 - u, v2 - v);
					setDirty();
				} else {
					sprite.setSize(width, height);
				}
			}
		}
	}

	@Override
	void updateDimensionsFromTexture() {
		if (texture != null) {
			sprite.setSize(u2 - u, v2 - v);
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
		region.setRegion(null, 0, 0, 0, 0);
	}
}