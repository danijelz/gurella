package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

public class PointLightComponent extends LightComponent<PointLight> implements NodeComponentActivityListener,
		NodeTransformChangedListener, PreRenderUpdateListener, PropertyChangeListener {
	@SuppressWarnings("unused")
	private float intensity = 0.1f;

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected PointLight createLight() {
		PointLight pointLight = new PointLight();
		pointLight.intensity = 0.1f;
		return pointLight;
	}

	public float getIntensity() {
		return light.intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		light.intensity = intensity;
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
			if (transformComponent == null) {
				light.position.setZero();
			} else {
				transformComponent.getWorldTranslation(light.position);
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
