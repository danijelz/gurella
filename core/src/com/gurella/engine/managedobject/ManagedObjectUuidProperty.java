package com.gurella.engine.managedobject;

import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.Range;

class ManagedObjectUuidProperty implements Property<String> {
	public static final String name = "uuid";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<String> getType() {
		return String.class;
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
	public boolean isFlatSerialization() {
		return true;
	}

	@Override
	public boolean isCopyable() {
		return false;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Property<String> newInstance(MetaType<?> owner) {
		return this;
	}

	@Override
	public String getValue(Object object) {
		return ((ManagedObject) object).uuid;
	}

	@Override
	public void setValue(Object object, String value) {
		((ManagedObject) object).uuid = value;
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		output.writeStringProperty(name, ((ManagedObject) object).ensureUuid());
	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		if (input.hasProperty(name)) {
			((ManagedObject) object).uuid = input.readStringProperty(name);
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
	}
}
