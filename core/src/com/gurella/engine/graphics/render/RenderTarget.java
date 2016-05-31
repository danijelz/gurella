package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class RenderTarget {
	private FrameBuffer frameBuffer;
	
	public void bind() {
		frameBuffer.bind();
	}
}
