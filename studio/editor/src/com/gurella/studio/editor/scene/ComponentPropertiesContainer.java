package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class ComponentPropertiesContainer extends PropertiesContainer<SceneNodeComponent2> {
	private ModelPropertiesContainer<SceneNodeComponent2> propertiesContainer;

	public ComponentPropertiesContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);
		setText(Models.getModel(target).getName());
		setExpandHorizontal(true);
		setMinWidth(200);
		getToolkit().adapt(this);
		getBody().setLayout(new GridLayout(3, false));
		Label separator = getToolkit().createSeparator(getBody(), SWT.NONE | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		propertiesContainer = new ModelPropertiesContainer<SceneNodeComponent2>(getGurellaEditor(), getBody(), target);
		propertiesContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		layout(true, true);
	}
}
