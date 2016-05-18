package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;

public class SpotLightComponent extends LightComponent<SpotLight> {
	private final Vector3 position = new Vector3();
	private final Vector3 direction = new Vector3();
	private float intensity;
	private float cutoffAngle;
	private float exponent;

	@Override
	protected SpotLight createLight() {
		return new SpotLight();
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
}
