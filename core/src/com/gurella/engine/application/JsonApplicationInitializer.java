package com.gurella.engine.application;

//TODO unused
public class JsonApplicationInitializer implements ApplicationInitializer {
	private final String projectFileName;

	public JsonApplicationInitializer(String projectFileName) {
		this.projectFileName = projectFileName;
	}

	@Override
	public void init(Application application) {
		//application.read(new Json(), new JsonReader().parse(Gdx.files.internal(projectFileName).readString()));
	}
}