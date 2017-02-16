package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ShaderTemplate extends ShaderTemplateNode {
	private final ObjectMap<String, FileHandle> dependenciesByPath = new ObjectMap<String, FileHandle>();
	private final Array<ShaderDependency> dependenciesOld = new Array<ShaderDependency>();
	private final Array<String> dependencies = new Array<String>();

	private final ObjectMap<String, PieceNode> piecesByName = new ObjectMap<String, PieceNode>();

	public Array<AssetDescriptor<?>> getDependenciesOld() {
		Array<AssetDescriptor<?>> dependencies = new Array<AssetDescriptor<?>>();
		for (FileHandle handle : dependenciesByPath.values()) {
			dependencies.add(new AssetDescriptor<ShaderTemplate>(handle, ShaderTemplate.class));
		}
		return dependencies;
	}

	public void collectDependencies(DependencyCollector collector) {
		for (int i = 0, n = dependencies.size; i < n; i++) {
			String dependency = dependencies.get(i);
			collector.addDependency(dependency, FileType.Internal, ShaderTemplate.class);
		}
	}

	public void initDependencies(DependencySupplier supplier) {
		for (int i = 0, n = dependencies.size; i < n; i++) {
			String dependency = dependencies.get(i);
			ShaderTemplate template = supplier.getDependency(dependency, FileType.Internal, ShaderTemplate.class, null);
			for (Entry<String, PieceNode> entry : template.piecesByName) {
				if (!piecesByName.containsKey(entry.key)) {
					piecesByName.put(entry.key, entry.value);
				}
			}
		}
	}

	public void initDependencies(AssetManager assetManager) {
		for (int i = 0, n = dependenciesOld.size; i < n; i++) {
			ShaderDependency dependency = dependenciesOld.get(i);
			ShaderTemplate template = assetManager.get(dependency.dependencyFile.path());
			dependency.template = template;
			for (Entry<String, PieceNode> entry : template.piecesByName) {
				if (!piecesByName.containsKey(entry.key)) {
					piecesByName.put(entry.key, entry.value);
				}
			}
		}
	}

	public void addDependency(String dependencyPath) {
		dependencies.add(dependencyPath);
		FileHandle dependencyFile = new FileHandle(dependencyPath);// TODO Assets.getFileHandle(dependencyPath);
		dependenciesByPath.put(dependencyPath, dependencyFile);
		ShaderDependency dependency = new ShaderDependency();
		dependency.dependencyFile = dependencyFile;
		dependenciesOld.add(dependency);
	}

	public void addPiece(PieceNode piece) {
		piecesByName.put(piece.name, piece);
	}

	@Override
	public void generate(ShaderGeneratorContext context) {
		preprocessChildren(context);
		generateChildren(context);
	}

	public PieceNode getPiece(String pieceName) {
		return piecesByName.get(pieceName);
	}

	@Override
	protected String toStringValue() {
		return "";
	}
}
