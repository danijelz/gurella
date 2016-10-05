package com.gurella.studio.editor.property;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class GridPoint2PropertyEditor extends SimplePropertyEditor<GridPoint2> {
	public GridPoint2PropertyEditor(Composite parent, PropertyEditorContext<?, GridPoint2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newInstance());
			if (context.isNullable()) {
				addMenuItem("Set null", () -> setNull());
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		GridPoint2 value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Model<GridPoint2> model = Models.getModel(GridPoint2.class);
			createEditorField(model, "x");
			createEditorField(model, "y");
		}

		body.layout();
	}

	private void createEditorField(final Model<GridPoint2> model, String propertyName) {
		Property<Integer> childProperty = model.getProperty(propertyName);

		Text text = UiUtils.createIntegerWidget(body);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);

		GridPoint2 value = getValue();
		if (value != null) {
			text.setText(childProperty.getValue(value).toString());
		}

		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Integer> childProperty, String txtValue) {
		GridPoint2 value = getValue();
		GridPoint2 oldValue = new GridPoint2(value);
		childProperty.setValue(value, Values.isBlank(txtValue) ? Integer.valueOf(0) : Integer.valueOf(txtValue));
		context.propertyValueChanged(oldValue, value);
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private void newInstance() {
		setValue(new GridPoint2());
		rebuildUi();
	}
}
