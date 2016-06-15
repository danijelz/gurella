package com.gurella.engine.graphics.render.shader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.graphics.render.RenderContext;

public class ShaderTemplate {
	private static Pattern includePattern = Pattern.compile("(?m)(?<!//.?)(#include\\s+?<)([^\\n\\r\\f]+?)(>)");

	private final String source;
	private final ObjectMap<String, FileHandle> dependenciesByPath = new ObjectMap<String, FileHandle>();
	private final Array<ShaderDependencyBlock> dependencyBlocks = new Array<ShaderDependencyBlock>();

	public ShaderTemplate(FileHandle file) {
		this.source = file.readString();
		extractIncludes();
	}
	
	public ShaderTemplate(String source) {
		this.source = source;
		extractIncludes();
	}

	private void extractIncludes() {
		Matcher matcher = includePattern.matcher(source);
		while (matcher.find()) {
			String path = matcher.group(2);
			FileHandle dependencyFile = dependenciesByPath.get(path);
			if (dependencyFile == null) {
				dependencyFile = Assets.getFileHandle(path);
				dependenciesByPath.put(path, dependencyFile);
			}

			ShaderDependencyBlock block = new ShaderDependencyBlock();
			block.start = matcher.start();
			block.end = matcher.end();
			block.dependencyFile = dependencyFile;
			dependencyBlocks.add(block);
		}
	}

	public Array<AssetDescriptor<?>> getDependencies() {
		Array<AssetDescriptor<?>> dependencies = new Array<AssetDescriptor<?>>();
		for (FileHandle handle : dependenciesByPath.values()) {
			dependencies.add(new AssetDescriptor<>(handle, ShaderTemplate.class));
		}
		return dependencies;
	}

	public void initDependencies(AssetManager assetManager) {
		for (int i = 0, n = dependencyBlocks.size; i < n; i++) {
			ShaderDependencyBlock block = dependencyBlocks.get(i);
			block.template = assetManager.get(block.dependencyFile.path());
		}
	}

	public StringBuilder generate(RenderContext context) {
		StringBuilder builder = new StringBuilder(source);
		for (int i = 0, n = dependencyBlocks.size; i < n; i++) {
			ShaderDependencyBlock block = dependencyBlocks.get(i);
			builder.replace(block.start, block.end, block.template.generate(context).toString());
		}
		return builder;
	}

	private static class ShaderDependencyBlock {
		private int start;
		private int end;
		private FileHandle dependencyFile;
		private ShaderTemplate template;
	}

	public static void main(String[] args) {
		String source = "ddd\n\r//" + "#include <ddd/aaa/nnn1>\n";
		source = "ddd//\n" + "#include <ddd/aaa/nnn1> /#include <ddd/aaa/nnn2>//\n" + "//#include <ddd/aaa/nnn-1>\n"
				+ "#include <ddd/aaa/nnn3>\n" + "  #include <ddd/aaa/nnn4> //#include <ddd/aaa/nnn-2>\n"
				+ "jhgadsg #include <ddd/aaa/nnn5>";
		Matcher matcher = includePattern.matcher(source);

		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			System.out.println(matcher.group(2));
			System.out.println(source.substring(start, end));
		}
	}
}
