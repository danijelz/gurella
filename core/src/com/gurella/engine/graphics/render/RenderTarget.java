package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class RenderTarget {
	private String name;
	private short width;
	private short height;
	private byte colourDepth;
	private AntiAliasingAmount aliasingAmount = AntiAliasingAmount.none;
	private DepthBufferType depthBufferType = DepthBufferType.none;
	private TextureFilter minFilter = TextureFilter.Nearest;
	private TextureFilter magFilter = TextureFilter.Nearest;
	private TextureWrap uWrap = TextureWrap.ClampToEdge;
	private TextureWrap vWrap = TextureWrap.ClampToEdge;

	private transient FrameBuffer frameBuffer;

	public void bind() {
		frameBuffer.bind();
	}

	public enum AntiAliasingAmount {
		none, two, four, eight;
	}

	public enum DepthBufferType {
		none, _16bit, _24bit;
	}
}
