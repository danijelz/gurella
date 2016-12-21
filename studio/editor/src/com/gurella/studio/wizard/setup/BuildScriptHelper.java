package com.gurella.studio.wizard.setup;

import static java.util.stream.Collectors.joining;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;

public class BuildScriptHelper {
	private static int indent = 0;

	public static void addBuildScript(List<ProjectType> projects, BufferedWriter wr) throws IOException {
		write(wr, "buildscript {");
		// repos
		write(wr, "repositories {");
		write(wr, DependencyBank.mavenLocal);
		write(wr, DependencyBank.mavenCentral);
		write(wr, "maven { url \"" + DependencyBank.libGDXSnapshotsUrl + "\" }");
		write(wr, DependencyBank.jCenter);
		write(wr, "}");
		space(wr);
		// dependencies
		write(wr, "dependencies {");
		if (projects.contains(ProjectType.HTML)) {
			write(wr, "classpath '" + DependencyBank.gwtPluginImport + "'");
		}
		if (projects.contains(ProjectType.ANDROID)) {
			write(wr, "classpath '" + DependencyBank.androidPluginImport + "'");
		}
		if (projects.contains(ProjectType.IOS)) {
			write(wr, "classpath '" + DependencyBank.roboVMPluginImport + "'");
		}
		if (projects.contains(ProjectType.IOSMOE)) {
			write(wr, "classpath '" + DependencyBank.moePluginImport + "'");
		}
		write(wr, "}");
		write(wr, "}");
		space(wr);
	}

	public static void addAllProjects(BufferedWriter wr) throws IOException {
		write(wr, "allprojects {");
		write(wr, "apply plugin: \"eclipse\"");
		write(wr, "apply plugin: \"idea\"");
		space(wr);
		write(wr, "version = '1.0'");
		space(wr);
		write(wr, "ext {");
		write(wr, "appName = \"%APP_NAME%\"");
		write(wr, "gdxVersion = '" + DependencyBank.libgdxVersion + "'");
		write(wr, "roboVMVersion = '" + DependencyBank.roboVMVersion + "'");
		write(wr, "box2DLightsVersion = '" + DependencyBank.box2DLightsVersion + "'");
		write(wr, "ashleyVersion = '" + DependencyBank.ashleyVersion + "'");
		write(wr, "aiVersion = '" + DependencyBank.aiVersion + "'");
		write(wr, "gurellaVersion = '" + DependencyBank.gurellaVersion + "'");
		write(wr, "}");
		space(wr);
		write(wr, "repositories {");
		write(wr, DependencyBank.mavenLocal);
		write(wr, DependencyBank.mavenCentral);
		write(wr, "maven { url \"" + DependencyBank.libGDXSnapshotsUrl + "\" }");
		write(wr, "maven { url \"" + DependencyBank.libGDXReleaseUrl + "\" }");
		write(wr, "}");
		write(wr, "}");
	}

	public static void addProject(ProjectType project, List<Dependency> dependencies, BufferedWriter wr)
			throws IOException {
		space(wr);
		write(wr, "project(\":" + project.getName() + "\") {");
		Arrays.stream(project.getPlugins())
				.forEachOrdered(p -> Try.successful(p).peek(tp -> write(wr, "apply plugin: \"" + tp + "\"")));
		space(wr);
		
		if (project.equals(ProjectType.ANDROID) || project.equals(ProjectType.IOSMOE)) {
			write(wr, "configurations { natives }");
			space(wr);
		}
		
		addDependencies(project, dependencies, wr);
		write(wr, "}");
	}

	private static void addDependencies(ProjectType project, List<Dependency> dependencyList, BufferedWriter wr)
			throws IOException {
		write(wr, "dependencies {");
		if (!project.equals(ProjectType.CORE)) {
			write(wr, "compile project(\":" + ProjectType.CORE.getName() + "\")");
		}

		for (Dependency dep : dependencyList) {
			if (dep.getDependencies(project) == null) {
				continue;
			}

			for (String moduleDependency : dep.getDependencies(project)) {
				if (moduleDependency == null) {
					continue;
				}
				addDependency(wr, project, moduleDependency);
			}
		}
		write(wr, "}");
	}

	private static void addDependency(BufferedWriter wr, ProjectType project, String moduleDependency)
			throws IOException {
		if (project.equals(ProjectType.ANDROID)
				|| project.equals(ProjectType.IOSMOE) && moduleDependency.contains("native")) {
			write(wr, "natives \"" + moduleDependency + "\"");
		} else {
			write(wr, "compile \"" + moduleDependency + "\"");
		}
	}

	private static void write(BufferedWriter wr, String input) throws IOException {
		int delta = countMatches(input, '{') - countMatches(input, '}');
		indent += delta *= 4;
		indent = clamp(indent);
		if (delta > 0) {
			wr.write(repeat(" ", clamp(indent - 4)) + input + "\n");
		} else if (delta < 0) {
			wr.write(repeat(" ", clamp(indent)) + input + "\n");
		} else {
			wr.write(repeat(" ", indent) + input + "\n");
		}
	}

	private static void space(BufferedWriter wr) throws IOException {
		wr.write("\n");
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