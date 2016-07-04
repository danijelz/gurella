package com.gurella.engine.graphics.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gurella.engine.graphics.render.command.ClearRenderTargetCommand.ClearType;
import com.gurella.engine.graphics.render.gl.CullFace;
import com.gurella.engine.graphics.render.gl.DepthTestFunction;
import com.gurella.engine.graphics.render.gl.StencilOp;
import com.gurella.engine.graphics.render.material.Pass;
import com.gurella.engine.graphics.render.material.Technique;
import com.gurella.engine.graphics.render.renderable.Renderable;
import com.gurella.engine.scene.Scene;

public class RenderContext {
	private Scene scene;

	private Camera camera;
	private Viewport viewport;

	private Technique technique;
	private Pass pass;

	private RenderState currentState = new RenderState();
	private Array<RenderState> states = new Array<RenderState>();

	private RenderQueueKeySupplier renderQueueKeySupplier;
	private final ObjectMap<RenderQueueKey, RenderQueue> renderQueues = new ObjectMap<RenderQueueKey, RenderQueue>();

	private final IntMap<RenderTarget> targetsById = new IntMap<RenderTarget>();
	private final ObjectMap<String, RenderTarget> targetsByName = new ObjectMap<String, RenderTarget>();

	public final ObjectMap<Object, Object> userData = new ObjectMap<Object, Object>();

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

	public boolean isBlendingEnabled() {
		return currentState.isBlendingEnabled();
	}

	public void setBlendingEnabled(boolean blending) {
		currentState.setBlendingEnabled(blending);
	}

	public boolean getDepthMask() {
		return currentState.getDepthMask();
	}

	public void setDepthMask(boolean depthMask) {
		currentState.setDepthMask(depthMask);
	}

	public DepthTestFunction getDepthFunction() {
		return currentState.getDepthFunction();
	}

	public void setDepthFunction(DepthTestFunction depthFunction) {
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

	public StencilOp getFrontStencilFailOp() {
		return currentState.getFrontStencilFailOp();
	}

	public void setFrontStencilFailOp(StencilOp frontStencilFailOp) {
		currentState.setFrontStencilFailOp(frontStencilFailOp);
	}

	public StencilOp getFrontDepthFailOp() {
		return currentState.getFrontDepthFailOp();
	}

	public void setFrontDepthFailOp(StencilOp frontDepthFailOp) {
		currentState.setFrontDepthFailOp(frontDepthFailOp);
	}

	public StencilOp getFrontPassOp() {
		return currentState.getFrontPassOp();
	}

	public void setFrontPassOp(StencilOp frontPassOp) {
		currentState.setFrontPassOp(frontPassOp);
	}

	public int getBackStencilMask() {
		return currentState.getBackStencilMask();
	}

	public void setBackStencilMask(int backStencilMask) {
		currentState.setBackStencilMask(backStencilMask);
	}

	public StencilOp getBackStencilFailOp() {
		return currentState.getBackStencilFailOp();
	}

	public void setBackStencilFailOp(StencilOp backStencilFailOp) {
		currentState.setBackStencilFailOp(backStencilFailOp);
	}

	public StencilOp getBackDepthFailOp() {
		return currentState.getBackDepthFailOp();
	}

	public void setBackDepthFailOp(StencilOp backDepthFailOp) {
		currentState.setBackDepthFailOp(backDepthFailOp);
	}

	public StencilOp getBackPassOp() {
		return currentState.getBackPassOp();
	}

	public void setBackPassOp(StencilOp backPassOp) {
		currentState.setBackPassOp(backPassOp);
	}

	public CullFace getCullFace() {
		return currentState.getCullFace();
	}

	public void setCullFace(CullFace cullFace) {
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
		if (type.clearColor) {
			setClearColorValue(clearColorValue);
		}
		// TODO Auto-generated method stub
	}

	public interface RenderQueueKey extends Comparable<RenderQueueKey> {

	}

	public interface RenderQueueKeySupplier {
		RenderQueueKey getKey(Renderable renderable);
	}
}
