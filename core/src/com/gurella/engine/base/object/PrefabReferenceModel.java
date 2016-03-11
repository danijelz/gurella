package com.gurella.engine.base.object;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.DefaultModels.SimpleObjectModel;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Values;

class PrefabReferenceModel extends SimpleObjectModel<PrefabReference> {
	public PrefabReferenceModel() {
		super(PrefabReference.class);
	}

	@Override
	protected void writeValue(PrefabReference value, Output output) {
		String fileUuid = value.fileUuid;
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
		return PrefabReference.obtain(original.fileUuid, original.uuid);
	}
}
