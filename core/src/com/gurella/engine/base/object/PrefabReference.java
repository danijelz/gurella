package com.gurella.engine.base.object;

import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.resource.FileService;
import com.gurella.engine.utils.Values;

@ModelDescriptor(model = PrefabReferenceModel.class)
public final class PrefabReference {
	String fileUuid;
	String uuid;
	transient ManagedObject prefab;

	public String getFileUuid() {
		return fileUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public ManagedObject get() {
		if (prefab == null) {
			String path = FileService.getPath(fileUuid);
			// TODO prefab = ResourceService.get(FileService.getPath(fileUuid), uuid);
		}
		return prefab;
	}

	@Override
	public String toString() {
		return Values.isBlank(fileUuid) ? uuid : (fileUuid + " " + uuid);
	}
}
