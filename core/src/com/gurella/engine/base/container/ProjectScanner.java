package com.gurella.engine.base.container;

import com.badlogic.gdx.files.FileHandle;

public class ProjectScanner {
	private String projectFileName;
	private ObjectManager manager = new ObjectManager();

	public ProjectScanner(String projectFileName) {
		this.projectFileName = projectFileName;
	}
	
	public void scan() {
		FileHandle fileHandle = new FileHandle(projectFileName);
		scan(fileHandle.parent());
	}
	
	private void scan(FileHandle directory) {
		
	}
}
