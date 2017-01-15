package com.gurella.studio.wizard.project.setup;

import java.io.File;

/**
 * A temporary file that wraps {@link ProjectFile}
 * 
 * @author Tomski
 */
public class TemporaryProjectFile extends ProjectFile {
	public File file;

	public TemporaryProjectFile(File file, String outputName, boolean isTemplate) {
		super(outputName, isTemplate);
		this.file = file;
	}
}
