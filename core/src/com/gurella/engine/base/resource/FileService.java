package com.gurella.engine.base.resource;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Uuid;

//TODO unused
public class FileService {
	private static final ObjectMap<String, String> pathToUuid = new ObjectMap<String, String>();
	private static final ObjectMap<String, String> uuidToPath = new ObjectMap<String, String>();

	private FileService() {
	}

	public String getUuid(String path) {
		String uuid = pathToUuid.get(path);
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
			pathToUuid.put(path, uuid);
			uuidToPath.put(uuid, path);
		}
		return uuid;
	}

	public static String getPath(String uuid) {
		return uuidToPath.get(uuid);
	}

	public static void pathChanged(String oldPath, String newPath) {
		String uuid = pathToUuid.remove(oldPath);
		if (uuid != null) {
			pathToUuid.put(newPath, uuid);
			uuidToPath.put(uuid, newPath);
		}
	}

	public static void removePath(String path) {
		uuidToPath.remove(pathToUuid.remove(path));
	}
}
