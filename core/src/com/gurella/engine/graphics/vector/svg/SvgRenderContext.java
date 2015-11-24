package com.gurella.engine.graphics.vector.svg;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.svg.element.Element;
import com.badlogic.gdx.utils.Pools;

public class SvgRenderContext implements Poolable {
	public Canvas canvas;
	public Element rootElement;
	public Element parentElement;
	
	public Rectangle viewPort;
	
	public static SvgRenderContext obtain() {
		return Pools.obtain(SvgRenderContext.class);
	}
	
	@Override
	public void reset() {
		if(canvas != null) {
			canvas.free();
			canvas = null;
		}
			
		rootElement = null;
		parentElement = null;
		
		viewPort = null;
	}
}
