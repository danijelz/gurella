package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.debug.DebugRenderable;

public abstract class RenderableComponent2d extends RenderableComponent implements DebugRenderable {
	private static final Color DEBUG_OUTLINE_COLOR = new Color(1f, 0.451f, 0f, 1.0f);

	float width;
	float height;
	boolean dimensionsFromTexture = true;
	int pixelsPerUnit = -1;
	@PropertyDescriptor(descriptiveName = "2.5D")
	boolean _25d;
	final Color tint = new Color(1, 1, 1, 1);

	// TODO flipX, flipY, zOrder, origin (center, leftBottom)

	transient final Sprite sprite = new Sprite();

	public RenderableComponent2d() {
		sprite.setColor(tint);
		sprite.flip(false, true);
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
		/*if (transformComponent == null) {
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
		}*/
		
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
	protected void doGetBounds(BoundingBox bounds) {
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
	protected boolean doGetIntersection(Ray ray, Vector3 intersection) {
		Ray inv = new Ray().set(ray);
		if(transformComponent != null) {
			transformComponent.transformRayFromWorld(inv);
		}
		
		Vector3 v1 = PoolService.obtain(Vector3.class).setZero();
		Vector3 v2 = PoolService.obtain(Vector3.class).setZero();
		Vector3 v3 = PoolService.obtain(Vector3.class).setZero();
		
		float[] vertices = sprite.getVertices();
		
		v1.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		v2.set(vertices[Batch.X2], vertices[Batch.Y2], 0);
		v3.set(vertices[Batch.X3], vertices[Batch.Y3], 0);
		
		if(Intersector.intersectRayTriangle(inv, v1, v2, v3, intersection)) {
			PoolService.free(v1);
			PoolService.free(v2);
			PoolService.free(v3);
			return true;
		}
		
		v1.set(vertices[Batch.X3], vertices[Batch.Y3], 0);
		v2.set(vertices[Batch.X4], vertices[Batch.Y4], 0);
		v3.set(vertices[Batch.X1], vertices[Batch.Y1], 0);
		boolean result = Intersector.intersectRayTriangle(inv, v1, v2, v3, intersection);
		
		PoolService.free(v1);
		PoolService.free(v2);
		PoolService.free(v3);
		
		return result;
	}

	@Override
	public void debugRender(GenericBatch batch) {
		if (sprite.getTexture() != null) {
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
		_25d = false;
		tint.set(1, 1, 1, 1);
	}
}
