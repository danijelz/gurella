package com.gurella.engine.base.resource;

public class BeanReference extends ResourceReference {
	public String objectUuid;
	
	@Override
	public void reset() {
		super.reset();
		objectUuid = null;
	}
}
