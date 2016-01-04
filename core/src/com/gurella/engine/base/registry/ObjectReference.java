package com.gurella.engine.base.registry;

public class ObjectReference {
	private int id;
	private String file;

	ObjectReference() {
	}

	public ObjectReference(int id, String file) {
		this.id = id;
		this.file = file;
	}

	public int getId() {
		return id;
	}

	public String getFile() {
		return file;
	}
}
