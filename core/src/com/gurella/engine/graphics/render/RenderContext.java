package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gurella.engine.graphics.render.command.ClearRenderTargetCommand.ClearType;
import com.gurella.engine.graphics.render.shader.Pass;
import com.gurella.engine.graphics.render.shader.Technique;
import com.gurella.engine.scene.Scene;

public class RenderContext {
	private Scene scene;

	private Camera camera;
	private Viewport viewport;

	private Technique technique;
	private Pass pass;

	private RenderState currentState = new RenderState();
	private Array<RenderState> states = new Array<RenderState>();

	private final Array<RenderQueue> queues = new Array<RenderQueue>();

	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();

	public final ObjectMap<String, Object> userData = new ObjectMap<String, Object>();

	public int saveState() {
		return saveState(false);
	}

	public int saveState(boolean cloneState) {
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

	public boolean isBlendingEnabled() {
		return currentState.isBlendingEnabled();
	}

	public void setBlendingEnabled(boolean blending) {
		currentState.setBlendingEnabled(blending);
	}

	public int getBlendSourceFactor() {
		return currentState.getBlendSourceFactor();
	}

	public void setBlendSourceFactor(int blendSourceFactor) {
		currentState.setBlendSourceFactor(blendSourceFactor);
	}

	public int getBlendDestinationFactor() {
		return currentState.getBlendDestinationFactor();
	}

	public void setBlendDestinationFactor(int blendDestinationFactor) {
		currentState.setBlendDestinationFactor(blendDestinationFactor);
	}

	public boolean isDepthMaskEnabled() {
		return currentState.isDepthMaskEnabled();
	}

	public void setDepthMaskEnabled(boolean depthMask) {
		currentState.setDepthMaskEnabled(depthMask);
	}

	public int getDepthFunction() {
		return currentState.getDepthFunction();
	}

	public void setDepthFunction(int depthFunction) {
		currentState.setDepthFunction(depthFunction);
	}

	public float getDepthRangeNear() {
		return currentState.getDepthRangeNear();
	}

	public void setDepthRangeNear(float depthRangeNear) {
		currentState.setDepthRangeNear(depthRangeNear);
	}

	public float getDepthRangeFar() {
		return currentState.getDepthRangeFar();
	}

	public void setDepthRangeFar(float depthRangeFar) {
		currentState.setDepthRangeFar(depthRangeFar);
	}

	public boolean isStencilEnabled() {
		return currentState.isStencilEnabled();
	}

	public void setStencilEnabled(boolean stencil) {
		currentState.setStencilEnabled(stencil);
	}

	public int getFrontStencilMask() {
		return currentState.getFrontStencilMask();
	}

	public void setFrontStencilMask(int frontStencilMask) {
		currentState.setFrontStencilMask(frontStencilMask);
	}

	public int getFrontStencilFailFunction() {
		return currentState.getFrontStencilFailFunction();
	}

	public void setFrontStencilFailFunction(int frontStencilFailFunction) {
		currentState.setFrontStencilFailFunction(frontStencilFailFunction);
	}

	public int getFrontDepthFailFunction() {
		return currentState.getFrontDepthFailFunction();
	}

	public void setFrontDepthFailFunction(int frontDepthFailFunction) {
		currentState.setFrontDepthFailFunction(frontDepthFailFunction);
	}

	public int getFrontPassFunction() {
		return currentState.getFrontPassFunction();
	}

	public void setFrontPassFunction(int frontPassFunction) {
		currentState.setFrontPassFunction(frontPassFunction);
	}

	public int getBackStencilMask() {
		return currentState.getBackStencilMask();
	}

	public void setBackStencilMask(int backStencilMask) {
		currentState.setBackStencilMask(backStencilMask);
	}

	public int getBackStencilFailFunction() {
		return currentState.getBackStencilFailFunction();
	}

	public void setBackStencilFailFunction(int backStencilFailFunction) {
		currentState.setBackStencilFailFunction(backStencilFailFunction);
	}

	public int getBackDepthFailFunction() {
		return currentState.getBackDepthFailFunction();
	}

	public void setBackDepthFailFunction(int backDepthFailFunction) {
		currentState.setBackDepthFailFunction(backDepthFailFunction);
	}

	public int getBackPassFunction() {
		return currentState.getBackPassFunction();
	}

	public void setBackPassFunction(int backPassFunction) {
		currentState.setBackPassFunction(backPassFunction);
	}

	public int getCullFace() {
		return currentState.getCullFace();
	}

	public void setCullFace(int cullFace) {
		currentState.setCullFace(cullFace);
	}

	public Color getClearColorValue() {
		return currentState.getClearColorValue();
	}

	public void setClearColorValue(Color clearColorValue) {
		currentState.setClearColorValue(clearColorValue);
	}

	public float getClearDepthValue() {
		return currentState.getClearDepthValue();
	}

	public void setClearDepthValue(float clearDepthValue) {
		currentState.setClearDepthValue(clearDepthValue);
	}

	public int getClearStencilValue() {
		return currentState.getClearStencilValue();
	}

	public void setClearStencilValue(int clearStencilValue) {
		currentState.setClearStencilValue(clearStencilValue);
	}

	public RenderTarget getRenderTarget() {
		return currentState.getRenderTarget();
	}

	public void setRenderTarget(RenderTarget renderTarget) {
		currentState.setRenderTarget(renderTarget);
	}

	public void clear(ClearType type, Color clearColorValue, float clearDepthValue, int clearStencilValue) {
		if(type.clearColor) {
			setClearColorValue(clearColorValue);
		}
		// TODO Auto-generated method stub
		
	}
}
