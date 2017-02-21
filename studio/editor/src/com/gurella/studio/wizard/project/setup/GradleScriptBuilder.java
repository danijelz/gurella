package com.gurella.studio.wizard.project.setup;

import static com.gurella.studio.editor.utils.Try.unchecked;
import static java.util.stream.Collectors.joining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.wizard.project.ProjectType;

public class GradleScriptBuilder {
	private final String appName;
	private final List<ProjectType> projects;
	private final List<Dependency> dependencies;

	private int indent = 0;
	private BufferedWriter writer;

	public GradleScriptBuilder(SetupInfo setupInfo) {
		appName = setupInfo.appName;
		projects = setupInfo.projects;
		dependencies = setupInfo.dependencies;
	}

	private static File createTempFile(String prefix, String suffix) {
		File file = unchecked(() -> File.createTempFile(prefix, suffix));
		if (!file.exists()) {
			unchecked(() -> Boolean.valueOf(file.createNewFile()));
			file.deleteOnExit();
		}
		file.setWritable(true);
		return file.getAbsoluteFile();
	}

	public File createSettingsScript() {
		File settingsFile = createTempFile("gurella-setup-settings", ".gradle");
		try (FileWriter settingsWriter = new FileWriter(settingsFile);
				BufferedWriter settingsBw = new BufferedWriter(settingsWriter)) {
			settingsBw.write(projects.stream().map(p -> p.getName()).collect(joining("', '", "include '", "'")));
			return settingsFile;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public File createBuildScript() {
		File buildFile = createTempFile("gurella-setup-build", ".gradle");
		try (FileWriter buildWriter = new FileWriter(buildFile);
				BufferedWriter writer = new BufferedWriter(buildWriter)) {
			this.writer = writer;
			addBuildScript();
			addAllProjects();
			projects.forEach(p -> addProject(p));
			addGradleNatureToEclipseProject();
			return buildFile;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			writer = null;
		}
	}

	private void addGradleNatureToEclipseProject() {
		if(Platform.getBundle(SetupConstants.eclipseGradleCorePlugin) == null) {
			return;
		}

		write("\n\neclipse {");
		write("\nproject {");
		write("natures 'org.eclipse.buildship.core.gradleprojectnature'");
		write("buildCommand \"org.eclipse.buildship.core.gradleprojectbuilder\"");
		write("}");
		write("}");
	}

	private void addBuildScript() {
		write("buildscript {");
		addBuildScriptRepos();
		addBuildScriptDependencies();
		write("}");
		space();
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

		space();
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
		space();
		write("version = '1.0'");
		space();
		write("ext {");
		write("appName = \"" + appName + "\"");
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
		Arrays.stream(projectType.getGradlePlugins()).forEachOrdered(p -> write("apply plugin: \"" + p + "\""));
		space();

		if (projectType.needsNatives()) {
			write("configurations { natives }");
			space();
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
		if (projectType.needsNatives() && dependency.contains("native")) {
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

		Try.unchecked(() -> writer.write(txt));
	}

	private void space() {
		Try.unchecked(() -> writer.write("\n"));
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
