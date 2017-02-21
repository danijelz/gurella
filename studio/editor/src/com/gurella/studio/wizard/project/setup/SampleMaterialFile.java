package com.gurella.studio.wizard.project.setup;

import com.gurella.engine.utils.Uuid;

public class SampleMaterialFile extends GeneratedProjectFile {
	public SampleMaterialFile() {
		super("core/assets/materials/sampleMaterial.gmat");
	}

	@Override
	protected String generate() {
		//@formatter:off
		return "{\n"
				+ "0: {\n"
				+ "		uuid: " + Uuid.randomUuidString() + "\n"
				+ "		diffuseColor: -1140915969\n"
				+ "	  }\n"
				+ "}";
		//@formatter:on
	}
}
