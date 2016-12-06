package com.gurella.studio.editor.inspector.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.ui.bean.BeanEditor;
import com.gurella.studio.editor.ui.bean.BeanEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.ui.bean.BeanEditorFactory;
import com.gurella.studio.editor.utils.SceneChangedEvent;

public class ComponentInspectableContainer extends InspectableContainer<SceneNodeComponent>
		implements EditorSceneActivityListener {
	private BeanEditor<SceneNodeComponent> editor;

	public ComponentInspectableContainer(InspectorView parent, SceneNodeComponent target) {
		super(parent, target);

		int editorId = editorContext.editorId;
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);

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

	@Override
	public void nodeAdded(Scene scene, SceneNode parentNode, SceneNode node) {
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode parentNode, SceneNode node) {
	}

	@Override
	public void nodeIndexChanged(SceneNode node, int newIndex) {
	}

	@Override
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
		if (component == target) {
			dispose();
		}
	}

	@Override
	public void componentIndexChanged(SceneNodeComponent component, int newIndex) {
	}
}
