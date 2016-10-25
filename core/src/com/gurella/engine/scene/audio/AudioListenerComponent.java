package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.debug.AudioDebugRenderer;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;

@ModelDescriptor(descriptiveName = "Audio Listener")
public class AudioListenerComponent extends SceneNodeComponent2
		implements NodeComponentActivityListener, DebugRenderable, Poolable {
	public final Vector3 up = new Vector3();
	public final Vector3 direction = new Vector3();

	transient TransformComponent transformComponent;
	transient final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	transient final Vector3 velocity = new Vector3();

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = null;
		}
	}

	public Matrix4 getTransform(Matrix4 out) {
		return transformComponent == null ? out.idt() : transformComponent.getWorldTransform(out);
	}

	@Override
	public void debugRender(RenderContext context) {
		AudioDebugRenderer.render(context, this);
	}

	@Override
	public void reset() {
		up.setZero();
		direction.setZero();
		transformComponent = null;
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		velocity.setZero();
	}
}
