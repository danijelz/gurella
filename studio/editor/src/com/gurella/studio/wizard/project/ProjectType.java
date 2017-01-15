package com.gurella.studio.wizard.project;

public enum ProjectType {
	CORE("core", "java"),
	DESKTOP("desktop", "java"),
	ANDROID("android", "android"),
	IOS("ios", "java", "robovm"),
	IOSMOE("ios-moe", "moe"),
	HTML("html", "gwt", "war");

	public final String name;
	private final String[] gradlePlugins;

	ProjectType(String name, String... gradlePlugins) {
		this.name = name;
		this.gradlePlugins = gradlePlugins;
	}

	public String getName() {
		return name;
	}

	public String[] getGradlePlugins() {
		return gradlePlugins;
	}

	public boolean needsNatives() {
		return this == ProjectType.ANDROID || this == ProjectType.IOSMOE;
	}
}