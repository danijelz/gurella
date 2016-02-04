package com.gurella.engine.base.serialization.json;

public class ObjectReference {
	private int id;
	private String fileName;

	ObjectReference() {
	}

	public ObjectReference(int id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}

	public int getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}
}
