package com.gurella.studio.wizard.project.setup;

import static com.gurella.studio.wizard.project.setup.Dependency.BOX2D;
import static com.gurella.studio.wizard.project.setup.Dependency.BULLET;
import static com.gurella.studio.wizard.project.setup.Dependency.GDX;
import static com.gurella.studio.wizard.project.setup.Dependency.GURELLA;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO unused
public class SetupInfo {
	public String name;
	public String location;
	public String packageName;
	public String mainClass;
	public String androidSdkLocation;
	public String androidApiLevel;
	public String androidBuildToolsVersion;
	
	public List<ProjectType> projects = new ArrayList<ProjectType>();
	public List<Dependency> dependencies = Arrays.asList(GDX, BULLET, BOX2D, GURELLA);
	
	public File settingsFile;
	public File buildFile;
	public List<ProjectFile> files = new ArrayList<ProjectFile>();
	public Map<String, String> replacements = new HashMap<String, String>();
}
