package com.gurella.engine.base.object;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.utils.ImmutableArray;

public final class Prefabs {
	private Prefabs() {
	}

	public static <T extends ManagedObject> T convertToPrefab(T object, String fileName) {
		T prefab = CopyContext.copyObject(object);
		setPrefab(object, prefab, fileName);
		return prefab;
	}

	public static <T extends ManagedObject> void convertToPrefab(T object, T prefab, String fileName) {
		setPrefab(object, prefab, fileName);
	}

	private static <T extends ManagedObject> void setPrefab(T object, T prefab, String fileName) {
		object.prefab = new PrefabReference(fileName, prefab.ensureUuid(), prefab);
		ImmutableArray<ManagedObject> children = object.children;
		ImmutableArray<ManagedObject> prefabChildren = prefab.children;
		for (int i = 0, n = children.size(); i < n; i++) {
			ManagedObject child = children.get(i);
			ManagedObject prefabChild = prefabChildren.get(i);
			setPrefab(child, prefabChild, fileName);
		}
	}

	public static <T extends ManagedObject> void dettachFromPrefab(T object) {
		ManagedObject prefab = getPrefab(object);
		if (prefab == null) {
			return;
		}

		dettachFromPrefab(object, prefab);
	}

	private static ManagedObject getPrefab(ManagedObject object) {
		PrefabReference prefabReference = object.getPrefab();
		return prefabReference == null ? null : prefabReference.get();
	}

	private static <T extends ManagedObject> void dettachFromPrefab(T object, T prefab) {
		object.prefab = null;
		ImmutableArray<ManagedObject> children = object.children;
		ImmutableArray<ManagedObject> prefabChildren = prefab.children;
		for (int i = 0; i < children.size(); i++) {
			ManagedObject child = children.get(i);
			ManagedObject prefabChild = findPrefabChild(child, prefabChildren);
			if (prefabChild != null) {
				dettachFromPrefab(child, prefabChild);
			}
		}
	}

	private static ManagedObject findPrefabChild(ManagedObject child, ImmutableArray<ManagedObject> prefabChildren) {
		String uuid = child.getUuid();
		if (uuid == null) {
			return null;
		}

		ManagedObject prefab = getPrefab(child);
		if (prefab == null) {
			return null;
		}

		for (int i = 0, n = prefabChildren.size(); i < n; i++) {
			ManagedObject prefabChild = prefabChildren.get(i);
			String prefabUuid = prefab.getUuid();
			if (prefabUuid != null && prefabUuid.equals(uuid)) {
				return prefabChild;
			}
		}

		return null;
	}
}
