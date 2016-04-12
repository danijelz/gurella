package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.common.ErrorComposite;

public class ErrorInspectableContainer extends InspectableContainer<Throwable> {
	public ErrorInspectableContainer(InspectorView parent, Throwable throwable) {
		super(parent, throwable);
		setText("Exception");
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		
		ErrorComposite errorComposite = new ErrorComposite(getBody(), throwable);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		toolkit.adapt(errorComposite);
	}
}
