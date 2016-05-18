package com.gurella.engine.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
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
	}

	public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
			final Environment environment, final Shader shader) {
		ensure3d();
	}

	// ////////2D

	public void render(PolygonRegion region, float x, float y) {
		ensure2d();
	}

	public void render(PolygonRegion region, float x, float y, float width, float height) {
		ensure2d();
	}

	public void render(PolygonRegion region, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation) {
		ensure2d();
	}

	public void render(Texture texture, float[] polygonVertices, int verticesOffset, int verticesCount,
			short[] polygonTriangles, int trianglesOffset, int trianglesCount) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX,
			boolean flipY) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth,
			int srcHeight, boolean flipX, boolean flipY) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y, float width, float height, float u, float v, float u2,
			float v2) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y) {
		ensure2d();
	}

	public void render(Texture texture, float x, float y, float width, float height) {
		ensure2d();
	}

	public void render(Texture texture, float[] spriteVertices, int offset, int count) {
		ensure2d();
	}

	public void render(TextureRegion region, float x, float y) {
		ensure2d();
	}

	public void render(TextureRegion region, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation) {
		ensure2d();
	}

	public void render(TextureRegion region, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, boolean clockwise) {
		ensure2d();
	}

	public void render(TextureRegion region, float width, float height, Affine2 transform) {
		ensure2d();
	}

	public void render(Sprite sprite) {
		ensure2d();
		sprite.draw(polygonSpriteBatch);
	}

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
