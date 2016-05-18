package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class PointLightComponent extends LightComponent<PointLight> {
	private final Vector3 position = new Vector3();
	private float intensity;

	@Override
	protected PointLight createLight() {
		return new PointLight();
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
}
