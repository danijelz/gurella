package com.gurella.studio.wizard.project.setup;

import static java.util.stream.Collectors.joining;

import java.util.List;

import com.gurella.studio.wizard.project.ProjectType;

public class GradleSettingsFile extends GeneratedProjectFile {
	private final List<ProjectType> projects;

	public GradleSettingsFile(String outputName, List<ProjectType> projects) {
		super(outputName);
		this.projects = projects;
	}

	@Override
	protected String generate() {
		return projects.stream().map(p -> p.getName()).collect(joining("', '", "include '", "'"));
	}
}
