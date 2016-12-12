package com.gurella.studio.wizard;

import static com.gurella.studio.editor.utils.PrettyPrintSerializer.serialize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.editor.utils.Try;

public class NewSceneCreationPage extends WizardNewFileCreationPage {
	public NewSceneCreationPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
	}

	@Override
	protected InputStream getInitialContents() {
		String serialized = serialize("", Scene.class, newScene());
		return new ByteArrayInputStream(Try.ofFailable(() -> serialized.getBytes("UTF-8")).getUnchecked());
	}

	private static Scene newScene() {
		Scene scene = new Scene();
		SceneNode node = scene.newNode("Main camera");
		node.newComponent(TransformComponent.class);
		node.newComponent(PerspectiveCameraComponent.class);
		node = scene.newNode("Directional light");
		node.newComponent(TransformComponent.class);
		node.newComponent(DirectionalLightComponent.class);
		return scene;
	}
}
