package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.model.SimplePropertyEditor;

public class ShortPropertyEditor extends SimplePropertyEditor<Short> {
	private Text text;

	public ShortPropertyEditor(ModelPropertiesContainer<?> parent, Property<Short> property) {
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
			public void modifyText(ModifyEvent event) {
				property.setValue(getModelInstance(), Short.valueOf(text.getText()));
				setDirty();
			}
		});
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				try {
					final String oldS = text.getText();
					String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
					if (newS.length() > 0) {
						Short.parseShort(newS);
					}
				} catch (Exception e2) {
					e.doit = false;
				}
			}
		});
	}

	@Override
	protected void present(Object modelInstance) {
		Short value = property.getValue(modelInstance);
		if (value != null) {
			text.setText(value.toString());
		}
	}
}
