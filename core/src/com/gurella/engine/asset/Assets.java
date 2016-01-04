package com.gurella.engine.asset;

import com.badlogic.gdx.utils.ObjectSet;

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
}
