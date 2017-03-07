package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.graphics.render.RenderContext;

public class ClearRenderTargetCommand implements RenderCommand {
	public ClearType type;
	public final Color clearColorValue = new Color();
	public float clearDepthValue;
	public int clearStencilValue;

	@Override
	public void process(RenderContext context) {
		context.clear(type, clearColorValue, clearDepthValue, clearStencilValue);
	}

	public enum ClearType {
		color(true, false, false),
		depth(false, true, false),
		stencil(false, false, true),
		colorDepth(true, true, false),
		colorStencil(true, false, true),
		colorDepthStencil(true, true, true),
		depthStencil(false, true, true);

		public final boolean clearColor;
		public final boolean clearDepth;
		public final boolean clearStencil;

		private ClearType(boolean clearColor, boolean clearDepth, boolean clearStencil) {
			this.clearColor = clearColor;
			this.clearDepth = clearDepth;
			this.clearStencil = clearStencil;
		}
	}
}
