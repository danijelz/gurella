package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;

public class DefaultPropertyEditor<T> extends SimplePropertyEditor<T> {
	public DefaultPropertyEditor(Composite parent, PropertyEditorContext<T> context,
			ModelEditorContainer<?> propertiesContainer) {
		super(parent, context, propertiesContainer);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		Label label = GurellaStudioPlugin.getToolkit().createLabel(this, "Missing editor");
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void present(Object modelInstance) {
	}
}
