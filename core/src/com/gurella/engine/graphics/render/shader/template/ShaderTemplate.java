package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ShaderTemplate extends ShaderTemplateNode {
	private final ObjectMap<String, FileHandle> dependenciesByPath = new ObjectMap<String, FileHandle>();
	private final Array<ShaderDependency> dependencies = new Array<ShaderDependency>();

	private final ObjectMap<String, PieceNode> piecesByName = new ObjectMap<String, PieceNode>();

	public Array<AssetDescriptor<?>> getDependencies() {
		Array<AssetDescriptor<?>> dependencies = new Array<AssetDescriptor<?>>();
		for (FileHandle handle : dependenciesByPath.values()) {
			dependencies.add(new AssetDescriptor<ShaderTemplate>(handle, ShaderTemplate.class));
		}
		return dependencies;
	}

	public void initDependencies(AssetManager assetManager) {
		for (int i = 0, n = dependencies.size; i < n; i++) {
			ShaderDependency dependency = dependencies.get(i);
			ShaderTemplate template = assetManager.get(dependency.dependencyFile.path());
			dependency.template = template;
			piecesByName.putAll(template.piecesByName);
		}
	}

	public void addDependency(String dependencyPath) {
		FileHandle dependencyFile = new FileHandle(dependencyPath);// TODO Assets.getFileHandle(dependencyPath);
		dependenciesByPath.put(dependencyPath, dependencyFile);
		ShaderDependency dependency = new ShaderDependency();
		dependency.dependencyFile = dependencyFile;
		dependencies.add(dependency);
	}

	public void addPiece(PieceNode piece) {
		piecesByName.put(piece.name, piece);
	}

	public void generate(StringBuilder builder) {
		generateChildren(this, builder);
	}

	@Override
	protected void generate(ShaderTemplate template, StringBuilder builder) {
		generateChildren(template, builder);
	}

	public PieceNode getPiece(String pieceName) {
		return piecesByName.get(pieceName);
	}
}
