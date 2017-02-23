package com.gurella.studio.wizard.project.build;

public class ApplicationConfigFile extends GeneratedFile {
	private final String initialScene;

	public ApplicationConfigFile(String initialScene) {
		super("core/assets/application.gcfg");
		this.initialScene = initialScene;
	}

	@Override
	protected String generate() {
		//@formatter:off
		return "{\n" + 
	           "0: { initialScenePath: scenes/" + initialScene + " }\n" + 
	           "}";
		//@formatter:on
	}
}
