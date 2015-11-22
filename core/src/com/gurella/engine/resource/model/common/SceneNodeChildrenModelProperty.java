package com.gurella.engine.resource.model.common;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.model.ResourceId;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelUtils;
import com.gurella.engine.utils.Range;

public class SceneNodeChildrenModelProperty implements ResourceModelProperty {
	private static final SceneNodeChildrenModelProperty instance = new SceneNodeChildrenModelProperty();
	private static final Array<SceneNode> defaultValue = new Array<SceneNode>();

	public static SceneNodeChildrenModelProperty getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "children";
	}

	@Override
	public Range<?> getRange() {
		return null;
	}

	@Override
	public String getDescriptiveName() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getGroup() {
		return "";
	}

	@Override
	public void initFromSerializableValue(Object resource, Object serializableValue, ResourceMap dependencies) {
		if (serializableValue == null) {
			return;
		}

		SceneNode node = (SceneNode) resource;
		for (Object child : (Array<?>) serializableValue) {
			node.addChild(ResourceModelUtils.<SceneNode> resolvePropertyValue(child, dependencies));
		}
	}

	@Override
	public Class<?> getPropertyType() {
		return Array.class;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public void initFromDefaultValue(Object resource) {
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void writeValue(Json json, Object serializableValue) {
		@SuppressWarnings("unchecked")
		Array<ResourceId> array = (Array<ResourceId>) serializableValue;
		if (array.size > 0) {
			int[] ids = new int[array.size];
			for (int i = 0; i < ids.length; i++) {
				ResourceId resourceId = array.get(i);
				ids[i] = resourceId.getId();
			}
			json.writeValue(getName(), ids, int[].class);
		}
	}

	@Override
	public Object readValue(Json json, JsonValue propertyValue) {
		Array<ResourceId> array = new Array<ResourceId>();
		int[] ids = propertyValue.asIntArray();
		for (int i = 0; i < ids.length; i++) {
			array.add(new ResourceId(ids[i]));
		}
		return array;
	}
}
