package com.gurella.engine.scene.camera;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Scaling;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.model.ValueRange;
import com.gurella.engine.base.model.ValueRange.FloatRange;

public class CameraViewport {
	private transient Camera camera;
	@PropertyDescriptor(nullable = false)
	private CameraViewportType type = CameraViewportType.screen;

	private float worldWidth;
	private float worldHeight;

	private int screenX;
	private int screenY;
	private int screenWidth;
	private int screenHeight;

	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	private float viewportX;
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	private float viewportY;
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	private float viewportWidth = 1;
	@ValueRange(floatRange = @FloatRange(min = 0, max = 1))
	private float viewportHeight = 1;

	private int viewportScreenX;
	private int viewportScreenY;
	private int viewportScreenWidth;
	private int viewportScreenHeight;

	// ////////////screen
	private float unitsPerPixel = 1;

	// ////////////extend
	private float minWorldWidth;
	private float minWorldHeight;
	private float maxWorldWidth;
	private float maxWorldHeight;

	private final Vector3 tmp = new Vector3();

	public CameraViewport(Camera camera) {
		this.camera = camera;
	}

	public void set(CameraViewport other) {
		CameraViewportType type = other.type;

		worldWidth = other.worldWidth;
		worldHeight = other.worldHeight;

		screenX = other.screenX;
		screenY = other.screenY;
		screenWidth = other.screenWidth;
		screenHeight = other.screenHeight;

		viewportX = other.viewportX;
		viewportY = other.viewportY;
		viewportWidth = other.viewportWidth;
		viewportHeight = other.worldHeight;

		viewportScreenX = other.viewportScreenX;
		viewportScreenY = other.viewportScreenY;
		viewportScreenWidth = other.viewportScreenWidth;
		viewportScreenHeight = other.viewportScreenHeight;

		unitsPerPixel = other.unitsPerPixel;

		minWorldWidth = other.minWorldWidth;
		minWorldHeight = other.minWorldHeight;
		maxWorldWidth = other.maxWorldWidth;
		maxWorldHeight = other.minWorldHeight;
	}

	/**
	 * Applies the viewport to the camera and sets the glViewport.
	 */
	public void apply() {
		Gdx.gl.glViewport(screenX, screenY, screenWidth, screenHeight);
		if (isConstrainedViewport()) {
			Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
			Gdx.gl.glScissor(viewportScreenX, viewportScreenY, viewportScreenWidth, viewportScreenHeight);
		} else {
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		}

		camera.viewportWidth = worldWidth;
		camera.viewportHeight = worldHeight;
		camera.update();
	}

	public void centerCamera() {
		camera.position.set(worldWidth / 2, worldHeight / 2, 0);
		camera.update();
	}

