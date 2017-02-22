package com.gurella.studio.wizard.project.setup;

import static java.util.stream.Collectors.joining;

import java.util.List;

import com.gurella.studio.wizard.project.ProjectType;

public class GradleSettingsFile extends GeneratedFile {
	private final List<ProjectType> projects;

	public GradleSettingsFile(List<ProjectType> projects) {
		super("settings.gradle");
		this.projects = projects;
	}

	@Override
	protected String generate() {
		return projects.stream().map(p -> p.getName()).collect(joining("', '", "include '", "'"));
	}
}
