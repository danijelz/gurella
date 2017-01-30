package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.Values;

public final class AssetId implements Poolable {
	public String fileName;
	public FileType fileType;
	public Class<?> assetType;

	// TODO remove
	public FileHandle cachedFile;

	public AssetId set(AssetId other) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		this.cachedFile = other.cachedFile;
		return this;
	}

	public AssetId set(String fileName, FileType fileType, Class<?> assetType) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = assetType;
		return this;
	}

	public AssetId set(String fileName) {
		this.fileName = fileName;
		this.fileType = FileType.Internal;
		this.assetType = Assets.getAssetClass(fileName);
		return this;
	}

	public AssetId set(String fileName, FileType fileType) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = Assets.getAssetClass(fileName);
		return this;
	}

	public AssetId set(String fileName, Class<?> assetType) {
		this.fileName = fileName;
		this.fileType = FileType.Internal;
		this.assetType = assetType;
		return this;
	}

	public AssetId set(FileHandle file, Class<?> assetType) {
		this.fileName = file.path();
		this.fileType = file.type();
		this.assetType = assetType;
		return this;
	}

	public AssetId set(FileHandle file) {
		this.fileName = file.path();
		this.fileType = file.type();
		this.assetType = Assets.getAssetClass(fileName);
		this.cachedFile = file;
		return this;
	}

	public boolean isEmpty() {
		return fileName != null && fileType != null && assetType != null;
	}

	public AssetId empty() {
		reset();
		return this;
	}

	public boolean equalsFile(FileHandle file) {
		return fileName.equals(file.path()) && fileType == file.type();
	}

	public boolean equalsFile(AssetId other) {
		return fileName.equals(other.fileName) && fileType == other.fileType;
	}

	public boolean equalsFile(String fileName, FileType fileType) {
		return this.fileName.equals(fileName) && this.fileType == fileType;
	}

	@Override
	public void reset() {
		fileName = null;
		fileType = null;
		assetType = null;
		cachedFile = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fileName.hashCode();
		result = prime * result + fileType.hashCode();
		result = prime * result + assetType.hashCode();
		return result;
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
		return equals(other.fileName, other.fileType, other.assetType);
	}

	public boolean equals(String fileName, FileType fileType, Class<?> assetType) {
		return this.fileType == fileType && this.assetType == assetType
				&& Values.isEqual(this.fileName, fileName, false);
	}
}
