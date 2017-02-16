package com.gurella.engine.asset;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.utils.Values;

//TODO try to generalize for remote assets -> rename fileName to uri, use ResourceType instead of FileType...
public final class AssetId implements Poolable {
	String fileName;//TODO rename fileName to uri
	FileType fileType;
	Class<?> assetType;
	String bundleId;

	// TODO try to make usefull or remove
	transient FileHandle file;

	public String getFileName() {
		return fileName;
	}

	public FileType getFileType() {
		return fileType;
	}

	public Class<?> getAssetType() {
		return assetType;
	}

	public String getBundleId() {
		return bundleId;
	}

	public AssetId set(AssetId other) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		this.file = other.file;
		this.bundleId = other.bundleId;
		return this;
	}

	public AssetId set(AssetId other, String bundleId) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		this.file = other.file;
		this.bundleId = bundleId;
		return this;
	}

	public AssetId set(String fileName, FileType fileType, Class<?> assetType) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = assetType;
		this.bundleId = null;
		this.file = null;
		return this;
	}

	public AssetId set(String fileName, FileType fileType, Class<?> assetType, String bundleId) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = assetType;
		this.bundleId = bundleId;
		this.file = null;
		return this;
	}

	public AssetId set(String fileName) {
		this.fileName = fileName;
		this.fileType = FileType.Internal;
		this.assetType = AssetDescriptors.getAssetType(fileName);
		this.bundleId = null;
		this.file = null;
		return this;
	}

	public AssetId set(String fileName, FileType fileType) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = AssetDescriptors.getAssetType(fileName);
		this.bundleId = null;
		this.file = null;
		return this;
	}

	public AssetId set(String fileName, Class<?> assetType) {
		this.fileName = fileName;
		this.fileType = FileType.Internal;
		this.assetType = assetType;
		this.bundleId = null;
		this.file = null;
		return this;
	}

	public AssetId set(FileHandle file, Class<?> assetType) {
		this.fileName = file.path();
		this.fileType = file.type();
		this.assetType = assetType;
		this.bundleId = null;
		this.file = file;
		return this;
	}

	public AssetId set(FileHandle file) {
		this.fileName = file.path();
		this.fileType = file.type();
		this.assetType = AssetDescriptors.getAssetType(fileName);
		this.bundleId = null;
		this.file = file;
		return this;
	}

	public void set(AssetId other, FileHandle file) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		this.file = file;
		this.bundleId = other.bundleId;
	}
	
	public void set(AssetId other, String bundleId, FileHandle file) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		this.file = file;
		this.bundleId = bundleId;
	}

	public boolean isEmpty() {
		return fileName == null || assetType == null;
	}

	public AssetId empty() {
		reset();
		return this;
	}

	public boolean equalsFile(FileHandle file) {
		if (file == null) {
			return isEmpty();
		} else {
			return fileType == file.type() && Values.nullSafeEquals(fileName, file.path());
		}
	}

	public boolean equalsFile(AssetId other) {
		return other != null && fileType == other.fileType && Values.nullSafeEquals(fileName, other.fileName);
	}

	public boolean equalsFile(String fileName, FileType fileType) {
		return this.fileType == fileType && Values.nullSafeEquals(this.fileName, fileName);
	}

	@Override
	public void reset() {
		fileName = null;
		fileType = null;
		assetType = null;
		bundleId = null;
		file = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fileName == null ? 0 : fileName.hashCode());
		result = prime * result + (fileType == null ? 0 : fileType.hashCode());
		result = prime * result + (assetType == null ? 0 : assetType.hashCode());
		return prime * result + (bundleId == null ? 0 : bundleId.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (AssetId.class != obj.getClass()) {
			return false;
		}

		AssetId other = (AssetId) obj;
		return equals(other.fileName, other.fileType, other.assetType, other.bundleId);
	}

	public boolean equals(String fileName, FileType fileType, Class<?> assetType, String bundleId) {
		return this.fileType == fileType && this.assetType == assetType
				&& Values.nullSafeEquals(this.fileName, fileName) && Values.nullSafeEquals(this.bundleId, bundleId);
	}

	public boolean equals(String fileName, FileType fileType, Class<?> assetType) {
		return this.fileType == fileType && this.assetType == assetType
				&& Values.isEqual(this.fileName, fileName, false);
	}

	public boolean equals(FileHandle file, Class<?> assetType) {
		return this.fileType == file.type() && this.assetType == assetType
				&& Values.isEqual(this.fileName, file.path(), false);
	}

	@Override
	public String toString() {
		return "AssetId [fileName=" + fileName + ", fileType=" + fileType + ", assetType=" + assetType + ", bundleId="
				+ bundleId + "]";
	}
}
