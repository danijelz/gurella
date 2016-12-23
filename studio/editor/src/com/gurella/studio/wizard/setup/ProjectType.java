package com.gurella.studio.wizard.setup;

public enum ProjectType {
	CORE("core", new String[] { "java" }),
	DESKTOP("desktop", new String[] { "java" }),
	ANDROID("android", new String[] { "android" }),
	IOS("ios", new String[] { "java", "robovm" }),
	IOSMOE("ios-moe", new String[] { "moe" }),
	HTML("html", new String[] { "gwt", "war" });

	final String name;
	private final String[] plugins;

	ProjectType(String name, String plugins[]) {
		this.name = name;
		this.plugins = plugins;
	}

	public String getName() {
		return name;
	}

	public String[] getPlugins() {
		return plugins;
	}
	
	public boolean needsNativLibs() {
		return this == ProjectType.ANDROID || this == ProjectType.IOSMOE;
	}
}