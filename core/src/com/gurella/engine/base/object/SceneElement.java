package com.gurella.engine.base.object;

public class SceneElement extends ManagedObject {
	boolean enabled = true;

	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	protected boolean isActivationAllowed() {
		return super.isActivationAllowed() && enabled;
	}
}
