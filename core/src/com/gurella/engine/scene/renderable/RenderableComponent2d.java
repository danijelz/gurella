package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.resource.model.ResourceProperty;

public abstract class RenderableComponent2d extends RenderableComponent {
	float width;
	float height;
	boolean dimensionsFromTexture = true;
	int pixelsPerUnit = -1;
	boolean _25d;
	@ResourceProperty
	final Color tint = new Color(1, 1, 1, 1);

	// TODO flipX, flipY, zOrder, origin (center, leftBottom)

	public transient final Sprite sprite = new Sprite();

	public RenderableComponent2d() {
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
			notifyChanged(this);
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
			notifyChanged(this);
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
			if (transformComponent != null) {
				updateTransform();
			}
			notifyChanged(this);
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
			notifyChanged(this);
		}
	}

	abstract void updateDimensionsFromTexture();

	@Override
	protected void updateTransform() {
		float y = transformComponent.getWorldTranslationY();
		if (_25d) {
			y += transformComponent.getWorldTranslationZ();
		}
		sprite.setScale(transformComponent.getWorldScaleX(), transformComponent.getWorldScaleY());
		sprite.setRotation(transformComponent.getWorldEulerRotationZ());
		sprite.setCenter(transformComponent.getWorldTranslationX(), y);
	}

	@Override
	protected void updateDefaultTransform() {
		sprite.setScale(1, 1);
		sprite.setRotation(0);
		sprite.setCenter(0, 0);
	}

	@Override
	protected void render(GenericBatch batch) {
		if (sprite.getTexture() != null) {
			batch.render(sprite);
		}
	}

	@Override
	public void getBounds(BoundingBox bounds) {
		Rectangle rect = sprite.getBoundingRectangle();
		bounds.min.set(rect.x, rect.y, 0);
		bounds.max.set(rect.x + rect.width, rect.y + rect.height, 0);
	}

	@Override
	public boolean getIntersection(Ray ray, Vector3 intersection) {
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
