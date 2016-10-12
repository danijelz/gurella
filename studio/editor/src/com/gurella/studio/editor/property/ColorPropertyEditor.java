package com.gurella.studio.editor.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.base.model.ModelDefaults;
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
		content.setLayout(layout);

		Color color = getValue();
		Color defaultColor = context.isNullable() ? null : getDefaultColor();
		colorSelector = new ColorSelectionWidget(content, color, defaultColor, true);
		colorSelector.setColorChangeListener(e -> setValue(colorSelector.getColor()));
	}

	private Color getDefaultColor() {
		Color defaultColor = ModelDefaults.getDefault(context.model.getType(), context.property);
		return defaultColor == null ? new Color(Color.WHITE) : new Color(defaultColor);
	}

	@Override
	protected void updateValue(Color value) {
		colorSelector.setColor(value);
	}
}
