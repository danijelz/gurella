package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class ComponentPropertiesContainer extends PropertiesContainer<SceneNodeComponent2> {
	private ModelPropertiesContainer<SceneNodeComponent2> propertiesContainer;

	public ComponentPropertiesContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);
		setText(Models.getModel(target).getName());
		FormToolkit toolkit = getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(3, false));
		propertiesContainer = new ModelPropertiesContainer<SceneNodeComponent2>(getGurellaEditor(), getBody(), target);
		propertiesContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		layout(true, true);
	}
}
