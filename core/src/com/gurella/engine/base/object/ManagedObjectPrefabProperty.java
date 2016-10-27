package com.gurella.engine.base.object;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Values;

class ManagedObjectPrefabProperty implements Property<PrefabReference> {
	public static final String name = "prefab";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<PrefabReference> getType() {
		return PrefabReference.class;
	}

	@Override
	public Range<?> getRange() {
		return null;
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public boolean isFinal() {
		return false;
	}

	@Override
	public boolean isCopyable() {
		return true;
	}

	@Override
	public boolean isFlatSerialization() {
		return true;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Property<PrefabReference> newInstance(Model<?> model) {
		return this;
	}

	@Override
	public PrefabReference getValue(Object object) {
		return ((ManagedObject) object).prefab;
	}

	@Override
	public void setValue(Object object, PrefabReference value) {
		((ManagedObject) object).prefab = value;
	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		PrefabReference value = ((ManagedObject) object).prefab;
		if (value == null && template == null) {
			return;
		}

		PrefabReference templateValue = ((ManagedObject) template).prefab;

		if (!Values.isEqual(value, templateValue)) {
			if (value == null) {
				output.writeNullProperty(name);
			} else {
				output.writeObjectProperty(name, PrefabReference.class, templateValue, value);
			}
		}
	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		if (input.hasProperty(name)) {
			PrefabReference templateValue = template == null ? null : ((ManagedObject) template).prefab;
			((ManagedObject) object).prefab = input.readObjectProperty(name, PrefabReference.class, templateValue);
		} else if (template != null) {
			PrefabReference templateValue = ((ManagedObject) template).prefab;
			if (templateValue == null) {
				return;
			}

			PrefabReference prefab = PrefabReference.obtain(templateValue.fileName, templateValue.uuid);
			((ManagedObject) object).prefab = prefab;
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		ManagedObject originalObj = (ManagedObject) original;
		PrefabReference originalPrefab = originalObj.prefab;

		if (originalPrefab == null) {
			String fileName = AssetService.getFileName(originalObj);
			if (fileName != null) {
				PrefabReference prefab = PrefabReference.obtain(fileName, originalObj.ensureUuid());
				((ManagedObject) duplicate).prefab = prefab;
			}
		} else {
			PrefabReference prefab = PrefabReference.obtain(originalPrefab.fileName, originalPrefab.uuid);
			((ManagedObject) duplicate).prefab = prefab;
		}
	}
}
