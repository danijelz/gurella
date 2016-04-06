package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.model.SimplePropertyEditor;

public class Vector3PropertyEditor extends SimplePropertyEditor<Vector3> {
	private Text xField;
	private Text yField;
	private Text zField;
	private Model<Vector3> vector3Model;

	public Vector3PropertyEditor(ModelPropertiesContainer<?> parent, Property<Vector3> property) {
		super(parent, property);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(6, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		vector3Model = Models.getModel(Vector3.class);
		xField = createField(vector3Model.getProperty("x"));
		yField = createField(vector3Model.getProperty("y"));
		zField = createField(vector3Model.getProperty("z"));
	}

	private Text createField(final Property<Float> attributeProperty) {
		Label label = getToolkit().createLabel(this, attributeProperty.getDescriptiveName());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		Text text = getToolkit().createText(this, "", SWT.BORDER);
		text.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				attributeProperty.setValue(getValue(), Float.valueOf(text.getText()));
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
						Float.parseFloat(newS);
					}
				} catch (Exception e2) {
					e.doit = false;
				}
			}
		});
		return text;
	}

	@Override
	protected void present(Object modelInstance) {
		Vector3 vector = getValue();
		if (vector == null) {
			return;
		}

		xField.setText(Float.toString(vector.x));
		yField.setText(Float.toString(vector.y));
		zField.setText(Float.toString(vector.z));
	}
}
