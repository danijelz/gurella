package com.gurella.engine.test;

import java.util.Comparator;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.application.SceneTransition;
import com.gurella.engine.async.AsyncCallback;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.scene.manager.NodeManager.SceneNodeFamily;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.scene.renderable.LayerMask;
import com.gurella.engine.scene.spatial.Spatial;
import com.gurella.engine.scene.tag.Tag;
import com.gurella.engine.utils.ImmutableArray;

@SuppressWarnings("unused")
public abstract class BehaviourComponent extends SceneNodeComponent {
	// TODO onJointBreak

	// TODO box2d physics events

	// TODO animation events

	// TODO layer events

	// //////////TODO METHODS

	public void loadScene(String sceneId, SceneTransition transition) {
	}

	public void nextScene(SceneTransition transition) {
	}

	public void previousScene(SceneTransition transition) {
	}

	// TODO InputMapper InputContext

	public void addInputProcessor(InputProcessor inputProcessor) {
	}

	public void removeInputProcessor(InputProcessor inputProcessor) {
	}

	public <T extends SceneNodeComponent> T getComponent(Class<T> componnetType) {
		return getNode().getComponent(componnetType);
	}

	public Array<? super SceneNodeComponent> getComponents(Array<? super SceneNodeComponent> out) {
		// Values<SceneNodeComponent2> components = getNode().getComponents();
		// while (components.hasNext) {
		// out.add(components.next());
		// }
		return out;
	}

	public <T extends SceneNodeComponent> T getComponentInChildren(Class<T> componetType) {
		// ImmutableArray<SceneNode2> children = getNode().children;
		// for (int i = 0; i < children.size(); i++) {
		// T component = children.get(i).getComponent(componetType);
		// if (component != null) {
		// return component;
		// }
		// }
		return null;
	}

	public <T extends SceneNodeComponent> Array<T> getComponentsInChildren(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public <T extends SceneNodeComponent> T getComponentInParent(Class<T> componnetType) {
		return null;
	}

	public <T extends SceneNodeComponent> Array<T> getComponentsInParent(Class<T> componnetType, Array<T> out) {
		return null;
	}

	public Array<SceneNodeComponent> getSceneComponents(Class<? extends SceneNodeComponent> componentClass,
			Comparator<SceneNodeComponent> comparator) {
		return null;
	}

	public SceneNode getParentNode() {
		SceneNode node = getNode();
		return null;// node == null ? null : node.getParent();
	}

	public <T> T obtainResource(int resourceId) {
		return null;
	}

	public <T> void obtainResourceAsync(int resourceId, AsyncCallback<T> callback) {
	}

	public boolean releaseResource(Object resource) {
		return false;
	}

	public <T extends Disposable> T manageDisposable(T disposable) {
		return null;
	}

	public void dispose(Disposable disposable) {
	}

	public SceneNodeFamily[] registerNodeGroupsOnInit() {
		return null;
	}

	public void registerNodeFamily(SceneNodeFamily nodeFamily) {

	}

	public void unregisterNodeFamily(SceneNodeFamily nodeFamily) {

	}

	public Array<SceneNode> getNodes(SceneNodeFamily nodeFamily) {
		return null;
	}

	public <T extends SceneNodeComponent> void registerComponentFamily(ComponentFamily componentFamily) {
	}

	public <T extends SceneNodeComponent> void unregisterComponentFamily(ComponentFamily componentFamily) {
	}

	public <T extends SceneNodeComponent> ImmutableArray<T> getComponents(ComponentFamily componentFamily) {
		return null;
	}

	public void addTag(Tag tag) {
	}

	public void removeTag(Tag tag) {
	}

	public void setLayer(Layer layer) {
	}

	public void getSpatials(BoundingBox bounds, Array<Spatial> out, LayerMask layers) {
	}

	public void getSpatials(Frustum frustum, Array<Spatial> out, LayerMask layers) {
	}

	public void getSpatials(Ray ray, Array<Spatial> out, LayerMask layers) {
	}

	public void getSpatials(Ray ray, float maxDistance, Array<Spatial> out, LayerMask layers) {
	}

	public void registerListener(Object listener) {
	}

	public void unregisterListener(Object listener) {
	}
}
