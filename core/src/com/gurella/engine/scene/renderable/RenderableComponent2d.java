package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable;

public abstract class RenderableComponent2d extends RenderableComponent implements DebugRenderable {
	private static final Color DEBUG_OUTLINE_COLOR = new Color(1f, 0.451f, 0f, 1.0f);

	public int zOrder;
	float width;
	float height;
	boolean flipX;
	boolean flipY;
	boolean dimensionsFromTexture = true; // TODO remove
	int pixelsPerUnit = -1;

	// TODO origin (center, leftBottom)

	transient final Sprite sprite = new Sprite();

	public RenderableComponent2d() {
		sprite.setOriginCenter();
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		if (this.width != width) {
			this.width = width;
			if (!dimensionsFromTexture) {
				sprite.setSize(width, height);
			}
			setDirty();
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		if (this.height != height) {
			this.height = height;
			if (!dimensionsFromTexture) {
				sprite.setSize(width, height);
			}
			setDirty();
		}
	}

	public boolean isFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
		sprite.setFlip(flipX, flipY);
	}

	public boolean isFlipY() {
		return flipY;
	}

	public void setFlipY(boolean flipY) {
		this.flipY = flipY;
		sprite.setFlip(flipX, flipY);
	}

	public Color getTint() {
		return sprite.getColor();
	}

	public void setTint(Color tint) {
		sprite.setColor(tint);
	}

	public void setTint(float r, float g, float b, float a) {
		sprite.setColor(r, g, b, a);
	}

	public boolean isDimensionsFromTexture() {
		return dimensionsFromTexture;
	}

	public void setDimensionsFromTexture(boolean dimensionsFromTexture) {
		if (this.dimensionsFromTexture != dimensionsFromTexture) {
			this.dimensionsFromTexture = dimensionsFromTexture;
			if (dimensionsFromTexture) {
				updateDimensionsFromTexture();
			} else {
				sprite.setSize(width, height);
			}
			setDirty();
		}
	}

	abstract void updateDimensionsFromTexture();

	@Override
	protected void updateGeometry() {
		sprite.setCenter(0, 0);
	}

	@Override
	protected void doRender(GenericBatch batch) {
		if (sprite.getTexture() != null) {
			batch.set2dTransform(transformComponent);
			batch.render(sprite);
		}
	}

	@Override
	protected void calculateBounds(BoundingBox bounds) {
		if (sprite.getTexture() == null) {
			return;
		}

		float width = sprite.getWidth();
		float height = sprite.getHeight();
		float x1 = -width * 0.5f;
		float y1 = height * 0.5f;
		float x2 = x1 + width;
		float y2 = y1 - height;
		bounds.ext(x1, y1, 0);
		bounds.ext(x2, y2, 0);
	}

	@Override
	public void debugRender(DebugRenderContext context) {
		if (sprite.getTexture() != null) {
			GenericBatch batch = context.batch;
			Gdx.gl20.glLineWidth(2.4f);
			batch.setShapeRendererTransform(transformComponent);
			batch.setShapeRendererColor(DEBUG_OUTLINE_COLOR);
			batch.setShapeRendererShapeType(ShapeType.Line);
			float width = sprite.getWidth();
			float height = sprite.getHeight();
			float x1 = -width * 0.5f;
			float y1 = -height * 0.5f;
			float x2 = x1 + width;
			float y2 = y1 + height;
			batch.line(x1, y1, x2, y1);
			batch.line(x2, y1, x2, y2);
			batch.line(x2, y2, x1, y2);
			batch.line(x1, y2, x1, y1);
			Gdx.gl20.glLineWidth(1f);
		}
	}

	@Override
	public void reset() {
		super.reset();
		width = 0;
		height = 0;
		dimensionsFromTexture = true;
		pixelsPerUnit = -1;
		sprite.setColor(1, 1, 1, 1);
		sprite.setFlip(false, false);
		sprite.setOriginCenter();
		flipX = false;
		flipY = false;
		zOrder = 0;
	}
}
