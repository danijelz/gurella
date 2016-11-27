package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.metatype.ModelDescriptor;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.serialization.Reference;
import com.gurella.engine.utils.Uuid;

@ModelDescriptor(model = PrefabReferenceModel.class)
public final class PrefabReference implements Reference, Poolable {
	String fileName;
	String uuid;
	transient ManagedObject prefab;

	public static PrefabReference obtain(String fileUuid, String uuid) {
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}

		PrefabReference prefab = PoolService.obtain(PrefabReference.class);
		prefab.fileName = fileUuid;
		prefab.uuid = uuid;
		return prefab;
	}

	static PrefabReference obtain() {
		return PoolService.obtain(PrefabReference.class);
	}

	PrefabReference() {
	}

	public PrefabReference(String fileName, String uuid) {
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}
		this.fileName = fileName;
		this.uuid = uuid;
	}

	PrefabReference(String fileName, String uuid, ManagedObject prefab) {
		this.fileName = fileName;
		this.uuid = uuid;
		this.prefab = prefab;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public Class<?> getValueType() {
		return ManagedObject.class;
	}

	public String getUuid() {
		return uuid;
	}

	public ManagedObject get() {
		if (prefab == null) {
			prefab = AssetService.get(fileName, uuid);
		}
		return prefab;
	}

	@Override
	public void reset() {
		fileName = null;
		uuid = null;
		prefab = null;
	}

	public void free() {
		PoolService.free(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * fileName.hashCode() + prime * uuid.hashCode();
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
		return fileName.equals(other.fileName) && uuid.equals(other.uuid);
	}

	@Override
	public String toString() {
		return "fileName: " + fileName + " uuid: " + uuid;
	}
}
