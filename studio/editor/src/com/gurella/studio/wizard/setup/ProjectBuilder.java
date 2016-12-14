package com.gurella.studio.wizard.setup;

import static com.gurella.studio.wizard.setup.BuildScriptHelper.addProject;
import static java.util.stream.Collectors.joining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;

public class ProjectBuilder {
	DependencyBank bank;
	List<ProjectType> projects = new ArrayList<ProjectType>();
	List<Dependency> dependencies = new ArrayList<Dependency>();
	List<String> incompatibilities = new ArrayList<String>();

	File settingsFile;
	File buildFile;

	public ProjectBuilder(DependencyBank bank, List<ProjectType> projects, List<Dependency> dependencies) {
		this.bank = bank;
		this.projects = projects;
		this.dependencies = dependencies;

		dependencies.stream()
				.forEach(d -> projects.stream().forEach(p -> incompatibilities.addAll(d.getIncompatibilities(p))));
	}

	public boolean build() throws IOException {
		settingsFile = File.createTempFile("libgdx-setup-settings", ".gradle");
		if (!settingsFile.exists()) {
			settingsFile.createNewFile();
		}
		settingsFile.setWritable(true);

		buildFile = File.createTempFile("libgdx-setup-build", ".gradle");
		if (!buildFile.exists()) {
			buildFile.createNewFile();
		}
		buildFile.setWritable(true);

		try {
			FileWriter settingsWriter = new FileWriter(settingsFile.getAbsoluteFile());
			BufferedWriter settingsBw = new BufferedWriter(settingsWriter);
			settingsBw.write(projects.stream().sequential().map(p -> "'" + p.getName() + "'")
					.collect(joining(", ", "include ", "")));
			settingsBw.close();
			settingsWriter.close();

			FileWriter buildWriter = new FileWriter(buildFile.getAbsoluteFile());
			BufferedWriter buildBw = new BufferedWriter(buildWriter);

			BuildScriptHelper.addBuildScript(projects, buildBw);
			BuildScriptHelper.addAllProjects(buildBw);
			projects.forEach(p -> Try.successful(p).peek(tp -> addProject(tp, dependencies, buildBw)));

			// Add task here for now
			buildBw.write("\n");
			buildBw.write("tasks.eclipse.doLast {\n");
			buildBw.write("    delete \".project\"\n");
			buildBw.write("}");

			buildBw.close();
			buildWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void cleanUp() {
		settingsFile.deleteOnExit();
		buildFile.deleteOnExit();
	}
}
