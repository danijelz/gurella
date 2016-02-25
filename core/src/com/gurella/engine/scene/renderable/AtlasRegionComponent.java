package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.gurella.engine.utils.Values;

public class AtlasRegionComponent extends RenderableComponent2d {
	private TextureAtlas textureAtlas;
	private String regionName;

	private final AtlasRegion region = new AtlasRegion(null, 0, 0, 0, 0);

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}

	public void setTextureAtlas(TextureAtlas textureAtlas) {
		if (this.textureAtlas != textureAtlas) {
			this.textureAtlas = textureAtlas;
			if (textureAtlas != null && regionName != null) {
				AtlasRegion tempRegion = textureAtlas.findRegion(regionName);
				if (tempRegion == null) {
					region.setRegion(null, 0, 0, 0, 0);
				} else {
					region.setRegion(tempRegion);
					sprite.setRegion(tempRegion);
				}
			}
		}
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		if (!Values.isEqual(this.regionName, regionName)) {
			this.regionName = regionName;
			if (textureAtlas != null && regionName != null) {
				AtlasRegion tempRegion = textureAtlas.findRegion(regionName);
				if (tempRegion == null) {
					region.setRegion(null, 0, 0, 0, 0);
				} else {
					region.setRegion(tempRegion);
					sprite.setRegion(tempRegion);
				}
			}
		}
	}

	@Override
	void updateDimensionsFromTexture() {
		sprite.setSize(region.getU2() - region.getU(), region.getV2() - region.getV());
	}
}
