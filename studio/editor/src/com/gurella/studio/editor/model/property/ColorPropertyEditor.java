package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.editor.common.ColorSelectionWidget;

public class ColorPropertyEditor extends SimplePropertyEditor<Color> {
	private ColorSelectionWidget colorSelector;

	public ColorPropertyEditor(Composite parent, PropertyEditorContext<?, Color> context) {
		super(parent, context);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		colorSelector = new ColorSelectionWidget(body);

		Color color = getValue();
		if (color != null) {
			colorSelector.setColor(color);
		}

		colorSelector.setColorChangeListener(e -> setValue(colorSelector.getColor()));
	}
}
