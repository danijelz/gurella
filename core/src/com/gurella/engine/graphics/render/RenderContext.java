package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gurella.engine.graphics.render.shader.Pass;
import com.gurella.engine.graphics.render.shader.Technique;
import com.gurella.engine.scene.Scene;

public class RenderContext {
	private Scene scene;

	private Camera camera;
	private Viewport viewport;

	private Technique technique;
	private Pass pass;

	private RenderState currentRenderState;
	private Array<RenderState> renderStateStack = new Array<RenderState>();

	private final Array<RenderQueue> queues = new Array<RenderQueue>();

	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();
	
	public final ObjectMap<String, Object> data = new ObjectMap<String, Object>();

	//	 public int saveState() {
	//		return saveState(false);
	//	}
	//
	//	public int saveState(boolean cloneState) {
	//		CanvasState newState = CanvasState.obtain();
	//		if(cloneState) {
	//			newState.set(currentState);
	//		}
	//		states.add(newState);
	//		currentState = newState;
	//		return states.size;
	//	}
	//	
	//	CanvasState getCurrentState() {
	//		return currentState;
	//	}
	//
	//	public boolean restoreState() {
	//		if(states.size > 1) {
	//			Pools.free(states.removeIndex(states.size - 1));
	//			currentState = states.peek();
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
	//	
	//	public int restoreToState(int state) {
	//		int stateToRestore = state < 1 ? 1 : state;
	//		int restored = 0;
	//		while(states.size > stateToRestore) {
	//			Pools.free(states.removeIndex(states.size - 1));
	//		}
	//		currentState = states.peek();
	//		return restored;
	//	}
}
