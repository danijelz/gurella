package com.gurella.studio.editor.inspector.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.common.bean.BeanEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.common.bean.BeanEditorFactory;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class ComponentInspectableContainer extends InspectableContainer<SceneNodeComponent> {
	private BeanEditor<SceneNodeComponent> editor;

	public ComponentInspectableContainer(InspectorView parent, SceneNodeComponent target) {
		super(parent, target);

		Composite head = getForm().getHead();
		head.setFont(GurellaStudioPlugin.getFont(head, SWT.BOLD));
		head.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		setText(MetaTypes.getMetaType(target).getName());

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());

		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));

		editor = BeanEditorFactory.createEditor(body, editorContext, target);
		Signal1<PropertyValueChangedEvent> signal = editor.getContext().propertiesSignal;
		signal.addListener(e -> EventService.post(editorContext.editorId, SceneChangedEvent.instance));
		editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		layout(true, true);
	}
}
