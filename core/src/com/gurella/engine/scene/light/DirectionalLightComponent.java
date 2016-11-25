package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.ModelDescriptor;

@ModelDescriptor(descriptiveName = "Directional Light")
public class DirectionalLightComponent extends LightComponent<DirectionalLight> {

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
		light.direction.set(direction);
	}

	@Override
	public void reset() {
		super.reset();
		light.direction.set(0, -1, 0);
	}
}
