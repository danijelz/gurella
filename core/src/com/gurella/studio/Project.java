package com.gurella.studio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.application2.Application;

public class Project implements Serializable {
	private static final String APPLICATION_FILE_NAME_TAG = "applicationFileName";

	FileHandle projectFileHandle;
	FileHandle applicationFileHandle;
	Application application;

	public Project() {
	}

	@Override
	public void write(Json json) {
		json.writeValue(APPLICATION_FILE_NAME_TAG, applicationFileHandle.path());
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String applicationFileName = jsonData.getString(APPLICATION_FILE_NAME_TAG);
		applicationFileHandle = Gdx.files.absolute(applicationFileName);
		application = new Json().fromJson(Application.class, applicationFileHandle);
	}

	public void save() {
		projectFileHandle.writeString(new Json().prettyPrint(this), false);
		applicationFileHandle.writeString(new Json().prettyPrint(application), false);
	}

	public Application getApplication() {
		return application;
	}

	public FileHandle getApplicationFileHandle() {
		return applicationFileHandle;
	}
}
