package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.scene.SceneNodeComponent;

public class AudioListenerComponent extends SceneNodeComponent {
	@ResourceProperty
	public final Vector3 up = new Vector3();
	@ResourceProperty
	public final Vector3 lookAt = new Vector3();
}
