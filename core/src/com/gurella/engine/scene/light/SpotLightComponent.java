package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;

public class SpotLightComponent extends LightComponent<SpotLight> {
	private final Vector3 position = new Vector3();
	private final Vector3 direction = new Vector3(0, -1, 0);
	private float intensity = 0.1f;
	private float cutoffAngle = 1;
	private float exponent = 1;

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
}
