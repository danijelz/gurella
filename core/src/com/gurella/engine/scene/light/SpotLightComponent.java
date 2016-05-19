package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

public class SpotLightComponent extends LightComponent<SpotLight>
		implements NodeComponentActivityListener, NodeTransformChangedListener, PreRenderUpdateListener {
	private final Vector3 position = new Vector3();
	private final Vector3 direction = new Vector3(0, -1, 0);
	@SuppressWarnings("unused")
	private float intensity = 0.1f;
	@SuppressWarnings("unused")
	private float cutoffAngle = 1;
	@SuppressWarnings("unused")
	private float exponent = 1;

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected SpotLight createLight() {
		SpotLight spotLight = new SpotLight();
		spotLight.direction.set(0, -1, 0);
		spotLight.intensity = 0.1f;
		spotLight.cutoffAngle = 1;
		spotLight.exponent = 1;
		return spotLight;
	}

	public float getIntensity() {
		return light.intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		light.intensity = intensity;
	}

	public float getCutoffAngle() {
		return light.cutoffAngle;
	}

	public void setCutoffAngle(float cutoffAngle) {
		this.cutoffAngle = cutoffAngle;
		light.cutoffAngle = cutoffAngle;
	}

	public float getExponent() {
		return light.exponent;
	}

	public void setExponent(float exponent) {
		this.exponent = exponent;
		light.exponent = exponent;
	}

	public Vector3 getPosition() {
		return light.position;
	}

	public void setPosition(Vector3 position) {
		this.position.set(position);
		light.position.set(position);
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
				light.position.setZero().add(position);
				light.direction.setZero().add(direction);
			} else {
				transformComponent.getWorldTranslation(light.position);
				transformComponent.localToWorld(light.direction).sub(position);
				light.position.add(position);
			}
			position.set(light.position);
			direction.set(light.direction);
		}
	}
}
