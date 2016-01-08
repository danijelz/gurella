package com.gurella.engine.base.serialization;

public class ObjectReference implements Reference {
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

	@Override
	public String getFileName() {
		return fileName;
	}
}
