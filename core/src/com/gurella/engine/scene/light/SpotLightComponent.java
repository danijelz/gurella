package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

@ModelDescriptor(descriptiveName = "Spot Light")
public class SpotLightComponent extends LightComponent<SpotLight> implements NodeComponentActivityListener,
		NodeTransformChangedListener, PreRenderUpdateListener, PropertyChangeListener {
	private final Vector3 direction = new Vector3(0, -1, 0);

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected SpotLight createLight() {
		SpotLight spotLight = new SpotLight();
		spotLight.intensity = 0.1f;
		spotLight.cutoffAngle = 1;
		spotLight.exponent = 1;
		return spotLight;
	}

	public float getIntensity() {
		return light.intensity;
	}

	public void setIntensity(float intensity) {
		light.intensity = intensity;
	}

	public float getCutoffAngle() {
		return light.cutoffAngle;
	}

	public void setCutoffAngle(float cutoffAngle) {
		light.cutoffAngle = cutoffAngle;
	}

	public float getExponent() {
		return light.exponent;
	}

	public void setExponent(float exponent) {
		light.exponent = exponent;
	}

	public Vector3 getDirection() {
		return direction;
	}

	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
		dirty = true;
	}

	@Override
	protected void componentActivated() {
		transformComponent = getNode().getComponent(TransformComponent.class);
	}

	@Override
	protected void componentDeactivated() {
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

			if (transformComponent == null) {
				light.position.setZero();
			} else {
				transformComponent.getWorldTranslation(light.position);
				transformComponent.transformPointToWorld(light.direction).sub(light.position);
			}
		}
	}

	@Override
	public void propertyChanged(String propertyName, Object oldValue, Object newValue) {
		dirty = true;
	}

	public Matrix4 getTransform(Matrix4 out) {
		return transformComponent == null ? out.idt() : transformComponent.getWorldTransform(out);
	}
}
