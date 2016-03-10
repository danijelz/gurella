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
		PrefabReference prefabReference = new PrefabReference();
		prefabReference.fileUuid = components.length == 2 ? components[0] : null;
		prefabReference.uuid = components.length == 2 ? components[1] : components[0];
		return prefabReference;
	}

	@Override
	public PrefabReference copy(PrefabReference original, CopyContext context) {
		PrefabReference copy = new PrefabReference();
		copy.fileUuid = original.fileUuid;
		copy.uuid = original.uuid;
		return copy;
	}
}
