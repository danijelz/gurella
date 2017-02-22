package com.gurella.engine.scene.light;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.metatype.PropertyChangeListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.light.debug.LightDebugRenderer;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

@MetaTypeDescriptor(descriptiveName = "Point Light")
public class PointLightComponent extends LightComponent<PointLight> implements NodeComponentActivityListener,
		NodeTransformChangedListener, PreRenderUpdateListener, PropertyChangeListener, DebugRenderable {

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
		light.intensity = intensity;
	}

	@Override
	protected void componentActivated() {
		transformComponent = getNode().getComponent(TransformComponent.class, false);
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
		dirty = true;
	}

	@Override
	public boolean equalAs(Object other) {
		if (other == this) {
			return true;
		} else if (other instanceof PointLightComponent) {
			PointLight otherLight = ((PointLightComponent) other).light;
			return light.color.equals(otherLight.color) && light.intensity == otherLight.intensity;
		} else {
			return false;
		}
	}
}
