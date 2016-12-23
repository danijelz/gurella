package com.gurella.studio.wizard.setup;

import static java.util.stream.Collectors.joining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.Try;

public class ScriptBuilder {
	List<ProjectType> projectTypes = new ArrayList<ProjectType>();
	List<Dependency> dependencies = new ArrayList<Dependency>();
	List<String> incompatibilities = new ArrayList<String>();

	File settingsFile;
	File buildFile;

	private int indent = 0;
	private BufferedWriter writer;

	public ScriptBuilder(List<ProjectType> projects, List<Dependency> dependencies) {
		this.projectTypes = projects;
		this.dependencies = dependencies;
		dependencies.stream().forEach(d -> projects.forEach(p -> incompatibilities.addAll(d.getIncompatibilities(p))));

		settingsFile = createTempFile("libgdx-setup-settings", ".gradle");
		buildFile = createTempFile("libgdx-setup-build", ".gradle");

		writeSettings(projects);

		try (FileWriter buildWriter = new FileWriter(buildFile.getAbsoluteFile());
				BufferedWriter writer = new BufferedWriter(buildWriter)) {
			this.writer = writer;
			addBuildScript();
			addAllProjects();
			projects.forEach(p -> addProject(p));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static File createTempFile(String prefix, String suffix) {
		try {
			File file = File.createTempFile(prefix, suffix);
			if (!file.exists()) {
				file.createNewFile();
				file.deleteOnExit();
			}
			file.setWritable(true);
			return file;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private void writeSettings(List<ProjectType> projects) {
		try (FileWriter settingsWriter = new FileWriter(settingsFile.getAbsoluteFile());
				BufferedWriter settingsBw = new BufferedWriter(settingsWriter)) {
			settingsBw.write(projects.stream().map(p -> p.getName()).collect(joining("', '", "include '", "'")));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private void addBuildScript() {
		write("buildscript {");
		// repos
		write("repositories {");
		write(SetupConstants.mavenLocal);
		write(SetupConstants.mavenCentral);
		write("maven { url \"" + SetupConstants.libGDXSnapshotsUrl + "\" }");
		write(SetupConstants.jCenter);
		write("}");
		addBuildScriptDependencies();
		write("}");
		space();
	}

	private void addBuildScriptDependencies() {
		if (!projectTypes.contains(ProjectType.HTML) && !projectTypes.contains(ProjectType.ANDROID)
				&& !projectTypes.contains(ProjectType.IOS) && !projectTypes.contains(ProjectType.IOSMOE)) {
			return;
		}

		space();
		write("dependencies {");

		if (projectTypes.contains(ProjectType.HTML)) {
			write("classpath '" + SetupConstants.gwtPluginImport + "'");
		}
		if (projectTypes.contains(ProjectType.ANDROID)) {
			write("classpath '" + SetupConstants.androidPluginImport + "'");
		}
		if (projectTypes.contains(ProjectType.IOS)) {
			write("classpath '" + SetupConstants.roboVmPluginImport + "'");
		}
		if (projectTypes.contains(ProjectType.IOSMOE)) {
			write("classpath '" + SetupConstants.moePluginImport + "'");
		}

		write("}");
	}

	private void addAllProjects() {
		write("allprojects {");
		write("apply plugin: \"eclipse\"");
		write("apply plugin: \"idea\"");
		space();
		write("version = '1.0'");
		space();
		write("ext {");
		write("appName = \"%APP_NAME%\"");
		write("gdxVersion = '" + SetupConstants.libgdxVersion + "'");
		write("gurellaVersion = '" + SetupConstants.gurellaVersion + "'");
		write("roboVMVersion = '" + SetupConstants.roboVmPluginVersion + "'");
		write("box2DLightsVersion = '" + SetupConstants.box2DLightsVersion + "'");
		write("aiVersion = '" + SetupConstants.aiVersion + "'");
		write("}");
		space();
		write("repositories {");
		write(SetupConstants.mavenLocal);
		write(SetupConstants.mavenCentral);
		write("maven { url \"" + SetupConstants.libGDXSnapshotsUrl + "\" }");
		write("maven { url \"" + SetupConstants.libGDXReleaseUrl + "\" }");
		write("}");
		write("}");
	}

	private void addProject(ProjectType projectType) {
		space();
		write("project(\":" + projectType.getName() + "\") {");
		Arrays.stream(projectType.getPlugins()).forEachOrdered(p -> write("apply plugin: \"" + p + "\""));
		space();

		if (projectType.needsNativLibs()) {
			write("configurations { natives }");
			space();
		}

		addDependencies(projectType);
		write("}");
	}

	private void addDependencies(ProjectType projectType) {
		write("dependencies {");
		if (projectType != ProjectType.CORE) {
			write("compile project(\":" + ProjectType.CORE.getName() + "\")");
		}

		dependencies.stream().filter(Values::isNotEmpty).flatMap(d -> Stream.of(d.getDependencies(projectType)))
				.filter(Values::isNotBlank).forEachOrdered(d -> addDependency(projectType, d));

		write("}");
	}

	private void addDependency(ProjectType projectType, String dependency) {
		if (projectType.needsNativLibs() && dependency.contains("native")) {
			write("natives \"" + dependency + "\"");
		} else {
			write("compile \"" + dependency + "\"");
		}
	}

	private void write(String input) {
		int delta = countMatches(input, '{') - countMatches(input, '}');
		indent += delta *= 4;
		indent = clamp(indent);

		String txt;
		if (delta > 0) {
			txt = repeat(" ", clamp(indent - 4)) + input + "\n";
		} else if (delta < 0) {
			txt = repeat(" ", clamp(indent)) + input + "\n";
		} else {
			txt = repeat(" ", indent) + input + "\n";
		}

		Try.successful(writer).peek(w -> w.write(txt)).getUnchecked();
	}

	private void space() {
		Try.successful(writer).peek(w -> w.write("\n")).getUnchecked();
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
