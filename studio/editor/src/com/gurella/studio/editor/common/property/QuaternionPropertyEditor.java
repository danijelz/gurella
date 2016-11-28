package com.gurella.studio.editor.common.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.Quaternion;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class QuaternionPropertyEditor extends SimplePropertyEditor<Quaternion> {
	public QuaternionPropertyEditor(Composite parent, PropertyEditorContext<?, Quaternion> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newValue(new Quaternion()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Quaternion value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(content, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			MetaType<Quaternion> metaType = MetaTypes.getMetaType(Quaternion.class);
			createEditorField(metaType, value, "x");
			createEditorField(metaType, value, "y");
			createEditorField(metaType, value, "z");
			createEditorField(metaType, value, "w");
			UiUtils.paintBordersFor(content);
		}
	}

	private void createEditorField(final MetaType<Quaternion> metaType, Quaternion value, String propertyName) {
		Property<Float> childProperty = metaType.getProperty(propertyName);
		Text text = UiUtils.createFloatWidget(content);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 40;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);
		text.setText(childProperty.getValue(value).toString());
		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Float> childProperty, String txtValue) {
		Quaternion value = getValue();
		Quaternion newValue = new Quaternion(value);
		childProperty.setValue(newValue, Values.isBlank(txtValue) ? Float.valueOf(0) : Float.valueOf(txtValue));
		setValue(newValue);
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
	}

	private void newValue(Quaternion value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Quaternion value) {
		rebuildUi();
	}
}
