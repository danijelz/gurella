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
		return "{\r\n" + 
				"0: {\r\n" + 
				"	uuid: " + 
				 Uuid.randomUuidString() + 
				"\r\n	nodes: {\r\n" + 
				"		elements: [ 2, 1, 2 ]\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"1: {\r\n" + 
				"	#: com.gurella.engine.scene.SceneNode\r\n" + 
				"	uuid: " + 
				 Uuid.randomUuidString() + 
				"\r\n	name: Main camera\r\n" + 
				"	components: {\r\n" + 
				"		elements: [ 2, 3, 4 ]\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"2: {\r\n" + 
				"	#: com.gurella.engine.scene.SceneNode\r\n" + 
				"	uuid: " + 
				 Uuid.randomUuidString() + 
				"\r\n	name: Directional light\r\n" + 
				"	components: {\r\n" + 
				"		elements: [ 2, 5, 6 ]\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"3: { #: com.gurella.engine.scene.transform.TransformComponent, uuid: " + 
				 Uuid.randomUuidString() + 
				" }\r\n" + 
				"4: {\r\n" + 
				"	#: com.gurella.engine.scene.camera.PerspectiveCameraComponent\r\n" + 
				"	uuid: " + 
				 Uuid.randomUuidString() + 
				"\r\n	viewport: {}\r\n" + 
				"}\r\n" + 
				"5: { #: com.gurella.engine.scene.transform.TransformComponent, uuid: " + 
				 Uuid.randomUuidString() + 
				" }\r\n" + 
				"6: { #: com.gurella.engine.scene.light.DirectionalLightComponent, uuid: " + 
				 Uuid.randomUuidString() + 
				" }\r\n" + 
				"}";
		// @formatter:on
	}
}
