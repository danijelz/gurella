package com.gurella.studio.wizard.project.setup;

import com.gurella.engine.utils.Uuid;

public class SampleMaterialFile extends GeneratedProjectFile {
	public SampleMaterialFile(String outputName) {
		super(outputName);
	}

	@Override
	protected String generate() {
		//@formatter:off
		return "{\r\n" 
				+ "		0: {\r\n" 
				+ "			uuid: " + Uuid.randomUuidString() + "\r\n"
				+ "			diffuseColor: -352264961\r\n" 
				+ "			diffuseTexture: {}\r\n"
				+ "			specularColor: -352264961\r\n" 
				+ "			specularTexture: {}\r\n"
				+ "			ambientTexture: {}\r\n" 
				+ "			emissiveTexture: {}\r\n"
				+ "			reflectionTexture: {}\r\n" 
				+ "			bumpTexture: {}\r\n"
				+ "			normalTexture: {}\r\n" 
				+ "			blend: {}\r\n" 
				+ "			depthTest: {}\r\n"
				+ "			shininess: 20\r\n" 
				+ "		}\r\n" 
				+ "		}";
		//@formatter:on
	}
}
