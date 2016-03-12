package com.gurella.engine.base.object;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
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
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public boolean isFlat() {
		return true;
	}

	@Override
	public boolean isCopyable() {
		return false;
	}

	@Override
	public String getDescriptiveName() {
		return name;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getGroup() {
		return null;
	}

	@Override
	public Property<String> newInstance(Model<?> model) {
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
