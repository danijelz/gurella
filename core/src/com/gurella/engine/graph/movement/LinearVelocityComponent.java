package com.gurella.engine.graph.movement;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.resource.model.ResourceProperty;

public class LinearVelocityComponent extends SceneNodeComponent {
	public final Vector3 lastPosition = new Vector3();
	@ResourceProperty
	public final Vector3 velocity = new Vector3();
}
