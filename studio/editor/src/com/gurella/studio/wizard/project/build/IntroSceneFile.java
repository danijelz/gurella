package com.gurella.studio.wizard.project.build;

import com.gurella.engine.utils.Uuid;

public class IntroSceneFile  extends GeneratedFile {
	public IntroSceneFile(String sceneName) {
		super("core/assets/scenes/" + sceneName);
	}

	@Override
	protected String generate() {
		//@formatter:off
		return "{\n" + 
				"0: {\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	nodes: {\n" + 
				"		elements: [ 3, 1, 2, 3 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"1: {\n" + 
				"	#: 2\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	name: Environment\n" + 
				"	children: {\n" + 
				"		elements: [ 2, 4, 5 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"2: {\n" + 
				"	#: 2\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	name: Main camera\n" + 
				"	components: {\n" + 
				"		elements: [ 2, 6, 7 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"3: {\n" + 
				"	#: 2\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	name: Box\n" + 
				"	components: {\n" + 
				"		elements: [ 2, 8, 9 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"4: {\n" + 
				"	#: 2\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	name: Sun\n" + 
				"	components: {\n" + 
				"		elements: [ 1, 10 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"5: {\n" + 
				"	#: 2\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	name: Sky\n" + 
				"	components: {\n" + 
				"		elements: [ 1, 11 ]\n" + 
				"	}\n" + 
				"}\n" + 
				"6: {\n" + 
				"	#: 3\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	translation: { y: 1, z: 3 }\n" + 
				"}\n" + 
				"7: {\n" + 
				"	#: 8\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"}\n" + 
				"8: { #: 3, uuid: " + Uuid.randomUuidString() + " }\n" + 
				"9: { #: 15, uuid: " + Uuid.randomUuidString() + ", shape: 12 }\n" + 
				"10: { #: 9, uuid: " + Uuid.randomUuidString() + ", color: -14081 }\n" + 
				"11: {\n" + 
				"	#: 16\n" + 
				"	uuid: " + Uuid.randomUuidString() + "\n" +
				"	texture: { #: @, p: 0 }\n" + 
				"}\n" + 
				"12: {\n" + 
				"	#: com.gurella.engine.scene.renderable.shape.BoxShapeModel\n" + 
				"	materialDescriptor: { #: @, p: 1 }\n" + 
				"}\n" + 
				"d: [ 20 sky/cloudySea.jpg, 18 materials/sampleMaterial.gmat ]\n" + 
				"}";
		//@formatter:on
	}
}
