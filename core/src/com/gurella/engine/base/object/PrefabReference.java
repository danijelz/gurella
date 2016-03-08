package com.gurella.engine.base.object;

//TODO unused
public class PrefabReference {
	String fileUuid;
	String uuid;
	transient ManagedObject prefab;

	public String getFileUuid() {
		return fileUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public ManagedObject getPrefab() {
		if (prefab == null) {
			//TODO prefab = ResourceService.get(FileService.getPath(fileUuid), uuid);
		}
		return prefab;
	}
}
