package com.gurella.engine.scene;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.application.ApplicationUpdateListener;

public class SceneCompositionTest {
	public static void main(String[] args) {
		Scene scene = new Scene();
		scene.newSystem(TestSystem.class);
		SceneNode2 node1 = scene.newNode("node 1");
		node1.newComponent(TestComponent.class);
		System.out.println(scene.getDiagnostics());

		scene.start();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		node1.removeComponent(TestComponent.class);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		TransformComponent transform1 = node1.newComponent(TransformComponent.class);
		update();

		SceneNode2 node2 = node1.newChild("node 2");
		TransformComponent transform2 = node2.newComponent(TransformComponent.class);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		node2.activate();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		Vector3 out = new Vector3();
		transform1.translate(1, 1, 1);
		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		transform1.translate(1, 1, 1);
		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		scene.addNode(node2);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		node1.addChild(node2);
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());

		transform2.getWorldTranslation(out);
		System.out.println("\n\n\n");
		System.out.println(out);

		transform1.disable();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());
		transform2.getWorldTranslation(out);
		System.out.println(out);

		transform1.enable();
		update();
		System.out.println("\n\n\n");
		System.out.println(scene.getDiagnostics());
		transform2.getWorldTranslation(out);
		System.out.println(out);
	}

	private static void update() {
		Array<ApplicationUpdateListener> listeners = new Array<ApplicationUpdateListener>();
		EventService.getSubscribers(ApplicationUpdateListener.class, listeners);
		for (int i = 0; i < listeners.size; i++) {
			listeners.get(i).update();
		}
	}

	private static class TestSystem extends SceneSystem2 implements Poolable {
		@Override
		public void reset() {
		}
	}

	private static class TestComponent extends SceneNodeComponent2 implements Poolable {
		@Override
		public void reset() {
		}
	}
}
