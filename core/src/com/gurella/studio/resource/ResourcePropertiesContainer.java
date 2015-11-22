package com.gurella.studio.resource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModel;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.studio.propertyeditor.PropertyEditorFactory;
import com.gurella.studio.propertyeditor.ResourcePropertyEditor;
import com.kotcrab.vis.ui.widget.VisTable;

public class ResourcePropertiesContainer extends VisTable {
	private Array<ResourcePropertyEditor<?>> editors = new Array<ResourcePropertyEditor<?>>();

	public ResourcePropertiesContainer() {
		setBackground("border");
	}

	public ResourcePropertiesContainer(ModelResourceFactory<?> factory) {
		ResourceModel<?> model = factory.getModel();
		for (ResourceModelProperty property : model.getProperties()) {
			ResourcePropertyEditor<?> editor = PropertyEditorFactory.createEditor(factory, property);
			editors.add(editor);
			Array<Actor> components = editor.getUiComponents();

			if (components.size == 1) {
				add(components.get(0)).right().top().colspan(2).expandX().fillX().pad(2);
			} else if (components.size == 2) {
				add(components.get(0)).right().top().pad(2);
				add(components.get(1)).left().top().expandX().fillX().pad(2);
			} else {
				VisTable eitorContent = new VisTable();
				add(eitorContent).right().top().colspan(2).expandX().fillX().pad(2);
			}
			row();
		}
	}

	public void save() {
		for (int i = 0; i < editors.size; i++) {
			ResourcePropertyEditor<?> editor = editors.get(i);
			editor.save();
		}
	}
}
