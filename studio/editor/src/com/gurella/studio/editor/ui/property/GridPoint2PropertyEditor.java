package com.gurella.studio.editor.ui.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class GridPoint2PropertyEditor extends SimplePropertyEditor<GridPoint2> {
	public GridPoint2PropertyEditor(Composite parent, PropertyEditorContext<?, GridPoint2> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, true);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 4;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New instance", () -> newValue(new GridPoint2()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		GridPoint2 value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(content, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			MetaType<GridPoint2> metaType = MetaTypes.getMetaType(GridPoint2.class);
			createEditorField(metaType, value, "x");
			createEditorField(metaType, value, "y");
			UiUtils.paintBordersFor(content);
		}
	}

	private void createEditorField(final MetaType<GridPoint2> metaType, GridPoint2 value, String propertyName) {
		Property<Integer> childProperty = metaType.getProperty(propertyName);
		Text text = UiUtils.createIntegerWidget(content);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 50;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);
		text.setToolTipText(propertyName);
		text.setText(childProperty.getValue(value).toString());
		text.addModifyListener(e -> valueChanged(childProperty, text.getText()));
	}

	private void valueChanged(Property<Integer> childProperty, String txtValue) {
		GridPoint2 value = getValue();
		GridPoint2 newValue = new GridPoint2(value);
		childProperty.setValue(newValue, Values.isBlank(txtValue) ? Integer.valueOf(0) : Integer.valueOf(txtValue));
		setValue(newValue);
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
	}

	private void newValue(GridPoint2 value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(GridPoint2 value) {
		rebuildUi();
	}
}
