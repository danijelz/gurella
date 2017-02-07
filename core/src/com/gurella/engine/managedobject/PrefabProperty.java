package com.gurella.engine.managedobject;

import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Values;

class PrefabProperty implements Property<ManagedObject> {
	public static final String name = "prefab";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<ManagedObject> getType() {
		return ManagedObject.class;
	}

	@Override
	public Range<?> getRange() {
		return null;
	}

	@Override
	public boolean isAsset() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public boolean isFinal() {
		return false;
	}

	@Override
	public boolean isCopyable() {
		return true;
	}

	@Override
	public boolean isFlatSerialization() {
		return true;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Property<ManagedObject> newInstance(MetaType<?> owner) {
		return this;
	}

	@Override
	public ManagedObject getValue(Object object) {
		return ((ManagedObject) object).prefab;
	}

	@Override
	public void setValue(Object object, ManagedObject value) {
		((ManagedObject) object).prefab = value;
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		ManagedObject value = ((ManagedObject) object).prefab;
		if (value == null && template == null) {
			return;
		}

		ManagedObject templateValue = ((ManagedObject) template).prefab;
		if (Values.isEqual(value, templateValue)) {
			return;
		} else if (value == null) {
			output.writeNullProperty(name);
		} else {
			output.writeObjectProperty(name, ManagedObject.class, value, templateValue);
		}
	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		if (input.hasProperty(name)) {
			((ManagedObject) object).prefab = input.readObjectProperty(name, ManagedObject.class, null);
		} else if (template != null) {
			((ManagedObject) object).prefab = ((ManagedObject) template).prefab;
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		ManagedObject originalObj = (ManagedObject) original;
		ManagedObject duplicateObj = (ManagedObject) duplicate;
		duplicateObj.prefab = originalObj.prefab;
	}
}
