package com.gurella.engine.scene.movement;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.PropertyValue;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.scene.SceneNodeComponent;

public class LinearVelocityComponent extends SceneNodeComponent {
	@ResourceProperty
	@DefaultValue(compositeValues = { @PropertyValue(name = "x", floatValue = Float.NaN),
			@PropertyValue(name = "y", floatValue = Float.NaN), @PropertyValue(name = "z", floatValue = Float.NaN) })
	public final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	@ResourceProperty
	public final Vector3 velocity = new Vector3();
}
