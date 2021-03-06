package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class RenderTarget implements Disposable {
	private String name;
	private short width;
	private short height;
	private Format format = Format.RGBA8888;
	private boolean hasStencil;
	private AntiAliasingAmount aliasingAmount = AntiAliasingAmount.none;
	private DepthBufferType depthBufferType = DepthBufferType.none;
	private TextureFilter minFilter = TextureFilter.Linear;
	private TextureFilter magFilter = TextureFilter.Linear;
	private TextureWrap uWrap = TextureWrap.ClampToEdge;
	private TextureWrap vWrap = TextureWrap.ClampToEdge;

	private transient FrameBuffer frameBuffer;
	
	public String getName() {
		return name;
	}

	public short getWidth() {
		return width;
	}

	public short getHeight() {
		return height;
	}

	public void bind() {
		frameBuffer.bind();
	}

	public void init() {
		if (frameBuffer != null) {
			throw new GdxRuntimeException("RenderTarget is allready initialized.");
		}

		boolean depthEnabled = DepthBufferType.isDepthEnabled(depthBufferType);
		frameBuffer = new FrameBufferExt(format, width, height, depthEnabled, hasStencil);
	}

	@Override
	public void dispose() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
			frameBuffer = null;
		}
	}

	public enum AntiAliasingAmount {
		none, two, four, eight;
	}

	public enum DepthBufferType {
		none, _16bit, _24bit;

		public static boolean isDepthEnabled(DepthBufferType type) {
			return type != none;
		}
	}

	// TODO EXT_draw_buffers
	private class FrameBufferExt extends FrameBuffer {
		public FrameBufferExt(Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
			super(format, width, height, hasDepth, hasStencil);
		}

		@Override
		protected Texture createColorTexture() {
			int glFormat = Pixmap.Format.toGlFormat(super.format);
			int glType = Pixmap.Format.toGlType(super.format);
			GLOnlyTextureData data = new GLOnlyTextureData(super.width, super.height, 0, glFormat, glFormat, glType);
			Texture result = new Texture(data);
			result.setFilter(minFilter, magFilter);
			result.setWrap(uWrap, vWrap);
			return result;
		}
	}
}
