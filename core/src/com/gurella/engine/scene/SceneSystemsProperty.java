package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.object.PrefabReference;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SequenceGenerator;

class SceneSystemsProperty extends SceneElementsProperty<SceneSystem2> {
	public SceneSystemsProperty() {
		super("Systems");
	}

	@Override
	public Property<ImmutableArray<SceneSystem2>> newInstance(Model<?> model) {
		return new SceneSystemsProperty();
	}

	@Override
	public ImmutableArray<SceneSystem2> getValue(Object object) {
		return ((Scene) object).systems;
	}

	@Override
	public void setValue(Object object, ImmutableArray<SceneSystem2> value) {
		Scene scene = (Scene) object;
		ImmutableArray<SceneSystem2> systems = scene.systems;
		for (int i = 0; i < systems.size(); i++) {
			SceneSystem2 system = systems.get(i);
			system.destroy();
		}

		for (int i = 0; i < value.size(); i++) {
			SceneSystem2 system = value.get(i);
			scene.addSystem(system);
		}
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		// TODO garbage
		SceneElements<SceneSystem2> elements = new SceneElements<SceneSystem2>();

		Scene scene = (Scene) object;
		ImmutableArray<SceneSystem2> systems = scene.systems;
		if (template == null) {
			systems.appendAll(elements.elements);
			output.writeObjectProperty(name, SceneElements.class, null, elements);
			return;
		}

		Scene templateScene = (Scene) template;
		ImmutableArray<SceneSystem2> templateSystems = templateScene.systems;
		IntSet templateIds = new IntSet();
		for (int i = 0; i < systems.size(); i++) {
			SceneSystem2 system = systems.get(i);
			int prefabId = getPrefabId(system);
			if (SequenceGenerator.invalidId != prefabId) {
				templateIds.add(prefabId);
			}
			elements.elements.add(system);
		}

		for (int i = 0; i < templateSystems.size(); i++) {
			SceneSystem2 system = templateSystems.get(i);
			if (!templateIds.contains(system.getInstanceId())) {
				elements.removedElements.add(system.getUuid());
			}
		}

		output.writeObjectProperty(name, SceneElements.class, null, elements);
	}

	private static int getPrefabId(SceneSystem2 system) {
		PrefabReference prefab = system.getPrefab();
		if (prefab == null) {
			return SequenceGenerator.invalidId;
		}
		return prefab.get().getInstanceId();
	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		// TODO garbage
		if (input.hasProperty(name)) {
			Scene scene = (Scene) object;
			Scene templateScene = (Scene) template;
			@SuppressWarnings("unchecked")
			SceneElements<SceneSystem2> sceneElements = input.readObjectProperty(name, SceneElements.class, null);
			Array<SceneSystem2> systems = sceneElements.elements;

			if (templateScene == null) {
				for (int i = 0; i < systems.size; i++) {
					scene.addSystem(systems.get(i));
				}
				return;
			}

			Array<String> removedElements = sceneElements.removedElements;
			ImmutableArray<SceneSystem2> templateSystems = templateScene.systems;
			int size = systems.size;
			int templateSize = templateSystems.size();
			int max = Math.max(size, templateSize);

			for (int i = 0; i < max; i++) {
				SceneSystem2 system = size < max ? null : systems.get(i);
				SceneSystem2 templateSystem = templateSize < max ? null : templateSystems.get(i);

				if (system == null) {
					if(!removedElements.contains(templateSystem.getUuid(), false)) {
						scene.addSystem(input.copyObject(templateSystem));
					}
				} else if (templateSystem == null) {
					scene.addSystem(system);
				} else {
					if(system.getPrefab() == null) {
						
					}
					if(templateSystem.getUuid().equals(getPrefabUuid(system))) {
						scene.addSystem(system);
					}
				}
			}
		} else if (template != null) {
			Scene scene = (Scene) object;
			Scene templateScene = (Scene) template;
			ImmutableArray<SceneSystem2> templateSystems = templateScene.systems;
			for (int i = 0; i < templateSystems.size(); i++) {
				SceneSystem2 system = templateSystems.get(i);
				scene.addSystem(input.copyObject(system));
			}
		}
	}
	
	private static String getPrefabUuid(SceneSystem2 system) {
		PrefabReference prefab = system.getPrefab();
		return prefab == null ? null : prefab.getUuid();
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		Scene originalScene = (Scene) original;
		Scene duplicateScene = (Scene) duplicate;

		ImmutableArray<SceneSystem2> duplicateSystems = duplicateScene.systems;
		for (int i = 0; i < duplicateSystems.size(); i++) {
			SceneSystem2 system = duplicateSystems.get(i);
			system.destroy();
		}

		ImmutableArray<SceneSystem2> newSystems = originalScene.systems;
		for (int i = 0; i < newSystems.size(); i++) {
			SceneSystem2 system = newSystems.get(i);
			duplicateScene.addSystem(context.copy(system));
		}
	}
}
