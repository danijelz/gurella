package com.gurella.engine.asset2;

import java.util.Arrays;
import java.util.StringTokenizer;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.AssetType;
import com.gurella.engine.asset.properties.AssetProperties;
import com.gurella.engine.utils.Values;

public class Assets {
	public static final String filePathDelimiters = "\\/";
	public static final char homePathAlias = '~';
	public static final char unixFilePathDelimiter = '/';
	public static final char windowsFilePathDelimiter = '\\';
	public static final char windowsDriveLetterDelimiter = ':';
	public static final char fileExtensionDelimiter = '.';
	public static final char localFileType = 'l';
	public static final char absoluteFileType = 'a';
	public static final char externalFileType = 'e';
	public static final char classpathFileType = 'c';
	public static final char internalFileType = 'i';
	public static final char fileTypeInfoDelimiter = '|';

	private static final ObjectSet<Class<?>> assetTypes = new ObjectSet<Class<?>>();
	private static final ObjectSet<Class<?>> nonAssetTypes = new ObjectSet<Class<?>>();

	private static final AssetType[] assetTypeEnums = AssetType.values();
	private static final ObjectMap<Class<?>, AssetType> enumsByType = new ObjectMap<Class<?>, AssetType>();

	static {
		for (int i = 0, n = assetTypeEnums.length; i < n; i++) {
			AssetType assetType = assetTypeEnums[i];
			Class<?> type = assetType.assetType;
			assetTypes.add(type);
			enumsByType.put(type, assetType);
		}
	}

	private Assets() {
	}

	public static boolean hasAssetType(Object obj) {
		return obj != null && isAssetType(obj.getClass());
	}

	public static boolean isAssetType(Class<?> type) {
		if (assetTypes.contains(type)) {
			return true;
		} else if (nonAssetTypes.contains(type)) {
			return false;
		}

		initTypeInfo(type);
		return assetTypes.contains(type);
	}

	private static AssetType initTypeInfo(Class<?> type) {
		synchronized (assetTypes) {
			for (int i = 0, n = assetTypeEnums.length; i < n; i++) {
				AssetType assetType = assetTypeEnums[i];
				if (ClassReflection.isAssignableFrom(assetType.assetType, type)) {
					assetTypes.add(type);
					enumsByType.put(type, assetType);
					return assetType;
				}
			}

			nonAssetTypes.add(type);
			return null;
		}
	}

	public static boolean hasFileExtension(final String fileName) {
		if (fileName == null) {
			return false;
		} else {
			int index = fileName.lastIndexOf(fileExtensionDelimiter);
			return index > 0 && index < fileName.length() - 1;
		}
	}

	public static String getFileExtension(final String fileName) {
		if (fileName == null) {
			return "";
		} else {
			int index = fileName.lastIndexOf(fileExtensionDelimiter);
			return index > 0 && index < fileName.length() - 1 ? fileName.substring(index + 1) : "";
		}
	}

	public static boolean hasValidExtension(Class<?> assetType, String fileName) {
		String fileExtension = getFileExtension(fileName);
		return Values.isNotBlank(fileExtension) && isValidExtension(assetType, fileExtension);
	}

	public static boolean isValidExtension(Class<?> assetType, String extension) {
		AssetType type = enumsByType.get(assetType);
		return type != null && Arrays.binarySearch(type.fileExtensions, extension) >= 0;
	}

	public static AssetType getAssetType(final Class<?> assetType) {
		return enumsByType.get(assetType);
	}

	public static AssetType getAssetType(final String fileName) {
		String extension = getFileExtension(fileName);
		if (Values.isBlank(extension)) {
			return null;
		}

		extension = extension.toLowerCase();
		for (int i = 0, n = assetTypeEnums.length; i < n; i++) {
			AssetType type = assetTypeEnums[i];
			if (type.isValidExtension(extension)) {
				return type;
			}
		}
		return null;
	}

	public static <T> Class<T> getAssetClass(final String fileName) {
		AssetType type = getAssetType(fileName);
		return type == null ? null : Values.<Class<T>> cast(type.assetType);
	}

	public static <T> Class<T> getAssetRootClass(final Object asset) {
		Class<? extends Object> type = asset.getClass();
		AssetType assetType = enumsByType.get(type);
		if (assetType == null) {
			assetType = initTypeInfo(type);
		}

		@SuppressWarnings("unchecked")
		Class<T> casted = (Class<T>) assetType.assetType;
		return casted;
	}

	public static String getPropertiesFileName(String assetFileName) {
		AssetType assetType = getAssetType(assetFileName);
		if (assetType != null && assetType.propsType != null) {
			return assetFileName + '.' + AssetType.assetProperties.extension();
		} else {
			return null;
		}
	}

	public static String getPropertiesFileName(Object asset) {
		AssetType assetType = getAssetType(asset.getClass());
		if (assetType != null && assetType.propsType != null) {
			String assetFileName = AssetService.getFileName(asset);
			return assetFileName + '.' + AssetType.assetProperties.extension();
		} else {
			return null;
		}
	}

