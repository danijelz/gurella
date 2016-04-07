package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class StringPropertyEditor extends SimplePropertyEditor<String> {
	private Text text;

	public StringPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer,
			Property<String> property, Object modelInstance) {
		super(parent, propertiesContainer, property, modelInstance);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		text = GurellaStudioPlugin.getToolkit().createText(this, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				property.setValue(getModelInstance(), text.getText());
				setDirty();
			}
		});
	}

	@Override
	public void present(Object modelInstance) {
		String value = property.getValue(modelInstance);
		if (value != null) {
			text.setText(value);
		}
	}
}
