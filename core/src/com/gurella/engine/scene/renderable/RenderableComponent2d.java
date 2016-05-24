package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.graphics.GenericBatch;

public abstract class RenderableComponent2d extends RenderableComponent {
	float width;
	float height;
	boolean dimensionsFromTexture = true;
	int pixelsPerUnit = -1;
	@PropertyDescriptor(descriptiveName = "2.5D")
	boolean _25d;
	final Color tint = new Color(1, 1, 1, 1);

	// TODO flipX, flipY, zOrder, origin (center, leftBottom)

	public transient final Sprite sprite = new Sprite();

	public RenderableComponent2d() {
		sprite.setColor(tint);
		sprite.flip(false, true);
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

	public Color getTint() {
		return tint;
	}

	public void setTint(Color tint) {
		sprite.setColor(this.tint.set(tint));
	}

	public void setTint(float r, float g, float b, float a) {
		sprite.setColor(this.tint.set(r, g, b, a));
	}

	public boolean is_25d() {
		return _25d;
	}

	public void set_25d(boolean _25d) {
		if (this._25d != _25d) {
			this._25d = _25d;
			setDirty();
		}
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
		if (transformComponent == null) {
			sprite.setScale(1, 1);
			sprite.setRotation(0);
			sprite.setCenter(0, 0);
		} else {
			float y = transformComponent.getWorldTranslationY();
			if (_25d) {
				y += transformComponent.getWorldTranslationZ();
			}
			sprite.setScale(transformComponent.getWorldScaleX(), transformComponent.getWorldScaleY());
			sprite.setRotation(transformComponent.getWorldEulerRotationZ());
			sprite.setCenter(transformComponent.getWorldTranslationX(), y);
			sprite.setOriginCenter();
		}
	}

	@Override
	protected void doRender(GenericBatch batch) {
		if (sprite.getTexture() != null) {
			batch.set2dTransform(transformComponent);
			batch.render(sprite);
		}
	}

	@Override
	protected void doGetBounds(BoundingBox bounds) {
		if (sprite.getTexture() == null) {
			return;
		}

		float width = sprite.getWidth();
		float height = sprite.getHeight();
		float x1 = -width * 0.5f;
		float y1 = -height * 0.5f;
		float x2 = x1 + width;
		float y2 = y1 + height;
		bounds.ext(x1, y1, 0);
		bounds.ext(x2, y2, 0);
		// float x = transformComponent.getWorldTranslationX();
		// float y = transformComponent.getWorldTranslationY();
		// bounds.min.add(x, y, 0);
		// bounds.max.add(x, y, 0);
	}

	@Override
	protected boolean doGetIntersection(Ray ray, Vector3 intersection) {
		return Intersector.intersectRayTriangles(ray, sprite.getVertices(), intersection);
	}

	@Override
	public void reset() {
		super.reset();
		width = 0;
		height = 0;
		dimensionsFromTexture = true;
		pixelsPerUnit = -1;
		_25d = false;
		tint.set(1, 1, 1, 1);
	}
}
