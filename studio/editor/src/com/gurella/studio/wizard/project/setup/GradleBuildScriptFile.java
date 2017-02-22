package com.gurella.studio.wizard.project.setup;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;

import com.gurella.engine.utils.Values;
import com.gurella.studio.wizard.project.ProjectType;

public class GradleBuildScriptFile extends GeneratedFile {
	private static final String nativeDependencyTxt = "native";
	private final String appName;
	private final List<ProjectType> projects;
	private final List<Dependency> dependencies;

	private int indent = 0;
	private final StringBuilder writer = new StringBuilder(4096);

	public GradleBuildScriptFile(SetupInfo setupInfo) {
		super("build.gradle");
		appName = setupInfo.appName;
		projects = setupInfo.projects;
		dependencies = setupInfo.dependencies;
	}

	@Override
	protected String generate() {
		addBuildScript();
		addAllProjects();
		projects.forEach(p -> addProject(p));
		addGradleNatureToEclipseProject();
		return writer.toString();
	}

	private void addBuildScript() {
		write("buildscript {");
		addBuildScriptRepos();
		addBuildScriptDependencies();
		write("}");
		newLine();
	}

	private void addBuildScriptRepos() {
		write("repositories {");
		write(SetupConstants.mavenLocal);
		write(SetupConstants.mavenCentral);
		write("maven { url \"" + SetupConstants.libGDXSnapshotsUrl + "\" }");
		write(SetupConstants.jCenter);
		write("}");
	}

	private void addBuildScriptDependencies() {
		if (!projects.contains(ProjectType.HTML) && !projects.contains(ProjectType.ANDROID)
				&& !projects.contains(ProjectType.IOS) && !projects.contains(ProjectType.IOSMOE)) {
			return;
		}

		newLine();
		write("dependencies {");

		if (projects.contains(ProjectType.HTML)) {
			write("classpath '" + SetupConstants.gwtPluginImport + "'");
		}
		if (projects.contains(ProjectType.ANDROID)) {
			write("classpath '" + SetupConstants.androidPluginImport + "'");
		}
		if (projects.contains(ProjectType.IOS)) {
			write("classpath '" + SetupConstants.roboVmPluginImport + "'");
		}
		if (projects.contains(ProjectType.IOSMOE)) {
			write("classpath '" + SetupConstants.moePluginImport + "'");
		}

		write("}");
	}

	private void addAllProjects() {
		write("allprojects {");
		write("apply plugin: \"eclipse\"");
		write("apply plugin: \"idea\"");
		newLine();
		write("version = '1.0'");
		newLine();
		write("ext {");
		write("appName = \"" + appName + "\"");
		write("gdxVersion = '" + SetupConstants.libgdxVersion + "'");
		write("gurellaVersion = '" + SetupConstants.gurellaVersion + "'");
		write("roboVMVersion = '" + SetupConstants.roboVmPluginVersion + "'");
		write("box2DLightsVersion = '" + SetupConstants.box2DLightsVersion + "'");
		write("aiVersion = '" + SetupConstants.aiVersion + "'");
		write("}");
		newLine();
		write("repositories {");
		write(SetupConstants.mavenLocal);
		write(SetupConstants.mavenCentral);
		write("maven { url \"" + SetupConstants.libGDXSnapshotsUrl + "\" }");
		write("maven { url \"" + SetupConstants.libGDXReleaseUrl + "\" }");
		write("}");
		write("}");
	}

	private void addProject(ProjectType projectType) {
		newLine();
		write("project(\":" + projectType.getName() + "\") {");
		Arrays.stream(projectType.getGradlePlugins()).forEachOrdered(p -> write("apply plugin: \"" + p + "\""));
		newLine();

		if (projectType.hasNativeDependencies()) {
			write("configurations { natives }");
			newLine();
		}

		addProjectDependencies(projectType);
		write("}");
	}

	private void addProjectDependencies(ProjectType projectType) {
		write("dependencies {");
		if (projectType != ProjectType.CORE) {
			write("compile project(\":" + ProjectType.CORE.getName() + "\")");
		}

		dependencies.stream().filter(Values::isNotEmpty).flatMap(d -> getDependencies(projectType, d))
				.forEachOrdered(d -> addDependency(projectType, d));

		write("}");
	}

	private static Stream<String> getDependencies(ProjectType projectType, Dependency dependency) {
		return Stream.of(dependency.getDependencies(projectType)).filter(Values::isNotBlank);
	}

	private void addDependency(ProjectType projectType, String dependency) {
		boolean nativeDep = projectType.hasNativeDependencies() && dependency.contains(nativeDependencyTxt);
		write((nativeDep ? "natives \"" : "compile \"") + dependency + "\"");
	}

	private void addGradleNatureToEclipseProject() {
		if (Platform.getBundle(SetupConstants.eclipseGradleCorePlugin) == null) {
			return;
		}

		newLine();
		write("eclipse {");
		write("project {");
		write("natures 'org.eclipse.buildship.core.gradleprojectnature'");
		write("buildCommand \"org.eclipse.buildship.core.gradleprojectbuilder\"");
		write("}");
		write("}");
	}

	private void write(String txt) {
		int delta = countMatches(txt, '{') - countMatches(txt, '}');
		indent += delta * 4;
		indent = clamp(indent);
		int count = delta > 0 ? clamp(indent - 4) : delta < 0 ? clamp(indent) : indent;
		writer.append(repeat(" ", count) + txt + "\n");
	}

	private void newLine() {
		writer.append("\n");
	}

	private static int clamp(int indent) {
		return indent < 0 ? 0 : indent;
	}

	private static int countMatches(String input, char match) {
		return (int) input.chars().filter(c -> c == match).count();
	}

	private static String repeat(String toRepeat, int count) {
		return IntStream.range(0, count).mapToObj(i -> toRepeat).collect(joining());
	}
}
