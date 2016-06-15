package com.gurella.engine.asset;

import java.util.Arrays;
import java.util.StringTokenizer;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

	public static FileHandle getFileHandle(String path) {
		boolean hasFileTypeInfo = hasFileTypeInfo(path);
		char fileTypeInfo = hasFileTypeInfo ? path.charAt(1) : 'i';
		String pathExtract = hasFileTypeInfo ? path.substring(3) : path;
		Files files = Gdx.files;
		switch (fileTypeInfo) {
		case 'i':
			return files.internal(pathExtract);
		case 'c':
			return files.classpath(pathExtract);
		case 'e':
			return files.external(pathExtract);
		case 'a':
			return files.absolute(pathExtract);
		case 'l':
			return files.local(pathExtract);
		default:
			return files.internal(pathExtract);
		}
	}

	private static boolean hasFileTypeInfo(String path) {
		if (path.length() < 4) {
			return false;
		}

		char fileTypeInfo = path.charAt(1);
		return path.charAt(0) == '{' && path.charAt(2) == '}' && (fileTypeInfo == 'c' || fileTypeInfo == 'i'
				|| fileTypeInfo == 'e' || fileTypeInfo == 'a' || fileTypeInfo == 'l');
	}

	public static FileHandle getRelativeFileHandle(FileHandle file, String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
		FileHandle result = file.parent();
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if (token.equals(".."))
				result = result.parent();
			else {
				result = result.child(token);
			}
		}
		return result;
	}
}
