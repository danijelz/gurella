package com.gurella.engine.graphics.render.shader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

public class ShaderTemplate {
	//private static Pattern includePattern = Pattern.compile("^#include\\s+?<[^\\n\\r\\f]+?>\\s+?$");
	private static Pattern includePattern = Pattern.compile("(?m)(?<!//.?)(#include\\s+?<)([^\\n\\r\\f]+?)(>)");
	
	private final FileHandle path;
	private final StringBuilder raw;
	private final ObjectMap<ShaderDependency, ShaderTemplate> dependencies = new ObjectMap<ShaderDependency, ShaderTemplate>();

	public ShaderTemplate(FileHandle path) {
		this.path = path;
		this.raw = new StringBuilder(path.readString());
		extractIncludes();
	}
	
	private void extractIncludes() {
		Matcher matcher = includePattern.matcher(raw);
		while(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String group = matcher.group(2);
		}
		// TODO Auto-generated method stub
		
	}

	private static class ShaderDependency {
		private int start;
		private int end;
		private String dependencyPath;
	}
	
	public static void main(String[] args) {
		String raw = "ddd\n\r" + 
				"#include <ddd/aaa/nnn1>\n";
		raw  = "ddd//\n" + 
				"#include <ddd/aaa/nnn1> /#include <ddd/aaa/nnn2>//\n" + 
				"//#include <ddd/aaa/nnn-1>\n" + 
				"#include <ddd/aaa/nnn3>\n" + 
				"  #include <ddd/aaa/nnn4> //#include <ddd/aaa/nnn-2>\n" + 
				"jhgadsg #include <ddd/aaa/nnn5>";
		Matcher matcher = includePattern.matcher(raw);
		
		while(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			System.out.println(matcher.group(2));
			System.out.println(raw.substring(start, end));
		}
	}
}
