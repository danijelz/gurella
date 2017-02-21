package com.gurella.studio.wizard.scene;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.gurella.engine.utils.Uuid;
import com.gurella.studio.editor.utils.Try;

public class NewScenePage extends WizardNewFileCreationPage {
	public NewScenePage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
	}

	@Override
	protected InputStream getInitialContents() {
		return new ByteArrayInputStream(Try.ofFailable(() -> newScene().getBytes("UTF-8")).getUnchecked());
	}

	private static String newScene() {
		// @formatter:off
		return  "{\n" +
				"	0: {\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n" +
				"		nodes: {\n" +
				"			elements: [ 3, 1, 2, 3 ]\n" +
				"		}\n" +
				"	}\n" +
				"	1: {\n" +
				"		#: 2\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n" +
				"		name: Environment\n" +
				"		children: {\n" +
				"			elements: [ 1, 4 ]\n" +
				"		}\n" +
				"	}\n" +
				"	2: {\n" +
				"		#: 2\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n"  +
				"		name: Main camera\n" +
				"		components: {\n" +
				"			elements: [ 2, 5, 6 ]\n" +
				"		}\n" +
				"	}\n" +
				"	3: {\n" +
				"		#: 2\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n" +
				"		name: Box\n" +
				"		components: {\n" +
				"			elements: [ 2, 7, 8 ]\n" +
				"		}\n" +
				"	}\n" +
				"	4: {\n" +
				"		#: 2\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n" +
				"		name: Sun\n" +
				"		components: {\n" +
				"			elements: [ 1, 9 ]\n" +
				"		}\n" +
				"	}\n" +
				"	5: {\n" +
				"		#: 3\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n"  +
				"		translation: { y: 1, z: 3 }\n" +
				"	}\n" +
				"	6: {\n" +
				"		#: 8\n" +
				"		uuid: " + Uuid.randomUuidString() + "\n" +
				"	}\n" +
				"	7: { #: 3, uuid: " + Uuid.randomUuidString() + " }\n" +
				"	8: { #: 15, uuid: " + Uuid.randomUuidString() + ", shape: 10 }\n" +
				"	9: { #: 9, uuid: " + Uuid.randomUuidString() + ", color: -21761 }\n" +
				"	10: { #: com.gurella.engine.scene.renderable.shape.BoxShapeModel }\n" +
				"	}"
				;
		// @formatter:on
	}
}
