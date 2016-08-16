package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public final class FileService {
	private static final ObjectMap<String, String> fileNameToUuid = new ObjectMap<String, String>();
	private static final ObjectMap<String, String> uuidToFileName = new ObjectMap<String, String>();

	private FileService() {
	}

	public static String getUuid(String fileName) {
		if (Values.isBlank(fileName)) {
			throw new IllegalArgumentException("fileName must not be blank.");
		}

		String uuid = fileNameToUuid.get(fileName);
		if (uuid == null) {
			uuid = Uuid.randomUuidString();
			fileNameToUuid.put(fileName, uuid);
			uuidToFileName.put(uuid, fileName);
		}
		return uuid;
	}

	public static String getFileName(String uuid) {
		return uuidToFileName.get(uuid);
	}

	public static void fileNameChanged(String oldFileName, String newFileName) {
		String uuid = fileNameToUuid.remove(oldFileName);
		if (uuid != null) {
			fileNameToUuid.put(newFileName, uuid);
			uuidToFileName.put(uuid, newFileName);
		}
	}

	public static void removeFileName(String fileName) {
		uuidToFileName.remove(fileNameToUuid.remove(fileName));
	}

	public static void addMapping(String fileName, String uuid) {
		fileNameToUuid.put(fileName, uuid);
		uuidToFileName.put(uuid, fileName);
	}
}
