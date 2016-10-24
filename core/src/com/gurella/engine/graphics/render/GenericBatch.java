package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.scene.transform.TransformComponent;

public class GenericBatch implements Disposable {
	private final PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();
	private final ModelBatch modelBatch = new ModelBatch();
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();

	private Camera camera;
	private Environment environment;
	private Object activeBatch;

	public void begin(Camera camera) {
		if (camera == null) {
			throw new NullPointerException("camera is null.");
		}
		if (this.camera != null) {
			throw new IllegalStateException("GenericBatch.end must be called before begin.");
		}
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}

	public void end() {
		if (activeBatch == polygonSpriteBatch) {
			polygonSpriteBatch.end();
		} else if (activeBatch == modelBatch) {
			modelBatch.end();
		} else if (activeBatch == shapeRenderer) {
			shapeRenderer.end();
		}

		activeBatch = null;
		camera = null;
		environment = null;
	}

	public void flush() {
		if (activeBatch == polygonSpriteBatch) {
			polygonSpriteBatch.flush();
		} else if (activeBatch == modelBatch) {
			modelBatch.flush();
		} else if (activeBatch == shapeRenderer) {
			shapeRenderer.flush();
		}
	}

	public void activate2dRenderer() {
		if (activeBatch != polygonSpriteBatch) {
			if (activeBatch == modelBatch) {
				modelBatch.end();
			} else if (activeBatch == shapeRenderer) {
				shapeRenderer.end();
			}

			activeBatch = polygonSpriteBatch;
			polygonSpriteBatch.begin();
			polygonSpriteBatch.setProjectionMatrix(camera.combined);
		}
	}

	public void activate3dRenderer() {
		if (activeBatch != modelBatch) {
			if (activeBatch == polygonSpriteBatch) {
				polygonSpriteBatch.end();
			} else if (activeBatch == shapeRenderer) {
				shapeRenderer.end();
			}

			activeBatch = modelBatch;
			modelBatch.begin(camera);
		}
	}

	public void activateShapeRenderer() {
		if (activeBatch != shapeRenderer) {
			if (activeBatch == polygonSpriteBatch) {
				polygonSpriteBatch.end();
			} else if (activeBatch == modelBatch) {
				modelBatch.end();
			}

			activeBatch = shapeRenderer;
			shapeRenderer.setAutoShapeType(true);
			shapeRenderer.begin();
		}
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void render(final Renderable renderable) {
		activate3dRenderer();
		modelBatch.render(renderable);
	}

	public void render(final RenderableProvider renderableProvider) {
		activate3dRenderer();
		modelBatch.render(renderableProvider, environment);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders) {
		activate3dRenderer();
		modelBatch.render(renderableProviders, environment);
	}

	public void render(final RenderableProvider renderableProvider, final Environment environment) {
		activate3dRenderer();
		modelBatch.render(renderableProvider, environment);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
			final Environment environment) {
		activate3dRenderer();
		modelBatch.render(renderableProviders, environment);
	}

	public void render(final RenderableProvider renderableProvider, final Shader shader) {
		activate3dRenderer();
		modelBatch.render(renderableProvider, environment, shader);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Shader shader) {
		activate3dRenderer();
		modelBatch.render(renderableProviders, environment, shader);
	}

	public void render(final RenderableProvider renderableProvider, final Environment environment,
			final Shader shader) {
		activate3dRenderer();
		modelBatch.render(renderableProvider, environment, shader);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
			final Environment environment, final Shader shader) {
		activate3dRenderer();
		modelBatch.render(renderableProviders, environment, shader);
	}

	////////// 2D

	public void set2dTransform(TransformComponent transformComponent) {
		Matrix4 transformMatrix = polygonSpriteBatch.getTransformMatrix();
		if (transformComponent == null) {
			transformMatrix.idt();
		} else {
			transformComponent.getWorldTransform(transformMatrix);
		}
		polygonSpriteBatch.setTransformMatrix(transformMatrix);
	}

	public void set2dTransform(Matrix4 transform) {
		polygonSpriteBatch.setTransformMatrix(transform);
	}

	public void set2dProjection(Matrix4 projection) {
		polygonSpriteBatch.setProjectionMatrix(projection);
	}

	public void render(Sprite sprite) {
		activate2dRenderer();
		sprite.draw(polygonSpriteBatch);
	}

	// SHAPE

	public void rectLine(float x1, float y1, float x2, float y2, float width) {
		activateShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.rectLine(x1, y1, x2, y2, width);
	}

	public void line(float x1, float y1, float x2, float y2) {
		activateShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.line(x1, y1, x2, y2);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		activateShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.line(x1, y1, z1, x2, y2, z2);
	}

	public void line(Vector3 v1, Vector3 v2) {
		activateShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.line(v1, v2);
	}

	public void box(float x, float y, float z, float width, float height, float depth) {
		activateShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.box(x, y, z, width, height, depth);
	}

	public void setShapeRendererShapeType(ShapeType shapeType) {
		activateShapeRenderer();
		shapeRenderer.set(shapeType);
	}

	public void setShapeRendererColor(Color color) {
		activateShapeRenderer();
		shapeRenderer.setColor(color);
	}

	public void setShapeRendererTransform(TransformComponent transform) {
		activateShapeRenderer();
		Matrix4 transformMatrix = shapeRenderer.getTransformMatrix();
		if (transform == null) {
			transformMatrix.idt();
		} else {
			transform.getWorldTransform(transformMatrix);
		}
		shapeRenderer.setTransformMatrix(transformMatrix);
	}

	@Override
	public void dispose() {
		polygonSpriteBatch.dispose();
		modelBatch.dispose();
	}
}
