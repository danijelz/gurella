package com.gurella.studio.wizard.setup;

import java.io.File;

/**
 * A temporary file that wraps {@link ProjectFile} for use in a {@link Project}
 * 
 * @author Tomski
 */
public class TemporaryProjectFile extends ProjectFile {
	public File file;

	public TemporaryProjectFile(File file, String outputString, boolean isTemplate) {
		super(outputString, isTemplate);
		this.file = file;
	}
}
