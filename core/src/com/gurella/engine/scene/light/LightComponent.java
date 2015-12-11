package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.PropertyValue;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene.BaseSceneElementType;
import com.gurella.engine.scene.SceneNodeComponent;

@BaseSceneElementType
public abstract class LightComponent<T extends BaseLight> extends SceneNodeComponent {
	@TransientProperty
	T light;

	@ResourceProperty
	@DefaultValue(compositeValues = { @PropertyValue(name = "a", floatValue = 1) })
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
