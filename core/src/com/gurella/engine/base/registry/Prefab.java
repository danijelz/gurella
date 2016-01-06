package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.ReferenceProperty;

//TODO unused
public class Prefab {
	private ManagedObject object;
	private final Array<ReferenceProperty<?>> referenceProperties = new Array<ReferenceProperty<?>>();

	public ManagedObject instantiate() {
		Model<ManagedObject> model = Models.getModel(object.getClass());
	}
}
