package com.gurella.engine.graphics.render.command;

import com.gurella.engine.graphics.render.RenderTarget;

public class SetRenderTargetCommand {
	String id;
	boolean depthBuf;
	int numColBufs;
	int width;
	int height;
	
	private RenderTarget renderTarget;
}
