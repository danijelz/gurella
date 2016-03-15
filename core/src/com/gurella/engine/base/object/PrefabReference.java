package com.gurella.engine.base.object;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.resource.FileService;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Uuid;

@ModelDescriptor(model = PrefabReferenceModel.class)
public final class PrefabReference implements Poolable {
	String fileUuid;
	String uuid;
	transient ManagedObject prefab;

	public static PrefabReference obtain(String fileUuid, String uuid) {
		if (!Uuid.isValid(fileUuid)) {
			throw new IllegalArgumentException("Invalid fileUuid: " + fileUuid);
		}
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}

		PrefabReference prefab = PoolService.obtain(PrefabReference.class);
		prefab.fileUuid = fileUuid;
		prefab.uuid = uuid;
		return prefab;
	}

	static PrefabReference obtain() {
		return PoolService.obtain(PrefabReference.class);
	}

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

	PrefabReference(String fileUuid, String uuid, ManagedObject prefab) {
		this.fileUuid = fileUuid;
		this.uuid = uuid;
		this.prefab = prefab;
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public ManagedObject get() {
		if (prefab == null) {
			prefab = ResourceService.get(FileService.getFileName(fileUuid)/* TODO, uuid*/);
		}
		return prefab;
	}

	@Override
	public void reset() {
		fileUuid = null;
		uuid = null;
		prefab = null;
	}

	public void free() {
		PoolService.free(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * fileUuid.hashCode() + prime * uuid.hashCode();
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
		return "fileUuid: " + fileUuid + " filePath: " + FileService.getFileName(fileUuid) + " uuid: " + uuid;
	}
}
