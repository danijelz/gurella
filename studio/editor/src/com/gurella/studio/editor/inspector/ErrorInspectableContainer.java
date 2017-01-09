package com.gurella.studio.editor.inspector;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.studio.GurellaStudioPlugin;

public class ErrorInspectableContainer extends InspectableContainer<Throwable> {
	public ErrorInspectableContainer(InspectorView parent, Throwable throwable) {
		super(parent, throwable);
		setText("Exception");
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());

		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));

		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY;
		Text text = toolkit.createText(getBody(), writer.toString(), style);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
}
