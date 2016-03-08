package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.object.PrefabReference;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Values;

abstract class SceneElementsProperty<T extends SceneElement2> implements Property<ImmutableArray<T>> {
	String name;

	public SceneElementsProperty(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<ImmutableArray<T>> getType() {
		return Values.cast(ImmutableArray.class);
	}

	@Override
	public Range<?> getRange() {
		return null;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public boolean isCopyable() {
		return true;
	}

	@Override
	public String getDescriptiveName() {
		return name;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getGroup() {
		return null;
	}

	protected boolean isElementContained(T element, ImmutableArray<T> templateElements) {
		PrefabReference prefab = element.getPrefab();
		if (prefab == null) {
			return false;
		}

		int prefabInstanceId = prefab.getPrefab().getInstanceId();

		for (int i = 0; i < templateElements.size(); i++) {
			T templateElement = templateElements.get(i);
			if (prefabInstanceId == templateElement.getInstanceId()) {
				return true;
			}
		}
		return false;
	}
	
	static class SceneElements<T extends SceneElement2> {
		Array<String> removedTemplateElements;
		Array<T> elements;
	}
}
