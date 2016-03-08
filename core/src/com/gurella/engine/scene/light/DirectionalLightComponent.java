package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.PropertyDescriptor;

public class DirectionalLightComponent extends LightComponent<DirectionalLight> {
	@PropertyDescriptor
	private final Vector3 direction = new Vector3();

	@Override
	protected DirectionalLight createLight() {
		return new DirectionalLight();
	}

	public Vector3 getDirection(Vector3 out) {
		return out.set(direction);
	}

	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
		light.direction.set(direction);
	}
}
