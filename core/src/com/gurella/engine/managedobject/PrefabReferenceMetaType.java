package com.gurella.engine.managedobject;

import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.DefaultMetaType.SimpleObjectMetaType;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.Values;

class PrefabReferenceMetaType extends SimpleObjectMetaType<PrefabReference> {
	public PrefabReferenceMetaType() {
		super(PrefabReference.class);
	}

	@Override
	protected void writeValue(PrefabReference value, Output output) {
		String fileName = value.fileName;
		String uuid = value.uuid;
		output.writeString(Values.isBlank(fileName) ? uuid : (uuid + " " + fileName));
	}

	@Override
	protected PrefabReference readValue(Input input) {
		String simpleValue = input.readString();
		String[] components = simpleValue.split(" ");
		String uuid = components[0];
		String fileName = components.length == 1 ? null : components[1];
		return PrefabReference.obtain(uuid, fileName);
	}

	@Override
	public PrefabReference copy(PrefabReference original, CopyContext context) {
		return PrefabReference.obtain(original.fileName, original.uuid, original.prefab);
	}
}
