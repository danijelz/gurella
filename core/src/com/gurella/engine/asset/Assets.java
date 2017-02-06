package com.gurella.engine.asset;

import java.util.StringTokenizer;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.asset.descriptor.DefaultAssetDescriptors;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.AssetProperties;

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

	private Assets() {
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

	public static String toPropertiesFileName(String assetFileName) {
		return assetFileName + '.' + DefaultAssetDescriptors.assetProps.getSingleExtension();
	}

	public static boolean hasProperties(String assetFileName, Class<?> assetType) {
		AssetLoader<?, ?, ?> loader = AssetDescriptors.getLoader(assetFileName, assetType);
		return loader != null && loader.getAssetPropertiesType() != null;
	}

	public static boolean fileExists(String fileName, FileType fileType) {
		return Gdx.files.getFileHandle(fileName, fileType).exists();
	}

	public static FileHandle getPropertiesFile(String assetFileName, FileType fileType, Class<?> assetType) {
		if (!hasProperties(assetFileName, assetType)) {
			return null;
		}

		String propertiesFileName = toPropertiesFileName(assetFileName);
		FileHandle propsHandle = Gdx.files.getFileHandle(propertiesFileName, fileType);
		return propsHandle.exists() ? propsHandle : null;
	}

	public static <T extends AssetProperties> T loadAssetProperties(Object asset) {
		String assetFileName = AssetService.getFileName(asset);
		FileHandle propsHandle = getPropertiesFile(assetFileName, FileType.Internal, asset.getClass());
		return propsHandle == null ? null : AssetService.<T> load(propsHandle.name());
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
