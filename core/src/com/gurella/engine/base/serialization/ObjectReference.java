package com.gurella.engine.base.serialization;

import com.gurella.engine.utils.ReflectionUtils;

public class ObjectReference implements Reference {
	private int id;
	private String filePath;
	private String typeName;

	private Class<?> type;

	ObjectReference() {
	}

	public ObjectReference(int id, String file, String typeName) {
		this.id = id;
		this.filePath = file;
		this.typeName = typeName;
	}

	public ObjectReference(int id, String file, Class<?> type) {
		this.id = id;
		this.filePath = file;
		this.typeName = type.getName();
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getTypeName() {
		return typeName;
	}

	public Class<?> getType() {
		if (type == null) {
			type = ReflectionUtils.forName(typeName);
		}
		return type;
	}
}
