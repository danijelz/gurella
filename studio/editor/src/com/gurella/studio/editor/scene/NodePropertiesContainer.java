package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class NodePropertiesContainer extends PropertiesContainer<SceneNode2> {
	private Text nameText;
	private Button enabledCheck;
	private Composite componentsPropertiesComposite;

	public NodePropertiesContainer(GurellaEditor editor, Composite parent, int style) {
		super(editor, parent, style);
	}

	@Override
	protected void init(FormToolkit toolkit, final SceneNode2 node) {
		toolkit.adapt(this);
		getBody().setLayout(new GridLayout(3, false));
		
		Label nameLabel = toolkit.createLabel(getBody(), "Name: ");
		nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		
		nameText = toolkit.createText(getBody(), node.getName());
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				node.setName(nameText.getText());
				editor.setDirty();
				postMessage(new NodeNameChangedMessage(node));
			}
		});
		
		enabledCheck = toolkit.createButton(getBody(), "Enabled", SWT.CHECK);
		enabledCheck.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
		enabledCheck.setSelection(node.isEnabled());
		enabledCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				node.setEnabled(enabledCheck.getSelection());
				editor.setDirty();
			}
		});
		
		componentsPropertiesComposite = toolkit.createComposite(getBody());
		componentsPropertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		componentsPropertiesComposite.setBackground(new Color(getDisplay(), 100, 0, 100));
	}

	@Override
	public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		// TODO Auto-generated method stub
		super.handleMessage(source, message, additionalData);
	}
}
