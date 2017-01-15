package com.gurella.studio.wizard.project.setup;

//TODO unused
public abstract class GeneratedProjectFile extends ProjectFile {
	public GeneratedProjectFile(String outputName) {
		super(outputName, outputName, false);
	}

	protected abstract String generate();
}
