package com.gurella.engine.managedobject;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
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
	public boolean isAsset() {
		return false;
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
	public Property<PrefabReference> newInstance(MetaType<?> owner) {
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
				output.writeObjectProperty(name, PrefabReference.class, value, templateValue);
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

			PrefabReference prefab = PrefabReference.obtain(templateValue.uuid, templateValue.fileName);
			((ManagedObject) object).prefab = prefab;
		}
	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		ManagedObject originalObj = (ManagedObject) original;
		PrefabReference originalPrefab = originalObj.prefab;
		ManagedObject duplicateObj = (ManagedObject) duplicate;

		if (originalPrefab == null) {
			String fileName = AssetService.getFileName(originalObj);
			if (fileName != null) {
				PrefabReference prefab = PrefabReference.obtain(originalObj.ensureUuid(), fileName);
				duplicateObj.prefab = prefab;
			}
		} else {
			PrefabReference prefab = PrefabReference.obtain( originalPrefab.uuid, originalPrefab.fileName);
			duplicateObj.prefab = prefab;
		}
	}
}
