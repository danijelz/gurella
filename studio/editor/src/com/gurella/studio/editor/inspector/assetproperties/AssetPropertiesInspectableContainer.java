package com.gurella.studio.editor.inspector.assetproperties;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.asset.loader.AssetProperties;
import com.gurella.engine.event.Signal1;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.ui.bean.BeanEditor;
import com.gurella.studio.editor.ui.bean.BeanEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.ui.bean.BeanEditorFactory;

public class AssetPropertiesInspectableContainer extends InspectableContainer<IFile> {
	private AssetProperties properties;
	private BeanEditor<AssetProperties> editor;

	public AssetPropertiesInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);

		Composite head = getForm().getHead();
		head.setFont(GurellaStudioPlugin.getFont(head, SWT.BOLD));
		head.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		setText(target.getName());

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());

		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));

		addDisposeListener(e -> editorContext.unload(properties));
		properties = editorContext.load(target);
		editor = BeanEditorFactory.createEditor(body, editorContext.editorId, properties);
		//TODO add methods for listeners -> editor.addPropertiesListener()
		Signal1<PropertyValueChangedEvent> signal = editor.getContext().propertiesSignal;
		signal.addListener(e -> editorContext.save(properties));
		editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		layout(true, true);
	}
}
