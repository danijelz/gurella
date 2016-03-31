package com.gurella.studio.editor.scene;

import org.eclipse.swt.layout.GridLayout;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class ComponentPropertiesContainer extends PropertiesContainer<SceneNodeComponent2> {
	private ModelPropertiesContainer<SceneNodeComponent2> propertiesContainer;

	public ComponentPropertiesContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);
		setText(target.getClass().getSimpleName());
		setExpandHorizontal(true);
		setMinWidth(200);
		getToolkit().adapt(this);
		getBody().setLayout(new GridLayout(3, false));
		propertiesContainer = new ModelPropertiesContainer<SceneNodeComponent2>(getGurellaEditor(), getBody(), target);
		layout(true, true);
	}
}
