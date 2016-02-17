package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.Pool.Poolable;

public class ResourceReference implements Poolable {
	public String pathUuid;
	public Class<?> type;
	
	@Override
	public void reset() {
		pathUuid = null;
		type = null;
	}
}
