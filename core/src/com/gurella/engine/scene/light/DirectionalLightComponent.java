package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

public class DirectionalLightComponent extends LightComponent<DirectionalLight>
		implements NodeComponentActivityListener, NodeTransformChangedListener, PreRenderUpdateListener {
	private final Vector3 direction = new Vector3(0, -1, 0);

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected DirectionalLight createLight() {
		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.direction.set(0, -1, 0);
		return directionalLight;
	}

	public Vector3 getDirection() {
		return light.direction;
	}

	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
		light.direction.set(direction);
	}
	
	@Override
	protected void onActivate() {
		transformComponent = getNode().getActiveComponent(TransformComponent.class);
	}

	@Override
	protected void onDeactivate() {
		transformComponent = null;
		dirty = true;
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
			dirty = true;
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = null;
			dirty = true;
		}
	}

	@Override
	public void onNodeTransformChanged() {
		dirty = true;
	}

	@Override
	public void onPreRenderUpdate() {
		if (dirty) {
			dirty = false;
			if (transformComponent == null) {
				light.direction.setZero().add(direction);
			} else {
				transformComponent.getWorldTranslation(direction);
				transformComponent.localToWorld(light.direction).sub(direction);
			}
			direction.set(light.direction);
		}
	}
}
