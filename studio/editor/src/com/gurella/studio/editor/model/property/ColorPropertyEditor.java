package com.gurella.studio.editor.model.property;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.editor.GurellaStudioPlugin;

public class ColorPropertyEditor extends SimplePropertyEditor<Color> {
	private ColorSelector colorSelector;
	private Spinner alphaSpinner;

	public ColorPropertyEditor(Composite parent, PropertyEditorContext<?, Color> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		colorSelector = new ColorSelector(body);
		alphaSpinner = new Spinner(body, SWT.NONE);
		GurellaStudioPlugin.getToolkit().adapt(body);
		alphaSpinner.setMinimum(0);
		alphaSpinner.setMaximum(255);
		alphaSpinner.setIncrement(1);
		alphaSpinner.setPageIncrement(1);

		Color color = getValue();
		if (color != null) {
			colorSelector.setColorValue(new RGB((int) color.r * 255, (int) color.g * 255, (int) color.b * 255));
			alphaSpinner.setSelection((int) color.a * 255);
		}

		colorSelector.addListener(e -> valueChanged());
		alphaSpinner.addModifyListener((e) -> valueChanged());
	}

	private void valueChanged() {
		Color color = getValue();
		Color oldValue = new Color(color);

		RGB rgb = colorSelector.getColorValue();
		color.set(rgb.red / 255f, rgb.green / 255f, rgb.blue / 255f, alphaSpinner.getSelection() / 255f);
		context.propertyValueChanged(oldValue, color);
	}
}
