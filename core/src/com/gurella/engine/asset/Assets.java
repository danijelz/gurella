package com.gurella.engine.asset;

import java.util.Arrays;

import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.utils.Values;

public class Assets {
	private static final ObjectSet<Class<?>> assetTypes = new ObjectSet<Class<?>>();

	static {
		AssetType[] values = AssetType.values();
		for (int i = 0; i < values.length; i++) {
			Class<?> assetType = values[i].assetType;
			if (assetType != null) {
				assetTypes.add(assetType);
			}
		}
	}

	private Assets() {
	}

	public static boolean isAsset(Object obj) {
		return obj != null && isAssetType(obj.getClass());
	}

	public static boolean isAssetType(Class<?> type) {
		Class<?> temp = type;
		while (temp != null && temp != Object.class) {
			if (assetTypes.contains(temp)) {
				return true;
			}
			temp = temp.getSuperclass();
		}
		return false;
	}

	public static String getFileExtension(final String fileName) {
		if (fileName == null) {
			return "";
		} else {
			int index = fileName.lastIndexOf('.');
			return index > 0 ? fileName.substring(index + 1) : "";
		}
	}

	public static <T> Class<T> getAssetType(final String fileName) {
		String extension = getFileExtension(fileName);
		if (Values.isBlank(extension)) {
			return null;
		}
		
		extension = extension.toLowerCase();
		AssetType[] values = AssetType.values();
		for (int i = 0; i < values.length; i++) {
			if (Arrays.binarySearch(values[i].extensions, extension) > -1) {
				return Values.cast(values[i].assetType);
			}
		}
		return null;
	}
}
