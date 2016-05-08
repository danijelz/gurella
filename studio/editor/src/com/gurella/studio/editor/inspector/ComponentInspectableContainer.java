package com.gurella.studio.editor.inspector;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneChangedMessage;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.model.ModelEditorContext;

public class ComponentInspectableContainer extends InspectableContainer<SceneNodeComponent2> {
	private ModelEditorContainer<SceneNodeComponent2> propertiesContainer;

	public ComponentInspectableContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);
		Composite head = getForm().getHead();
		head.setFont(GurellaStudioPlugin.createFont(FontDescriptor.createFrom(head.getFont()).setStyle(SWT.BOLD)));
		setText(Models.getModel(target).getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(3, false));
		ModelEditorContext<SceneNodeComponent2> context = new ModelEditorContext<>(getEditorContext(), target);
		context.signal.addListener((event) -> postMessage(SceneChangedMessage.instance));
		propertiesContainer = new ModelEditorContainer<SceneNodeComponent2>(getBody(), context);
		propertiesContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		layout(true, true);
	}
}
