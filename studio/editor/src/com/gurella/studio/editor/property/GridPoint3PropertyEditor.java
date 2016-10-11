package com.gurella.studio.editor.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.GridPoint3;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class GridPoint3PropertyEditor extends SimplePropertyEditor<GridPoint3> {
	public GridPoint3PropertyEditor(Composite parent, PropertyEditorContext<?, GridPoint3> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New Vector3", () -> newValue(new GridPoint3()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		GridPoint3 value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<GridPoint3> model = Models.getModel(GridPoint3.class);
			createEditorField(model, value, "x");
			createEditorField(model, value, "y");
			createEditorField(model, value, "z");
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void createEditorField(final Model<GridPoint3> model, GridPoint3 value, String propertyName) {
		Property<Integer> childProperty = model.getProperty(propertyName);
		Text text = UiUtils.createFloatWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 50;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);
		text.setText(childProperty.getValue(value).toString());
		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Integer> childProperty, String txtValue) {
		GridPoint3 value = getValue();
		GridPoint3 newValue = new GridPoint3(value);
		childProperty.setValue(newValue, Values.isBlank(txtValue) ? Integer.valueOf(0) : Integer.valueOf(txtValue));
		setValue(newValue);
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(body);
		buildUi();
	}

	private void newValue(GridPoint3 value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(GridPoint3 value) {
		rebuildUi();
	}
}
