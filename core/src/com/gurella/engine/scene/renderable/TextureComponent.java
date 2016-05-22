package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable;

public class TextureComponent extends RenderableComponent2d implements DebugRenderable {
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
					setDirty();
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

	@Override
	public void debugRender(GenericBatch batch) {
		if (texture != null) {
			batch.setShapeRendererTransform(transformComponent);
			batch.setShapeRendererColor(Color.RED);
			batch.setShapeRendererShapeType(ShapeType.Line);
			float x = transformComponent == null ? 0 : 0;//transformComponent.getTranslationX();
			float y = transformComponent == null ? 0 : 0;//transformComponent.getTranslationY();
			float tempWidth = sprite.getWidth();
			float tempHeight = sprite.getHeight();
			float x1 = x - (tempWidth * 0.5f);
			float y1 = y - (tempHeight * 0.5f);
			float x2 = x1 + tempWidth;
			float y2 = y1 + tempHeight;
			batch.line(x1, y1, x2, y1);
			batch.line(x2, y1, x2, y2);
			batch.line(x2, y2, x1, y2);
			batch.line(x1, y2, x1, y1);
		}
	}

	@Override
	public void reset() {
		super.reset();
		texture = null;
		sprite.setTexture(null);
	}
}
