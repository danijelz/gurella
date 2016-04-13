package com.gurella.studio.editor.inspector;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
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

		Composite body = getBody();
		body.setLayout(new GridLayout(1, false));

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		Text text = toolkit.createText(getBody(), writer.toString(),
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		text.setLayoutData(layoutData);
		//parent.addListener(SWT.Resize, e -> setTextSize(text, layoutData));

		/*ErrorComposite errorComposite = new ErrorComposite(body, throwable);
		errorComposite.setLayoutData(layoutData);
		toolkit.adapt(errorComposite);*/
	}

	private void setTextSize(Text text, GridData layoutData) {
		/*layoutData.widthHint = getBody().getSize().x;
		layoutData.heightHint = getBody().getSize().y;
		getBody().setSize(getBody().getSize().x - 5, getBody().getSize().y - 5);*/
		//reflow(true);
	}
}
