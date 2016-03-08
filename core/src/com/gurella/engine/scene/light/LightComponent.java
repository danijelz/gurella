package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;

@BaseSceneElement
public abstract class LightComponent<T extends BaseLight<T>> extends SceneNodeComponent2 {
	transient T light;
	final Color color = new Color(0, 0, 0, 1);

	public LightComponent() {
		light = createLight();
	}

	protected abstract T createLight();

	public Color getColor(Color out) {
		return out.set(color);
	}

	public void setColor(Color color) {
		this.color.set(color);
		light.color.set(color);
	}
}
