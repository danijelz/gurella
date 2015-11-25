package com.gurella.engine.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.application.Application;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneSystem;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.SceneElementsResourceContext;
import com.gurella.engine.signal.Signal0.Signal0Impl;
import com.gurella.engine.signal.Signal1.Signal1Impl;

public class Scene extends SceneElementsResourceContext {
	private static final String ID_TAG = "id";
	private static final String GROUP_TAG = "group";
	private static final String INITIAL_SYSTEMS_TAG = "initialSystems";
	private static final String INITIAL_NODES_TAG = "initialNodes";

	public static final String defaultGroup = "Default";

	private String id;
	private String group = defaultGroup;
	public final IntArray initialSystems = new IntArray();
	public final IntArray initialNodes = new IntArray();

	public final Signal0Impl startSignal = new Signal0Impl();
	public final Signal0Impl stopSignal = new Signal0Impl();
	public final Signal0Impl pauseSignal = new Signal0Impl();
	public final Signal0Impl resumeSignal = new Signal0Impl();
	public final Signal1Impl<Vector2> resizeSignal = new Signal1Impl<Vector2>();

	public final SceneGraph graph = new SceneGraph(this);

	private final Vector2 screenSize = new Vector2();

	public Scene(Application application, String id) {
		super(application);
		this.id = id;
	}

	public Application getApplication() {
		return (Application) getParent();
	}

	public String getId() {
		return id;
	}

	public String getGroup() {
		return group;
	}

	public void addInitialSystem(int systemId) {
		initialSystems.add(systemId);
	}

	public void removeInitialSystem(int systemId) {
		initialSystems.removeValue(systemId);
	}

	public IntArray getInitialSystems() {
		return initialSystems;
	}

	public void addInitialNode(int nodeId) {
		initialNodes.add(nodeId);
	}

	public void removeInitialNode(int nodeId) {
		initialNodes.removeValue(nodeId);
	}

	public IntArray getInitialNodes() {
		return initialNodes;
	}

	public void init(ResourceMap initialResources) {
		//TODO init graph
		for (int i = 0; i < initialSystems.size; i++) {
			int initialSystemId = initialSystems.get(i);
			SceneSystem system = initialResources.getResource(initialSystemId);
			graph.addSystem(system);
		}

		for (int i = 0; i < initialNodes.size; i++) {
			int initialNodeId = initialNodes.get(i);
			SceneNode node = initialResources.getResource(initialNodeId);
			graph.addNode(node);
		}

		startSignal.dispatch();
	}

	public void stop() {
		stopSignal.dispatch();
		// TODO reset graph
	}

	public void pause() {
		pauseSignal.dispatch();
	}

	public void resume() {
		resumeSignal.dispatch();
	}

	public void resize(int width, int height) {
		screenSize.set(width, height);
		resizeSignal.dispatch(screenSize);
	}

	@Override
	public void write(Json json) {
		json.writeValue(ID_TAG, id);
		json.writeValue(GROUP_TAG, group);

		json.writeArrayStart(INITIAL_SYSTEMS_TAG);
		for (int i = 0; i < initialSystems.size; i++) {
			int initialSystemId = initialSystems.get(i);
			json.writeValue(initialSystemId);
		}
		json.writeArrayEnd();

		json.writeArrayStart(INITIAL_NODES_TAG);
		for (int i = 0; i < initialNodes.size; i++) {
			int initialNodeId = initialNodes.get(i);
			json.writeValue(initialNodeId);
		}
		json.writeArrayEnd();

		super.write(json);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		id = jsonData.getString(ID_TAG);
		group = jsonData.getString(GROUP_TAG);

		JsonValue values = jsonData.get(INITIAL_SYSTEMS_TAG);
		for (JsonValue value : values) {
			addInitialSystem(value.asInt());
		}

		values = jsonData.get(INITIAL_NODES_TAG);
		for (JsonValue value : values) {
			addInitialNode(value.asInt());
		}

		super.read(json, jsonData);
	}

	@Override
	public String toString() {
		return id;
	}
}
