package com.gurella.studio.wizard.project.setup;

public class SetupConstants {
	// Versions
	public static final String libgdxVersion = "1.9.5";
	// Temporary snapshot version, we need a more dynamic solution for pointing to the latest nightly
	public static final String libgdxNightlyVersion = "1.9.6-SNAPSHOT";
	public static final String roboVmPluginVersion = "2.3.0";
	public static final String moePluginVersion = "1.2.3";
	public static final int androidApiLevel = 20;
	public static final int[] androidBuildToolsVersion = { 23, 0, 1 };
	public static final String androidPluginVersion = "1.5.0";
	public static final String gwtVersion = "2.8.0";
	public static final String gwtPluginVersion = "0.6";
	public static final String gurellaVersion = "0.1-SNAPSHOT";
	public static final String box2DLightsVersion = "1.4";
	public static final String aiVersion = "1.8.0";

	// Repositories
	public static final String mavenLocal = "mavenLocal()";
	public static final String mavenCentral = "mavenCentral()";
	public static final String jCenter = "jcenter()";
	public static final String libGDXSnapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/";
	public static final String libGDXReleaseUrl = "https://oss.sonatype.org/content/repositories/releases/";

	// Project plugins
	public static final String gwtPluginImport = "de.richsource.gradle.plugins:gwt-gradle-plugin:0" + gwtPluginVersion;
	public static final String androidPluginImport = "com.android.tools.build:gradle:" + androidPluginVersion;
	public static final String roboVmPluginImport = "com.mobidevelop.robovm:robovm-gradle-plugin:"
			+ roboVmPluginVersion;
	public static final String moePluginImport = "org.multi-os-engine:moe-gradle:" + moePluginVersion;

	private SetupConstants() {
	}
}