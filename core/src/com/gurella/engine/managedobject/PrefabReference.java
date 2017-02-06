package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.serialization.Reference;
import com.gurella.engine.utils.Uuid;

//TODO remove
@MetaTypeDescriptor(metaType = PrefabReferenceMetaType.class)
public final class PrefabReference implements Reference, Poolable {
	String uuid;
	String fileName;
	ManagedObject prefab;

	public static PrefabReference obtain(String uuid, String fileName, ManagedObject prefab) {
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}

		PrefabReference prefabReference = PoolService.obtain(PrefabReference.class);
		prefabReference.uuid = uuid;
		prefabReference.fileName = fileName;
		prefabReference.prefab = prefab;
		return prefabReference;
	}

	static PrefabReference obtain() {
		return PoolService.obtain(PrefabReference.class);
	}

	PrefabReference() {
	}

	public PrefabReference(String uuid, String fileName) {
		if (!Uuid.isValid(uuid)) {
			throw new IllegalArgumentException("Invalid uuid: " + uuid);
		}
		this.fileName = fileName;
		this.uuid = uuid;
	}

	PrefabReference(String uuid, String fileName, ManagedObject prefab) {
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
