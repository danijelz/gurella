package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

public class DirectionalLightComponent extends LightComponent<DirectionalLight> implements
		NodeComponentActivityListener, NodeTransformChangedListener, PreRenderUpdateListener, PropertyChangeListener {
	private final Vector3 direction = new Vector3(0, -1, 0);

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected DirectionalLight createLight() {
		return new DirectionalLight();
	}

	public Vector3 getDirection() {
		return direction;
	}

	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
		dirty = true;
	}

	@Override
	protected void onActivate() {
		transformComponent = getNode().getComponent(TransformComponent.class);
	}

	@Override
	protected void onDeactivate() {
		transformComponent = null;
		dirty = true;
	}

	@Override
	public void nodeComponentActivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
			dirty = true;
		}
	}

	@Override
	public void nodeComponentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof TransformComponent) {
			transformComponent = null;
			dirty = true;
		}
	}

	@Override
	public void onNodeTransformChanged() {
		dirty = true;
	}

	@Override
	public void onPreRenderUpdate() {
		if (dirty) {
			dirty = false;
			light.direction.set(direction);
			if (transformComponent != null) {
				float x = direction.x;
				float y = direction.y;
				float z = direction.z;
				transformComponent.getWorldTranslation(direction);
				transformComponent.transformPointToWorld(light.direction).sub(direction);
				direction.set(x, y, z);
			}
		}
	}

	@Override
	public void propertyChanged(PropertyChangeEvent event) {
		Array<Object> propertyPath = event.propertyPath;
		if (!dirty && propertyPath.peek() == this) {
			dirty = true;
		}
	}
}
