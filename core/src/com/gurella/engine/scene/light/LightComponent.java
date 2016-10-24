package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;

//TODO Poolable
@BaseSceneElement
public abstract class LightComponent<T extends BaseLight<T>> extends SceneNodeComponent2 {
	transient T light;

	public LightComponent() {
		light = createLight();
		light.setColor(1, 1, 1, 1);
	}

	protected abstract T createLight();

	public T getLight() {
		return light;
	}

	public Color getColor() {
		return light.color;
	}

	public void setColor(Color color) {
		light.color.set(color);
	}
}
