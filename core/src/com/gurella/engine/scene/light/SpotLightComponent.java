package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.metatype.PropertyChangeListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.light.debug.LightDebugRenderer;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

@MetaTypeDescriptor(descriptiveName = "Spot Light")
public class SpotLightComponent extends LightComponent<SpotLight> implements NodeComponentActivityListener,
		NodeTransformChangedListener, PreRenderUpdateListener, PropertyChangeListener, DebugRenderable {

	private transient TransformComponent transformComponent;
	private transient boolean dirty = true;

	@Override
	protected SpotLight createLight() {
		SpotLight spotLight = new SpotLight();
		spotLight.intensity = 0.1f;
		spotLight.cutoffAngle = 1;
		spotLight.exponent = 1;
		spotLight.direction.set(0, -1, 0);
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
		return light.direction;
	}

	public void setDirection(Vector3 direction) {
		light.direction.set(direction);
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
	public void onNodeComponentActivated(SceneNodeComponent component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
			dirty = true;
		}
	}

	@Override
	public void onNodeComponentDeactivated(SceneNodeComponent component) {
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

	@Override
	public void debugRender(DebugRenderContext context) {
		LightDebugRenderer.render(context, this);
	}

	@Override
	public void reset() {
		super.reset();
		light.intensity = 0.1f;
		light.cutoffAngle = 1;
		light.exponent = 1;
		light.direction.set(0, -1, 0);
	}

	@Override
	public boolean equalAs(Object other) {
		if (other == this) {
			return true;
		} else if (other instanceof SpotLightComponent) {
			SpotLight otherLight = ((SpotLightComponent) other).light;
			return light.color.equals(otherLight.color) && light.direction.equals(otherLight.direction)
					&& light.intensity == otherLight.intensity && light.cutoffAngle == otherLight.cutoffAngle
					&& light.exponent == otherLight.exponent;
		} else {
			return false;
		}
	}
}
