package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.render.material.Pass;
import com.gurella.engine.graphics.render.material.Technique;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.camera.CameraViewport;

public class RenderContext {
	final RenderPath path;
	RenderNode node;

	Scene scene;
	Camera camera;
	CameraViewport viewport;

	String passName;
	Technique technique;
	Pass pass;

	private RenderState currentState = new RenderState();
	private Array<RenderState> states = new Array<RenderState>();

	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();

	public final ObjectMap<Object, Object> userData = new ObjectMap<Object, Object>();

	RenderContext(RenderPath path) {
		this.path = path;
	}

	public int saveState() {
		return saveState(false);
	}

	public int pushState() {
		return saveState(true);
	}

	private int saveState(boolean cloneState) {
		RenderState newState = RenderState.obtain();
		if (cloneState) {
			newState.set(currentState);
		}
		states.add(newState);
		currentState = newState;
		return states.size;
	}

	RenderState getCurrentState() {
		return currentState;
	}

	public boolean restoreState() {
		if (states.size > 1) {
			states.removeIndex(states.size - 1).free();
			currentState = states.peek();
			return true;
		} else {
			return false;
		}
	}

	public int restoreToState(int state) {
		int stateToRestore = state < 1 ? 1 : state;
		int restored = 0;
		while (states.size > stateToRestore) {
			states.removeIndex(states.size - 1).free();
		}
		currentState = states.peek();
		return restored;
	}
}
