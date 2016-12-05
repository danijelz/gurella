package com.gurella.studio.editor.ui.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.editor.ui.ColorSelectionWidget;

public class ColorPropertyEditor extends SimplePropertyEditor<Color> {
	private ColorSelectionWidget colorSelector;

	public ColorPropertyEditor(Composite parent, PropertyEditorContext<?, Color> context) {
		super(parent, context);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		Color color = getValue();
		Color defaultColor = context.isNullable() ? null : new Color(Color.WHITE);
		colorSelector = new ColorSelectionWidget(content, color, defaultColor, true);
		colorSelector.setColorChangeListener(e -> setValue(colorSelector.getColor()));
	}

	@Override
	protected void updateValue(Color value) {
		colorSelector.setColor(value);
	}
}
