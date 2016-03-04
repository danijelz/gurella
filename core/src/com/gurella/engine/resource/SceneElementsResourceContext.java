package com.gurella.engine.resource;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class SceneElementsResourceContext extends ResourceContext implements Serializable {
	private static final String SCENE_NODES_TAG = "sceneNodes";
	private static final String SCENE_NODE_TEMPLATES_TAG = "sceneNodeTemplates";
	private static final String SCENE_NODE_COMPONENTS_TAG = "sceneNodeComponents";
	private static final String SCENE_NODE_COMPONENT_TEMPLATES_TAG = "sceneNodeComponentTemplates";
	private static final String SCENE_SYSTEMS_TAG = "sceneSystems";
	private static final String SCENE_SYSTEM_TEMPLATES_TAG = "sceneSystemTemplates";
	private static final String ASSETS_TAG = "assets";
	private static final String ASSET_DESCRIPTORS_TAG = "assetDescriptors";

	private IntMap<SharedResourceReference<? extends SceneNode>> sceneNodes = new IntMap<SharedResourceReference<? extends SceneNode>>();
	private IntMap<TemplateResourceReference<? extends SceneNode>> sceneNodeTemplates = new IntMap<TemplateResourceReference<? extends SceneNode>>();

	private IntMap<SharedResourceReference<? extends SceneNodeComponent>> sceneNodeComponents = new IntMap<SharedResourceReference<? extends SceneNodeComponent>>();
	private IntMap<TemplateResourceReference<? extends SceneNodeComponent>> sceneNodeComponentTemplates = new IntMap<TemplateResourceReference<? extends SceneNodeComponent>>();

	private IntMap<SharedResourceReference<? extends SceneSystem>> sceneSystems = new IntMap<SharedResourceReference<? extends SceneSystem>>();
	private IntMap<TemplateResourceReference<? extends SceneSystem>> sceneSystemTemplates = new IntMap<TemplateResourceReference<? extends SceneSystem>>();

	private IntMap<AssetResourceReference<?>> assets = new IntMap<AssetResourceReference<?>>();

	// TODO defaultresolvers and loaders (can be overridn in children)
	/*
	 * FileHandleResolver resolver; AssetLoader<?, ?> loader;
	 */

	public SceneElementsResourceContext(ResourceContext parent) {
		super(parent);
	}

	public IntMap<SharedResourceReference<? extends SceneNode>> getSceneNodes() {
		return sceneNodes;
	}

	public IntMap<TemplateResourceReference<? extends SceneNode>> getSceneNodeTemplates() {
		return sceneNodeTemplates;
	}

	public IntMap<SharedResourceReference<? extends SceneNodeComponent>> getSceneNodeComponents() {
		return sceneNodeComponents;
	}

	public IntMap<TemplateResourceReference<? extends SceneNodeComponent>> getSceneNodeComponentTemplates() {
		return sceneNodeComponentTemplates;
	}

	public IntMap<SharedResourceReference<? extends SceneSystem>> getSceneSystems() {
		return sceneSystems;
	}

	public IntMap<TemplateResourceReference<? extends SceneSystem>> getSceneSystemTemplates() {
		return sceneSystemTemplates;
	}

	@Override
	public void add(ResourceReference<?> reference) {
		super.add(reference);
		if (reference instanceof SharedResourceReference) {
			addSharedReference((SharedResourceReference<?>) reference);
		} else if (reference instanceof TemplateResourceReference) {
			addTemplateReference((TemplateResourceReference<?>) reference);
		} else if (reference instanceof AssetResourceReference) {
			AssetResourceReference<?> asset = (AssetResourceReference<?>) reference;
			assets.put(asset.getId(), asset);
		}
	}

	private void addSharedReference(SharedResourceReference<?> reference) {
		Class<?> resourceType = reference.getResourceType();
		if (ClassReflection.isAssignableFrom(SceneNode.class, resourceType)) {
			@SuppressWarnings("unchecked")
			SharedResourceReference<? extends SceneNode> sceneNode = (SharedResourceReference<? extends SceneNode>) reference;
			sceneNodes.put(sceneNode.getId(), sceneNode);
		} else if (ClassReflection.isAssignableFrom(SceneNodeComponent.class, resourceType)) {
			@SuppressWarnings("unchecked")
			SharedResourceReference<? extends SceneNodeComponent> sceneNodeComponent = (SharedResourceReference<? extends SceneNodeComponent>) reference;
			sceneNodeComponents.put(sceneNodeComponent.getId(), sceneNodeComponent);
		} else if (ClassReflection.isAssignableFrom(SceneSystem.class, resourceType)) {
			@SuppressWarnings("unchecked")
			SharedResourceReference<? extends SceneSystem> sceneSystem = (SharedResourceReference<? extends SceneSystem>) reference;
			sceneSystems.put(sceneSystem.getId(), sceneSystem);
		}
	}

	private void addTemplateReference(TemplateResourceReference<?> reference) {
		Class<?> resourceType = reference.getResourceType();
		if (ClassReflection.isAssignableFrom(SceneNode.class, resourceType)) {
			@SuppressWarnings("unchecked")
			TemplateResourceReference<? extends SceneNode> sceneNode = (TemplateResourceReference<? extends SceneNode>) reference;
			sceneNodeTemplates.put(sceneNode.getId(), sceneNode);
		} else if (ClassReflection.isAssignableFrom(SceneNodeComponent.class, resourceType)) {
			@SuppressWarnings("unchecked")
			TemplateResourceReference<? extends SceneNodeComponent> sceneNodeComponent = (TemplateResourceReference<? extends SceneNodeComponent>) reference;
			sceneNodeComponentTemplates.put(sceneNodeComponent.getId(), sceneNodeComponent);
		} else if (ClassReflection.isAssignableFrom(SceneSystem.class, resourceType)) {
			@SuppressWarnings("unchecked")
			TemplateResourceReference<? extends SceneSystem> sceneSystem = (TemplateResourceReference<? extends SceneSystem>) reference;
			sceneSystemTemplates.put(sceneSystem.getId(), sceneSystem);
		}
	}

	@Override
	public ResourceReference<?> remove(int resourceId) {
		ResourceReference<?> reference = super.remove(resourceId);
		if (reference instanceof SharedResourceReference) {
			removeSharedReference((SharedResourceReference<?>) reference);
		} else if (reference instanceof TemplateResourceReference) {
			removeTemplateReference((TemplateResourceReference<?>) reference);
		} else if (reference instanceof AssetResourceReference) {
			assets.remove(reference.getId());
		}
		return reference;
	}

	private void removeSharedReference(SharedResourceReference<?> reference) {
		Class<?> resourceType = reference.getResourceType();
		if (ClassReflection.isAssignableFrom(SceneNode.class, resourceType)) {
			sceneNodes.remove(reference.getId());
		} else if (ClassReflection.isAssignableFrom(SceneNodeComponent.class, resourceType)) {
			sceneNodeComponents.remove(reference.getId());
		} else if (ClassReflection.isAssignableFrom(SceneSystem.class, resourceType)) {
			sceneSystems.remove(reference.getId());
		}
	}

	private void removeTemplateReference(TemplateResourceReference<?> reference) {
		Class<?> resourceType = reference.getResourceType();
		if (ClassReflection.isAssignableFrom(SceneNode.class, resourceType)) {
			sceneNodeTemplates.remove(reference.getId());
		} else if (ClassReflection.isAssignableFrom(SceneNodeComponent.class, resourceType)) {
			sceneNodeComponentTemplates.remove(reference.getId());
		} else if (ClassReflection.isAssignableFrom(SceneSystem.class, resourceType)) {
			sceneSystemTemplates.remove(reference.getId());
		}
	}

	@Override
	public void write(Json json) {
		json.writeArrayStart(SCENE_NODES_TAG);
		for (Entry<SharedResourceReference<? extends SceneNode>> entry : sceneNodes.entries()) {
			json.writeValue(entry.value, SharedResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(SCENE_NODE_TEMPLATES_TAG);
		for (Entry<TemplateResourceReference<? extends SceneNode>> entry : sceneNodeTemplates.entries()) {
			json.writeValue(entry.value, TemplateResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(SCENE_NODE_COMPONENTS_TAG);
		for (Entry<SharedResourceReference<? extends SceneNodeComponent>> entry : sceneNodeComponents.entries()) {
			json.writeValue(entry.value, SharedResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(SCENE_NODE_COMPONENT_TEMPLATES_TAG);
		for (Entry<TemplateResourceReference<? extends SceneNodeComponent>> entry : sceneNodeComponentTemplates
				.entries()) {
			json.writeValue(entry.value, TemplateResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(SCENE_SYSTEMS_TAG);
		for (Entry<SharedResourceReference<? extends SceneSystem>> entry : sceneSystems.entries()) {
			json.writeValue(entry.value, SharedResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(SCENE_SYSTEM_TEMPLATES_TAG);
		for (Entry<TemplateResourceReference<? extends SceneSystem>> entry : sceneSystemTemplates.entries()) {
			json.writeValue(entry.value, TemplateResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(ASSETS_TAG);
		for (Entry<AssetResourceReference<?>> entry : assets.entries()) {
			json.writeValue(entry.value, AssetResourceReference.class);
		}
		json.writeArrayEnd();

		json.writeArrayStart(ASSET_DESCRIPTORS_TAG);
		for (ObjectMap.Entry<String, AssetResourceDescriptor<?>> entry : getAssetDescriptors().entries()) {
			json.writeValue(entry.value, AssetResourceDescriptor.class);
		}
		json.writeArrayEnd();
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue values = jsonData.get(SCENE_NODES_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(SharedResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(SCENE_NODE_TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(TemplateResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(SCENE_NODE_COMPONENTS_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(SharedResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(SCENE_NODE_COMPONENT_TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(TemplateResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(SCENE_SYSTEMS_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(SharedResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(SCENE_SYSTEM_TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(TemplateResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(ASSETS_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				add(json.readValue(AssetResourceReference.class, resourceValue));
			}
		}

		values = jsonData.get(ASSET_DESCRIPTORS_TAG);
		if (values != null) {
			for (JsonValue resourceValue : values) {
				addAssetDescriptor(json.readValue(AssetResourceDescriptor.class, resourceValue));
			}
		}
	}
}
