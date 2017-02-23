package com.gurella.studio.wizard.project.build;

public abstract class ProjectFile {
	public String outputName;

	public ProjectFile(String outputName) {
		this.outputName = outputName;
	}

	abstract byte[] getContent();
}