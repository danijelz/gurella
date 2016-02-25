package com.gurella.engine.base.object;

public class SceneElement extends ManagedObject {
	boolean enabled = true;

	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		boolean active = isActive();
		if(enabled && !active) {
			handleActivation();
		} else if(!enabled && active) {
			handleDeactivation();
		}
	}
	
	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && enabled;
	}
	
	@Override
	protected void reset() {
		super.reset();
		enabled = true;
	}
}
