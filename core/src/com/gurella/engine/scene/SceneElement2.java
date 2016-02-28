package com.gurella.engine.scene;

import com.gurella.engine.base.object.ManagedObject;

public class SceneElement2 extends ManagedObject {
	boolean enabled = true;
	Scene scene;

	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		boolean active = isActive();
		if (enabled && !active) {
			activate();
		} else if (!enabled && active) {
			deactivate();
		}
	}

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && scene != null && enabled;
	}

	@Override
	protected void reset() {
		super.reset();
		enabled = true;
		scene = null;
	}
}
