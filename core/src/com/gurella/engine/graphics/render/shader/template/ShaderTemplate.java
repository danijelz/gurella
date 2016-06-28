package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.Assets;

public class ShaderTemplate extends ShaderTemplateNode {
	private final ObjectMap<String, FileHandle> dependenciesByPath = new ObjectMap<String, FileHandle>();
	private final Array<ShaderDependency> dependencies = new Array<ShaderDependency>();

	public void addDependency(String dependencyPath) {
		// TODO
		// FileHandle dependencyFile = Assets.getFileHandle(dependencyPath);
		// dependenciesByPath.put(dependencyPath, dependencyFile);
		ShaderDependency dependency = new ShaderDependency();
		// dependency.dependencyFile = dependencyFile;
		dependencies.add(dependency);
	}
}
