package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.model.SimplePropertyEditor;

public class StringPropertyEditor extends SimplePropertyEditor<String> {
	private Text text;

	public StringPropertyEditor(ModelPropertiesContainer<?> parent, Property<String> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		text = getToolkit().createText(this, "", SWT.BORDER);
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
	protected void present(Object modelInstance) {
		text.setText(property.getValue(modelInstance));
	}
}
