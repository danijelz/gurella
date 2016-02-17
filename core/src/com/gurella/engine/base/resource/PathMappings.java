package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Uuid;

public class PathMappings {
	private static final ObjectMap<String, Uuid> pathToUuid = new ObjectMap<String, Uuid>();
	private static final ObjectMap<Uuid, String> uuidToPath = new ObjectMap<Uuid, String>();

	private PathMappings() {
	}

	public Uuid getPathUuid(String path) {
		Uuid uuid = pathToUuid.get(path);
		if (uuid == null) {
			uuid = Uuid.randomUuid();
			pathToUuid.put(path, uuid);
			uuidToPath.put(uuid, path);
		}
		return uuid;
	}

	public static String getPath(Uuid uuid) {
		return uuidToPath.get(uuid);
	}

	public static void pathChanged(String oldPath, String newPath) {
		Uuid uuid = pathToUuid.remove(oldPath);
		pathToUuid.put(newPath, uuid);
		uuidToPath.put(uuid, newPath);
	}
}
