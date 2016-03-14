package com.gurella.engine.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.base.object.Prefabs;
import com.gurella.engine.base.resource.FileService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.movement.TransformComponent;

public class SceneSerializationTestApp {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gdx Editor";
		cfg.useGL30 = false;

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		cfg.width = 800;
		cfg.height = 600;
		cfg.initialBackgroundColor = Color.BLACK;

		new LwjglApplication(new TestApplicationListener(), cfg);
	}

	private static class TestApplicationListener extends ApplicationAdapter {
		@Override
		public void create() {
			String fileName1 = "/media/danijel/data/ddd/testSceneGurella/Scene1.gsc";
			String fileName2 = "/media/danijel/data/ddd/testSceneGurella/Scene1.gsc";
			
			Scene scene = new Scene();
			scene.newNode("Node 1");
			scene.newNode("Node 2");
			SceneNode2 node3 = scene.newNode("Node 3");
			SceneNode2 node3_1 = node3.newChild("Node 3/1");
			TransformComponent component = node3_1.newComponent(TransformComponent.class);
			component.setTranslation(1, 1, 1);
			
			String fileUuid1 = FileService.getUuid(fileName1);
			Prefabs.saveAsPrefab(scene, Scene.class, fileName1);
			Prefabs.save(scene, Scene.class, fileName2);
		}
	}
}
