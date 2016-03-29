package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class NodePropertiesContainer extends PropertiesContainer<SceneNode2> {
	private Text nameText;
	private Button enabledCheck;
	private Composite componentsPropertiesComposite;

	public NodePropertiesContainer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void init(FormToolkit toolkit, SceneNode2 node) {
		toolkit.adapt(this);
		getBody().setLayout(new GridLayout(3, false));
		toolkit.createLabel(getBody(), "Name: ");
		nameText = toolkit.createText(getBody(), node.getName());
		enabledCheck = toolkit.createButton(getBody(), "Enabled", SWT.CHECK);
		enabledCheck.setSelection(node.isEnabled());
		componentsPropertiesComposite = toolkit.createComposite(getBody());
		componentsPropertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	}

}
