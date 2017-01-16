package com.gurella.studio.wizard.project.setup;

import com.gurella.engine.utils.Uuid;

public class SampleMaterialFile extends GeneratedProjectFile {
	public SampleMaterialFile(String outputName) {
		super(outputName);
	}

	@Override
	protected String generate() {
		//@formatter:off
		return "{\n"
				+ "0: {\n"
				+ "			uuid: " + Uuid.randomUuidString() + "\n"
				+ "			diffuseColor: -352264961\n"
				+ "	  }\n"
				+ "}";
		//@formatter:on
	}
}
