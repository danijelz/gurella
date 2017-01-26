package com.gurella.engine.asset2;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.utils.Pool.Poolable;

public final class AssetId implements Poolable {
	public String fileName;
	public FileType fileType;
	public Class<?> assetType;

	public AssetId set(AssetId other) {
		this.fileName = other.fileName;
		this.fileType = other.fileType;
		this.assetType = other.assetType;
		return this;
	}

	public AssetId set(String fileName, FileType fileType, Class<?> assetType) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.assetType = assetType;
		return this;
	}

	@Override
	public void reset() {
		fileName = null;
		fileType = null;
		assetType = null;
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
		return fileType == other.fileType && assetType == other.assetType && fileName.equals(other.fileName);
	}
}
