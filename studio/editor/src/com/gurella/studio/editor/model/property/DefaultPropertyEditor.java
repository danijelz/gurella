package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class DefaultPropertyEditor<P> extends SimplePropertyEditor<P> {
	public DefaultPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		Label label = GurellaStudioPlugin.getToolkit().createLabel(body, "Missing editor");
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}
}
