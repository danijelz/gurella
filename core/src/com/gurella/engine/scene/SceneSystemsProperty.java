package com.gurella.engine.scene;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class SceneSystemsProperty extends SceneElementsProperty<SceneSystem2> {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		Scene scene = (Scene) context.getObjectStack().peek();
		ImmutableArray<SceneSystem2> systems = scene.systems;
		for (int i = 0; i < systems.size(); i++) {
			SceneSystem2 system = systems.get(i);
			system.destroy();
		}

		ImmutableArray<SceneSystem2> newSystems = Values.cast(original);
		for (int i = 0; i < newSystems.size(); i++) {
			SceneSystem2 system = newSystems.get(i);
			scene.addSystem(system);
		}
	}
}
