package com.gurella.studio.wizard.project.setup;

public class ApplicationConfigFile extends GeneratedProjectFile {
	private final String initialScene;

	public ApplicationConfigFile(String outputName, String initialScene) {
		super(outputName);
		this.initialScene = initialScene;
	}

	@Override
	protected String generate() {
		return "{\r\n" + "0: { initialScenePath: scenes/" + initialScene + " }\r\n" + "}";
	}
}
