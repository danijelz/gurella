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
		String fileUuid = value.fileName;
		String uuid = value.uuid;
		output.writeString(Values.isBlank(fileUuid) ? uuid : (fileUuid + " " + uuid));
	}

	@Override
	protected PrefabReference readValue(Input input) {
		String simpleValue = input.readString();
		String[] components = simpleValue.split(" ");
		return PrefabReference.obtain(components[0], components[1]);
	}

	@Override
	public PrefabReference copy(PrefabReference original, CopyContext context) {
		return PrefabReference.obtain(original.fileName, original.uuid);
	}
}
