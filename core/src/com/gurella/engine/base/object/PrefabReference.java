package com.gurella.engine.base.object;

import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.resource.FileService;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.utils.Uuid;

@ModelDescriptor(model = PrefabReferenceModel.class)
public final class PrefabReference {
	String fileUuid;
	String uuid;
	transient ManagedObject prefab;

	PrefabReference() {
	}

	public PrefabReference(String fileUuid, String uuid) {
		if (!Uuid.isValid(fileUuid)) {
			throw new IllegalArgumentException("Invalid fileUuid: " + fileUuid);
		}
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}
		this.fileUuid = fileUuid;
		this.uuid = uuid;
	}

	public PrefabReference(ManagedObject prefab) {
		this.prefab = prefab;
		fileUuid = FileService.getUuid(ResourceService.getFileName(prefab));
		uuid = prefab.ensureUuid();
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public ManagedObject get() {
		if (prefab == null) {
			// String path = FileService.getPath(fileUuid);
			// TODO prefab = ResourceService.get(FileService.getPath(fileUuid), uuid);
		}
		return prefab;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((fileUuid == null) ? 0 : fileUuid.hashCode());
		return prime * result + uuid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		PrefabReference other = (PrefabReference) obj;
		return fileUuid.equals(other.fileUuid) && uuid.equals(other.uuid);
	}

	@Override
	public String toString() {
		return fileUuid + " " + uuid;
	}
}
