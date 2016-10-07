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

	private Object activeBatch;
	private Camera activeCamera;
	private Environment activeEnvironment;

	public void begin(Camera camera) {
		if (activeCamera != null) {
			throw new IllegalStateException("GenericBatch.end must be called before begin.");
		}
		activeCamera = camera;
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
		activeCamera = null;
		activeEnvironment = null;
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

	private void ensure2d() {
		if (activeBatch != polygonSpriteBatch) {
			if (activeBatch == modelBatch) {
				modelBatch.end();
			} else if (activeBatch == shapeRenderer) {
				shapeRenderer.end();
			}

			activeBatch = polygonSpriteBatch;
			polygonSpriteBatch.begin();
			polygonSpriteBatch.setProjectionMatrix(activeCamera.combined);
		}
	}

	private void ensure3d() {
		if (activeBatch != modelBatch) {
			if (activeBatch == polygonSpriteBatch) {
				polygonSpriteBatch.end();
			} else if (activeBatch == shapeRenderer) {
				shapeRenderer.end();
			}

			activeBatch = modelBatch;
			modelBatch.begin(activeCamera);
		}
	}

	private void ensureShapes() {
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
		this.activeEnvironment = environment;
	}

	public void render(final Renderable renderable) {
		ensure3d();
		modelBatch.render(renderable);
	}

	public void render(final RenderableProvider renderableProvider) {
		ensure3d();
		modelBatch.render(renderableProvider, activeEnvironment);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders) {
		ensure3d();
		modelBatch.render(renderableProviders, activeEnvironment);
	}

	public void render(final RenderableProvider renderableProvider, final Environment environment) {
		ensure3d();
		modelBatch.render(renderableProvider, environment);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
			final Environment environment) {
		ensure3d();
		modelBatch.render(renderableProviders, environment);
	}

	public void render(final RenderableProvider renderableProvider, final Shader shader) {
		ensure3d();
		modelBatch.render(renderableProvider, activeEnvironment, shader);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Shader shader) {
		ensure3d();
		modelBatch.render(renderableProviders, activeEnvironment, shader);
	}

	public void render(final RenderableProvider renderableProvider, final Environment environment,
			final Shader shader) {
		ensure3d();
		modelBatch.render(renderableProvider, environment, shader);
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
			final Environment environment, final Shader shader) {
		ensure3d();
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

	public void render(Sprite sprite) {
		ensure2d();
		sprite.draw(polygonSpriteBatch);
	}

	// SHAPE

	public void rectLine(float x1, float y1, float x2, float y2, float width) {
		ensureShapes();
		shapeRenderer.setProjectionMatrix(activeCamera.combined);
		shapeRenderer.rectLine(x1, y1, x2, y2, width);
	}

	public void line(float x1, float y1, float x2, float y2) {
		ensureShapes();
		shapeRenderer.setProjectionMatrix(activeCamera.combined);
		shapeRenderer.line(x1, y1, x2, y2);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		ensureShapes();
		shapeRenderer.setProjectionMatrix(activeCamera.combined);
		shapeRenderer.line(x1, y1, z1, x2, y2, z2);
	}

	public void line(Vector3 v1, Vector3 v2) {
		ensureShapes();
		shapeRenderer.setProjectionMatrix(activeCamera.combined);
		shapeRenderer.line(v1, v2);
	}

	public void box(float x, float y, float z, float width, float height, float depth) {
		ensureShapes();
		shapeRenderer.setProjectionMatrix(activeCamera.combined);
		shapeRenderer.box(x, y, z, width, height, depth);
	}

	public void setShapeRendererShapeType(ShapeType shapeType) {
		ensureShapes();
		shapeRenderer.set(shapeType);
	}

	public void setShapeRendererColor(Color color) {
		ensureShapes();
		shapeRenderer.setColor(color);
	}

	public void setShapeRendererTransform(TransformComponent transform) {
		ensureShapes();
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
