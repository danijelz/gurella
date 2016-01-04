package com.gurella.engine.base.registry;

public class ObjectReference {
	private int id;
	private String file;
	private String typeName;

	ObjectReference() {
	}

	public ObjectReference(int id, String file, String typeName) {
		this.id = id;
		this.file = file;
		this.typeName = typeName;
	}

	public int getId() {
		return id;
	}

	public String getFile() {
		return file;
	}

	public String getTypeName() {
		return typeName;
	}
}
