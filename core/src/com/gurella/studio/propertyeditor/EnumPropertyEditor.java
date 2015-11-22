package com.gurella.studio.propertyeditor;

import java.util.EnumSet;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.utils.ValueUtils;
import com.kotcrab.vis.ui.widget.VisSelectBox;

public class EnumPropertyEditor<T extends Enum<T>> extends
		SimpleResourcePropertyEditor<VisSelectBox<EnumPropertyEditor.EnumItem<T>>, T> {
	private EnumItem<T> emptyItem;
	private ObjectMap<T, EnumItem<T>> itemsByValue;

	public EnumPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory) {
		super(property, factory);
	}

	@Override
	public void present(T value) {
		if (ValueUtils.isEmpty(value)) {
			valueComponent.setSelected(emptyItem);
		} else {
			valueComponent.setSelected(itemsByValue.get(value));
		}
	}

	@Override
	protected VisSelectBox<EnumItem<T>> createValueComponent() {
		emptyItem = new EnumItem<T>(null);
		itemsByValue = new ObjectMap<T, EnumItem<T>>();
		VisSelectBox<EnumItem<T>> selectBox = new VisSelectBox<EnumItem<T>>();
		selectBox.setItems(createItems());
		return selectBox;
	}

	private Array<EnumItem<T>> createItems() {
		@SuppressWarnings("unchecked")
		Class<T> propertyType = (Class<T>) getProperty().getPropertyType();
		Array<EnumItem<T>> items = new Array<EnumItem<T>>();
		items.add(emptyItem);
		for (T value : EnumSet.allOf(propertyType)) {
			EnumItem<T> item = new EnumItem<T>(value);
			itemsByValue.put(value, item);
			items.add(item);
		}
		return items;
	}

	@Override
	protected T getComponentValue() {
		return valueComponent.getSelected().value;
	}

	static class EnumItem<T extends Enum<T>> {
		T value;

		public EnumItem(T value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value == null
					? ""
					: value.name();
		}
	}
}
