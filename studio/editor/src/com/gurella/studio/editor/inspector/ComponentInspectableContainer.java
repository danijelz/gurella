package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.model.MetaModelEditor;
import com.gurella.studio.editor.model.ModelEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.model.ModelEditorFactory;
import com.gurella.studio.editor.scene.event.SceneChangedEvent;

public class ComponentInspectableContainer extends InspectableContainer<SceneNodeComponent2> {
	private MetaModelEditor<SceneNodeComponent2> modelEditor;

	public ComponentInspectableContainer(InspectorView parent, SceneNodeComponent2 target) {
		super(parent, target);

		Composite head = getForm().getHead();
		head.setFont(GurellaStudioPlugin.getFont(head, SWT.BOLD));
		head.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		setText(Models.getModel(target).getName());

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());

		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));

		SceneEditorContext sceneContext = getSceneEditorContext();
		modelEditor = ModelEditorFactory.createEditor(body, sceneContext, target);
		Signal1<PropertyValueChangedEvent> signal = modelEditor.getContext().propertyChangedSignal;
		signal.addListener(e -> EventService.post(getSceneEditorContext().editorId, SceneChangedEvent.instance));
		modelEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		layout(true, true);
	}
}
