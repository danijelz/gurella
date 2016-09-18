package com.gurella.engine.scene;

import com.gurella.engine.base.object.ManagedObject;

public class SceneElement2 extends ManagedObject {
	transient Scene scene;

	boolean enabled = true;

	@Override
	protected boolean isActivationAllowed() {
		return super.isActivationAllowed() && enabled && scene != null;
	}

	public final Scene getScene() {
		return scene;
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;
		boolean active = isActive();
		if (enabled && !active) {
			activate();
		} else if (!enabled && active) {
			deactivate();
		}
	}

	public final void enable() {
		setEnabled(true);
	}

	public final void disable() {
		setEnabled(false);
	}

	@Override
	protected final void destroyed() {
		enabled = true;
		elementDestroyed();
	}

	protected void elementDestroyed() {
	}
}
