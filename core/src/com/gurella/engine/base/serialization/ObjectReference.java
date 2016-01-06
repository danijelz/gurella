package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.ReflectionUtils;

public class ObjectReference implements Serializable, Reference {
	private int id;
	private String file;
	private String typeName;

	private Class<?> type;

	ObjectReference() {
	}

	public ObjectReference(int id, String file, String typeName) {
		this.id = id;
		this.file = file;
		this.typeName = typeName;
	}
	
	public ObjectReference(int id, String file, Class<?> type) {
		this.id = id;
		this.file = file;
		this.typeName = type.getName();
		this.type = type;
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

	public Class<?> getType() {
		if (type == null) {
			type = ReflectionUtils.forName(typeName);
		}
		return type;
	}

	@Override
	public void write(Json json) {
		json.writeField(FILE_NAME_TAG, assetTypeName + " " + fileName);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String value = jsonData.getString(FILE_NAME_TAG);
		int index = value.indexOf(' ');
		assetTypeName = value.substring(0, index);
		fileName = value.substring(index + 1);
	}
}
