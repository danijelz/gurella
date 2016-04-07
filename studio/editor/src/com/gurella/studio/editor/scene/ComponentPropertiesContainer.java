package com.gurella.studio.editor.scene;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.model.property.ModelEditorContext;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class ComponentPropertiesContainer extends PropertiesContainer<SceneNodeComponent2> {
	private ModelEditorContainer<SceneNodeComponent2> propertiesContainer;

	public ComponentPropertiesContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);
		Composite head = getForm().getHead();
		head.setFont(GurellaStudioPlugin.createFont(FontDescriptor.createFrom(head.getFont()).setStyle(SWT.BOLD)));
		setText(Models.getModel(target).getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(3, false));
		ModelEditorContext<SceneNodeComponent2> context = new ModelEditorContext<SceneNodeComponent2>(target);
		context.signal.addListener((event) -> setDirty());
		propertiesContainer = new ModelEditorContainer<SceneNodeComponent2>(getBody(), context);
		propertiesContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		layout(true, true);
	}
}