	/**
	 * Configures this viewport's screen bounds using the specified screen size. Typically called from
	 * {@link ApplicationListener#resize(int, int)}.
	 */
	public void update(int newScreenWidth, int newScreenHeight) {
		updateViewportRect(newScreenWidth, newScreenHeight);

		switch (type) {
		case extend:
			updateToExtend();
			break;
		case screen:
			updateToScreen();
			break;
		case fit:
			updateToScaling(Scaling.fit);
			break;
		case fill:
			updateToScaling(Scaling.fill);
			break;
		case fillX:
			updateToScaling(Scaling.fillX);
			break;
		case fillY:
			updateToScaling(Scaling.fillY);
			break;
		case stretch:
			updateToScaling(Scaling.stretch);
			break;
		case stretchX:
			updateToScaling(Scaling.stretchX);
			break;
		case stretchY:
			updateToScaling(Scaling.stretchY);
			break;
		case none:
			updateToScaling(Scaling.none);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void updateToExtend() {
		// Fit min size to the screen.
		float tempWorldWidth = minWorldWidth;
		float tempWorldHeight = minWorldHeight;
		Vector2 scaled = Scaling.fit.apply(tempWorldWidth, tempWorldHeight, viewportScreenWidth, viewportScreenHeight);

		// Extend in the short direction.
		int tempViewportWidth = Math.round(scaled.x);
		int tempViewportHeight = Math.round(scaled.y);
		if (tempViewportWidth < viewportScreenWidth) {
			float toViewportSpace = tempViewportHeight / tempWorldHeight;
			float toWorldSpace = tempWorldHeight / tempViewportHeight;
			float lengthen = (viewportScreenWidth - tempViewportWidth) * toWorldSpace;
			if (maxWorldWidth > 0) {
				lengthen = Math.min(lengthen, maxWorldWidth - minWorldWidth);
			}
			tempWorldWidth += lengthen;
			tempViewportWidth += Math.round(lengthen * toViewportSpace);
		} else if (tempViewportHeight < viewportScreenHeight) {
			float toViewportSpace = tempViewportWidth / tempWorldWidth;
			float toWorldSpace = tempWorldWidth / tempViewportWidth;
			float lengthen = (viewportScreenHeight - tempViewportHeight) * toWorldSpace;
			if (maxWorldHeight > 0) {
				lengthen = Math.min(lengthen, maxWorldHeight - minWorldHeight);
			}
			tempWorldHeight += lengthen;
			tempViewportHeight += Math.round(lengthen * toViewportSpace);
		}

		setWorldSize(tempWorldWidth, tempWorldHeight);

		// Center.
		setScreenBounds((viewportScreenWidth - tempViewportWidth) / 2 + viewportScreenX,
				(viewportScreenHeight - tempViewportHeight) / 2 + viewportScreenY, tempViewportWidth,
				tempViewportHeight);
	}

	public void updateToScreen() {
		setScreenBounds(viewportScreenX, viewportScreenY, viewportScreenWidth, viewportScreenHeight);
		setWorldSize(viewportScreenWidth * unitsPerPixel, viewportScreenHeight * unitsPerPixel);
	}

	private void updateViewportRect(int newScreenWidth, int newScreenHeight) {
		viewportScreenX = (int) (newScreenWidth * viewportX);
		viewportScreenY = (int) (newScreenHeight * viewportY);
		viewportScreenWidth = (int) (newScreenWidth * viewportWidth);
		viewportScreenHeight = (int) (newScreenHeight * viewportHeight);
	}

	private boolean isConstrainedViewport() {
		return viewportX != 0 || viewportY != 0 || viewportWidth != 1 || viewportHeight != 1;
	}

	public void updateToScaling(Scaling scaling) {
		Vector2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(), viewportScreenWidth, viewportScreenHeight);
		int tempViewportWidth = Math.round(scaled.x);
		int tempViewportHeight = Math.round(scaled.y);

		// Center.
		setScreenBounds((viewportScreenWidth - tempViewportWidth) / 2 + viewportScreenX,
				(viewportScreenHeight - tempViewportHeight) / 2 + viewportScreenY, tempViewportWidth,
				tempViewportHeight);
	}

	/**
	 * Transforms the specified screen coordinate to world coordinates.
	 * 
	 * @return The vector that was passed in, transformed to world coordinates.
	 * @see Camera#unproject(Vector3)
	 */
	public Vector2 unproject(Vector2 screenCoords) {
		// TODO check for viewport bounds
		tmp.set(screenCoords.x, screenCoords.y, 1);
		camera.unproject(tmp, screenX, screenY, screenWidth, screenHeight);
		screenCoords.set(tmp.x, tmp.y);
		return screenCoords;
	}

	/**
	 * Transforms the specified world coordinate to screen coordinates.
	 * 
	 * @return The vector that was passed in, transformed to screen coordinates.
	 * @see Camera#project(Vector3)
	 */
	public Vector2 project(Vector2 worldCoords) {
		tmp.set(worldCoords.x, worldCoords.y, 1);
		camera.project(tmp, screenX, screenY, screenWidth, screenHeight);
		worldCoords.set(tmp.x, tmp.y);
		return worldCoords;
	}

	/**
	 * Transforms the specified screen coordinate to world coordinates.
	 * 
	 * @return The vector that was passed in, transformed to world coordinates.
	 * @see Camera#unproject(Vector3)
	 */
	public Vector3 unproject(Vector3 screenCoords) {
		camera.unproject(screenCoords, screenX, screenY, screenWidth, screenHeight);
		return screenCoords;
	}

	/**
	 * Transforms the specified world coordinate to screen coordinates.
	 * 
	 * @return The vector that was passed in, transformed to screen coordinates.
	 * @see Camera#project(Vector3)
	 */
	public Vector3 project(Vector3 worldCoords) {
		camera.project(worldCoords, screenX, screenY, screenWidth, screenHeight);
		return worldCoords;
	}

	/** @see Camera#getPickRay(float, float, float, float, float, float) */
	public Ray getPickRay(float screenCoordX, float screenCoordY) {
		return camera.getPickRay(screenCoordX, screenCoordY, this.screenX, this.screenY, screenWidth, screenHeight);
	}

	/**
	 * Transforms a point to real screen coordinates (as opposed to OpenGL ES window coordinates), where the origin is
	 * in the top left and the the y-axis is pointing downwards.
	 */
	public Vector2 toScreenCoordinates(Vector2 worldCoords, Matrix4 transformMatrix) {
		tmp.set(worldCoords.x, worldCoords.y, 0);
		tmp.mul(transformMatrix);
		camera.project(tmp);
		tmp.y = Gdx.graphics.getHeight() - tmp.y;
		worldCoords.x = tmp.x;
		worldCoords.y = tmp.y;
		return worldCoords;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public float getWorldWidth() {
		return worldWidth;
	}

	/**
	 * The virtual width of this viewport in world coordinates. This width is scaled to the viewport's screen width.
	 */
	public void setWorldWidth(float worldWidth) {
		this.worldWidth = worldWidth;
	}

	public float getWorldHeight() {
		return worldHeight;
	}

	/**
	 * The virtual height of this viewport in world coordinates. This height is scaled to the viewport's screen height.
	 */
	public void setWorldHeight(float worldHeight) {
		this.worldHeight = worldHeight;
	}

	public void setWorldSize(float worldWidth, float worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public int getScreenX() {
		return screenX;
	}

	/**
	 * Sets the viewport's offset from the left edge of the screen. This is typically set by
	 * {@link #update(int, int, boolean)}.
	 */
	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	/**
	 * Sets the viewport's offset from the bottom edge of the screen. This is typically set by
	 * {@link #update(int, int, boolean)}.
	 */
	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Sets the viewport's width in screen coordinates. This is typically set by {@link #update(int, int, boolean)}.
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Sets the viewport's height in screen coordinates. This is typically set by {@link #update(int, int, boolean)}.
	 */
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * Sets the viewport's position in screen coordinates. This is typically set by {@link #update(int, int, boolean)}.
	 */
	public void setScreenPosition(int screenX, int screenY) {
		this.screenX = screenX;
		this.screenY = screenY;
	}

	/**
	 * Sets the viewport's size in screen coordinates. This is typically set by {@link #update(int, int, boolean)}.
	 */
	public void setScreenSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	/**
	 * Sets the viewport's bounds in screen coordinates. This is typically set by {@link #update(int, int, boolean)}.
	 */
	public void setScreenBounds(int screenX, int screenY, int screenWidth, int screenHeight) {
		this.screenX = screenX;
		this.screenY = screenY;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public void setViewportBounds(float viewportScreenX, float viewportScreenY, float viewportScreenWidth,
			float viewportScreenHeight) {
		this.viewportX = MathUtils.clamp(viewportScreenX, 0, 1);
		this.viewportY = MathUtils.clamp(viewportScreenY, 0, 1);
		this.viewportWidth = MathUtils.clamp(viewportScreenWidth, 0, 1);
		this.viewportHeight = MathUtils.clamp(viewportScreenHeight, 0, 1);
	}

	/** Returns the left gutter (black bar) width in screen coordinates. */
	public int getLeftGutterWidth() {
		return screenX;
	}

	/** Returns the right gutter (black bar) x in screen coordinates. */
	public int getRightGutterX() {
		return screenX + screenWidth;
	}

	/** Returns the right gutter (black bar) width in screen coordinates. */
	public int getRightGutterWidth() {
		return Gdx.graphics.getWidth() - (screenX + screenWidth);
	}

	/** Returns the bottom gutter (black bar) height in screen coordinates. */
	public int getBottomGutterHeight() {
		return screenY;
	}

	/** Returns the top gutter (black bar) y in screen coordinates. */
	public int getTopGutterY() {
		return screenY + screenHeight;
	}

	/** Returns the top gutter (black bar) height in screen coordinates. */
	public int getTopGutterHeight() {
		return Gdx.graphics.getHeight() - (screenY + screenHeight);
	}

	public float getMinWorldWidth() {
		return minWorldWidth;
	}

	public void setMinWorldWidth(float minWorldWidth) {
		this.minWorldWidth = minWorldWidth;
	}

	public float getMinWorldHeight() {
		return minWorldHeight;
	}

	public void setMinWorldHeight(float minWorldHeight) {
		this.minWorldHeight = minWorldHeight;
	}

	public float getMaxWorldWidth() {
		return maxWorldWidth;
	}

	public void setMaxWorldWidth(float maxWorldWidth) {
		this.maxWorldWidth = maxWorldWidth;
	}

	public float getMaxWorldHeight() {
		return maxWorldHeight;
	}

	public void setMaxWorldHeight(float maxWorldHeight) {
		this.maxWorldHeight = maxWorldHeight;
	}

	public float getUnitsPerPixel() {
		return unitsPerPixel;
	}

	/**
	 * Sets the number of pixels for each world unit. Eg, a scale of 2.5 means there are 2.5 world units for every 1
	 * screen pixel. Default is 1.
	 */
	public void setUnitsPerPixel(float unitsPerPixel) {
		this.unitsPerPixel = unitsPerPixel;
	}

	public CameraViewportType getType() {
		return type;
	}

	public void setType(CameraViewportType type) {
		this.type = type;
	}

	public float getViewportX() {
		return viewportX;
	}

	public void setViewportX(float viewportX) {
		this.viewportX = MathUtils.clamp(viewportX, 0, 1);
	}

	public float getViewportY() {
		return viewportY;
	}

	public void setViewportY(float viewportY) {
		this.viewportY = MathUtils.clamp(viewportY, 0, 1);
	}

	public float getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportWidth(float viewportWidth) {
		this.viewportWidth = MathUtils.clamp(viewportWidth, 0, 1);
	}

	public float getViewportHeight() {
		return viewportHeight;
	}

	public void setViewportHeight(float viewportHeight) {
		this.viewportHeight = MathUtils.clamp(viewportHeight, 0, 1);
	}

	void reset() {
		type = CameraViewportType.screen;
		worldWidth = 0;
		worldHeight = 0;
		screenX = 0;
		screenY = 0;
		screenWidth = 0;
		screenHeight = 0;
		viewportX = 0;
		viewportY = 0;
		viewportWidth = 1;
		viewportHeight = 1;
		viewportScreenX = 0;
		viewportScreenY = 0;
		viewportScreenWidth = 0;
		viewportScreenHeight = 0;
		unitsPerPixel = 1;
		minWorldWidth = 0;
		minWorldHeight = 0;
		maxWorldWidth = 0;
		maxWorldHeight = 0;
	}

	public enum CameraViewportType {
		/**
		 * Scales the source to fit the target while keeping the same aspect ratio. This may cause the source to be
		 * smaller than the target in one direction.
		 */
		fit,
		/**
		 * Scales the source to fill the target while keeping the same aspect ratio. This may cause the source to be
		 * larger than the target in one direction.
		 */
		fill,
		/**
		 * Scales the source to fill the target in the x direction while keeping the same aspect ratio. This may cause
		 * the source to be smaller or larger than the target in the y direction.
		 */
		fillX,
		/**
		 * Scales the source to fill the target in the y direction while keeping the same aspect ratio. This may cause
		 * the source to be smaller or larger than the target in the x direction.
		 */
		fillY,
		/**
		 * Scales the source to fill the target. This may cause the source to not keep the same aspect ratio.
		 */
		stretch,
		/**
		 * Scales the source to fill the target in the x direction, without changing the y direction. This may cause the
		 * source to not keep the same aspect ratio.
		 */
		stretchX,
		/**
		 * Scales the source to fill the target in the y direction, without changing the x direction. This may cause the
		 * source to not keep the same aspect ratio.
		 */
		stretchY,
		/** The source is not scaled. */
		none,
		/**
		 * A viewport that keeps the world aspect ratio by extending the world in one direction. The world is first
		 * scaled to fit within the viewport, then the shorter dimension is lengthened to fill the viewport. A maximum
		 * size can be specified to limit how much the world is extended and black bars (letterboxing) are used for any
		 * remaining space.
		 */
		extend,
		/**
		 * A viewport where the world size is based on the size of the screen. By default 1 world unit == 1 screen
		 * pixel, but this ratio can be {@link #setUnitsPerPixel(float) changed}.
		 */
		screen;
	}
}