	public static String getPropertiesFileName(String assetFileName, Class<?> assetType) {
		AssetType assetTypeEnum = Assets.getAssetType(assetType);
		if (assetTypeEnum != null && assetTypeEnum.propsType != null) {
			return assetFileName + '.' + AssetType.assetProperties.extension();
		} else {
			return null;
		}
	}

	public static boolean hasPropertiesFile(String assetFileName, FileType fileType) {
		String propertiesFileName = getPropertiesFileName(assetFileName);
		return propertiesFileName == null ? false : fileExists(propertiesFileName, fileType);
	}

	public static boolean fileExists(String fileName, FileType fileType) {
		return Gdx.files.getFileHandle(fileName, fileType).exists();
	}

	public static FileHandle getPropertiesFile(String assetFileName, FileType fileType) {
		String propertiesFileName = getPropertiesFileName(assetFileName);
		if (propertiesFileName == null) {
			return null;
		}

		FileHandle propsHandle = Gdx.files.getFileHandle(propertiesFileName, fileType);
		return propsHandle.exists() ? propsHandle : null;
	}

	public static boolean hasPropertiesFile(Class<?> assetType, String assetFileName, FileType fileType) {
		String propertiesFileName = getPropertiesFileName(assetFileName, assetType);
		if (propertiesFileName == null) {
			return false;
		}

		return fileExists(propertiesFileName, fileType);
	}

	public static FileHandle getPropertiesFile(String assetFileName, FileType fileType, Class<?> assetType) {
		String propertiesFileName = getPropertiesFileName(assetFileName, assetType);
		if (propertiesFileName == null) {
			return null;
		}

		FileHandle propsHandle = Gdx.files.getFileHandle(propertiesFileName, fileType);
		return propsHandle.exists() ? propsHandle : null;
	}

	public static <T extends AssetProperties<?>> T loadAssetProperties(Object asset) {
		String assetFileName = AssetService.getFileName(asset);
		FileHandle propsHandle = getPropertiesFile(assetFileName, FileType.Internal, asset.getClass());
		if (propsHandle == null) {
			return null;
		} else {
			return AssetService.load(propsHandle.name());
		}
	}

	public static FileHandle getFileHandle(String path) {
		boolean hasFileTypeInfo = hasFileTypeInfo(path);
		char fileTypeInfo = hasFileTypeInfo ? path.charAt(0) : getDefaultFileTypeInfo(path);
		String pathExtract = hasFileTypeInfo ? path.substring(3) : path;
		Files files = Gdx.files;
		switch (fileTypeInfo) {
		case internalFileType:
			return files.internal(pathExtract);
		case classpathFileType:
			return files.classpath(pathExtract);
		case externalFileType:
			return files.external(pathExtract);
		case absoluteFileType:
			return files.absolute(pathExtract);
		case localFileType:
			return files.local(pathExtract);
		default:
			return files.internal(pathExtract);
		}
	}

	public static FileType getFileType(String path) {
		boolean hasFileTypeInfo = hasFileTypeInfo(path);
		char fileTypeInfo = hasFileTypeInfo ? path.charAt(0) : getDefaultFileTypeInfo(path);
		switch (fileTypeInfo) {
		case internalFileType:
			return FileType.Internal;
		case classpathFileType:
			return FileType.Classpath;
		case externalFileType:
			return FileType.External;
		case absoluteFileType:
			return FileType.Absolute;
		case localFileType:
			return FileType.Local;
		default:
			return FileType.Internal;
		}
	}

	private static char getDefaultFileTypeInfo(String path) {
		return isAbsolutePath(path) ? absoluteFileType : internalFileType;
	}

	public static boolean isAbsolutePath(String path) {
		int length = path.length();
		if (length < 1) {
			return false;
		}

		char firstChar = path.charAt(0);
		if (isPathDelimiter(firstChar) || firstChar == homePathAlias) {
			return true;
		} else if (length < 3 || path.charAt(1) != windowsDriveLetterDelimiter) {
			return false;
		} else {
			return isPathDelimiter(path.charAt(2));
		}
	}

	private static boolean isPathDelimiter(char c) {
		return c == unixFilePathDelimiter || c == windowsFilePathDelimiter;
	}

	private static boolean hasFileTypeInfo(String path) {
		if (path.length() < 3) {
			return false;
		}

		if (path.charAt(1) != fileTypeInfoDelimiter || path.charAt(2) != fileTypeInfoDelimiter) {
			return false;
		}

		char typeInfo = path.charAt(0);
		return typeInfo == classpathFileType || typeInfo == internalFileType || typeInfo == externalFileType
				|| typeInfo == absoluteFileType || typeInfo == localFileType;
	}

	public static FileHandle getRelativeFileHandle(FileHandle file, String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, filePathDelimiters);
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
