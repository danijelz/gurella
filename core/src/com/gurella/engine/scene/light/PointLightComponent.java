package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

public class PointLightComponent extends LightComponent<PointLight>
		implements NodeComponentActivityListener, NodeTransformChangedListener, PreRenderUpdateListener {
	private final Vector3 position = new Vector3();
	@SuppressWarnings("unused")
	private float intensity = 0.1f;

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected PointLight createLight() {
		PointLight pointLight = new PointLight();
		pointLight.intensity = 0.1f;
		return pointLight;
	}

	public float getIntensity() {
		return light.intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		light.intensity = intensity;
	}

	public Vector3 getPosition() {
		return light.position;
	}

	public void setPosition(Vector3 position) {
		this.position.set(position);
		light.position.set(position);
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
				light.position.setZero().add(position);
			} else {
				transformComponent.getWorldTranslation(light.position).add(position);
			}
			position.set(light.position);
		}
	}
}
