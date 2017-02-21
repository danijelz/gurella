package com.gurella.studio.wizard.project.setup;

public abstract class GeneratedProjectFile extends ProjectFile {
	public GeneratedProjectFile(String name) {
		super(name, name, false);
	}

	protected abstract String generate();
}
