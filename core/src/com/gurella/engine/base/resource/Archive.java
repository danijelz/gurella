package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.utils.IdentitySet;

//TODO unused
public class Archive<T> implements Poolable {
	T rootValue;
	final IdentitySet<ManagedObject> managedObjects = new IdentitySet<ManagedObject>();

	@Override
	public void reset() {
		rootValue = null;
		managedObjects.reset();
	}
}
