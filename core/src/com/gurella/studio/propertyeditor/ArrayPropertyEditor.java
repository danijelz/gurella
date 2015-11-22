package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.utils.Range;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ArrayPropertyEditor<T, V> extends AbstractResourcePropertyEditor<ModelResourceFactory<T>> {
	private VisTable editor = new VisTable();

	private VisTable header = new VisTable();
	private VisLabel propertyNameLabel = new VisLabel("");
	private VisTextButton collapseButton = new VisTextButton(" + ");

	private VisTable content = new VisTable();
	private CollapsibleWidget collapsibleWidget;

	private VisTable editorsContent = new VisTable();
	private Array<ArrayItemEditor> itemEditors = new Array<ArrayItemEditor>();
	private VisTextButton addButton = new VisTextButton(" Add Item ");

	private Array<Actor> uiComponents = new Array<Actor>();

	private ModelResourceFactory<T> propertyFactory;
	private Class<V> componentType;

	public ArrayPropertyEditor(ResourceModelProperty property, ModelResourceFactory<T> factory) {
		super(property, factory);

		uiComponents.add(editor);

		propertyNameLabel.setText(property.getName() + ": ");
		propertyNameLabel.setEllipsis(true);
		propertyNameLabel.setWidth(80);

		collapseButton.addListener(new ColapseButtonClickListener());

		addButton.setWidth(40);
		addButton.addListener(new AddButtonClickListener());

		editor.setBackground("border");
		collapsibleWidget = new CollapsibleWidget(content, true);

		header.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		header.add(propertyNameLabel).top().left().fillX().expandX();
		header.add(collapseButton).top().left().width(20);

		editor.add(header).top().left().fillX().expandX();
		editor.row();
		editor.add(collapsibleWidget).top().left().expand().fill();

		content.add(editorsContent).fill().expand();
		content.row();
		content.add(addButton).expandX();
		present(factory);
	}

	@Override
	public Array<Actor> getUiComponents() {
		return uiComponents;
	}

	@Override
	public int getCellspan(int componentIndex) {
		return 2;
	}

	@Override
	public int getRowspan(int componentIndex) {
		return 1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void present(ModelResourceFactory<T> value) {
		Class<T> casted = (Class<T>) property.getPropertyType();
		componentType = (Class<V>) casted.getComponentType();
		propertyFactory = value.getPropertyValue(property.getName());
		if (propertyFactory == null) {
			propertyFactory = new ModelResourceFactory<T>(casted);
		}

		Object items = propertyFactory.getPropertyValue("items");
		if (items != null) {
			int length = ArrayReflection.getLength(items);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(items, i);
				ArrayItemEditor arrayItemEditor = createItemEditor();
				arrayItemEditor.propertyEditor.present(item);
				itemEditors.add(arrayItemEditor);
			}
		}

		presentEditors();
	}

	private ArrayItemEditor createItemEditor() {
		ArrayItemEditor arrayItemEditor = new ArrayItemEditor();
		ItemProperty itemProperty = new ItemProperty(componentType);
		@SuppressWarnings("unchecked")
		ResourcePropertyEditor<Object> casted = (ResourcePropertyEditor<Object>) PropertyEditorFactory.createEditor(
				factory, itemProperty);
		arrayItemEditor.propertyEditor = casted;
		return arrayItemEditor;
	}

	private void presentEditors() {
		editorsContent.clearChildren();
		for (int i = 0; i < itemEditors.size; i++) {
			ArrayItemEditor arrayItemEditor = itemEditors.get(i);
			editorsContent.add(Integer.toString(i) + ": ");
			Array<Actor> propertyUiComponents = arrayItemEditor.propertyEditor.getUiComponents();
			Actor component = propertyUiComponents.size == 1
					? propertyUiComponents.get(0)
					: propertyUiComponents.get(1);
			editorsContent.add(component).fillX().expandX();
			editorsContent.add(arrayItemEditor.removeButton);
			editorsContent.row();
		}
	}

	@Override
	public void save() {
		factory.setPropertyValue(property.getName(), getValue());
	}

	@Override
	public Object getValue() {
		Class<?> serializableValueType;
		if (componentType.isPrimitive() || componentType == String.class || componentType == Integer.class
				|| componentType == Boolean.class || componentType == Float.class || componentType == Long.class
				|| componentType == Double.class || componentType == Short.class || componentType == Byte.class
				|| componentType == Character.class || ClassReflection.isAssignableFrom(Enum.class, componentType)) {
			serializableValueType = componentType;
		} else {
			serializableValueType = ModelResourceFactory.class;
		}

		Object items = ArrayReflection.newInstance(serializableValueType, itemEditors.size);
		for (int i = 0; i < itemEditors.size; i++) {
			ArrayReflection.set(items, i, itemEditors.get(i).propertyEditor.getValue());
		}

		propertyFactory.setPropertyValue("items", items);

		return propertyFactory;
	}

	private final class ColapseButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			if (collapsibleWidget.isCollapsed()) {
				collapseButton.setText(" - ");
				collapsibleWidget.setCollapsed(false);
			} else {
				collapseButton.setText(" + ");
				collapsibleWidget.setCollapsed(true);
			}
		}
	}

	private final class AddButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			itemEditors.add(createItemEditor());
			presentEditors();
		}
	}

	private class ArrayItemEditor extends VisTable {
		private ResourcePropertyEditor<Object> propertyEditor;
		private VisTextButton removeButton = new VisTextButton(" - ");

		public ArrayItemEditor() {
			removeButton.addListener(new RemoveButtonClickListener(this));
		}
	}

	private final class RemoveButtonClickListener extends ClickListener {
		private ArrayItemEditor arrayItemEditor;

		public RemoveButtonClickListener(ArrayItemEditor arrayItemEditor) {
			this.arrayItemEditor = arrayItemEditor;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			itemEditors.removeValue(arrayItemEditor, true);
			presentEditors();
		}
	}

	public static class ItemProperty implements ResourceModelProperty {
		private Class<?> componentType;

		public ItemProperty(Class<?> componentType) {
			this.componentType = componentType;
		}

		@Override
		public String getName() {
			return "item";
		}

		@Override
		public void initFromSerializableValue(Object resource, Object serializableValue, ResourceMap dependencies) {
		}

		@Override
		public Class<?> getPropertyType() {
			return componentType;
		}

		@Override
		public void writeValue(Json json, Object serializableValue) {
		}

		@Override
		public Object readValue(Json json, JsonValue propertyValue) {
			return null;
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
		public String getDescriptiveName() {
			return "";
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
		public void initFromDefaultValue(Object resource) {
		}

		@Override
		public Object getDefaultValue() {
			return null;
		}
	}
}
