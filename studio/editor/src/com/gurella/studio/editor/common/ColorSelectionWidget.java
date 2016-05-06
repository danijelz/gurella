package com.gurella.studio.editor.common;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.RGBAColorDescriptor;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorSelectionWidget extends Composite {
	private Text text;
	private Composite button;

	private Color color;

	private org.eclipse.swt.graphics.Color swtColor;
	private RGBA rgba;
	private RGBAColorDescriptor descriptor;

	private Consumer<Color> listener;

	public ColorSelectionWidget(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		text = UiUtils.createText(this);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		Point textSize = text.computeSize(60, 16, true);

		button = GurellaStudioPlugin.getToolkit().createComposite(this);
		layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = textSize.y;
		layoutData.heightHint = textSize.y;
		button.setLayoutData(layoutData);
		button.addPaintListener(e -> paintButton(e));

		UiUtils.paintBordersFor(this);
	}

	private void paintButton(PaintEvent e) {
		GC gc = e.gc;
		Point size = button.getSize();
		int width = size.x;
		int height = size.y;

		if (swtColor == null) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.setLineWidth(2);
			gc.drawLine(2, 2, width - 2, height - 2);
		} else {
			int halfWidth = width / 2;
			int halfHeight = height / 2;

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(2, 2, halfWidth - 2, halfHeight - 2);
			gc.drawRectangle(halfWidth + 2, halfHeight + 2, halfWidth - 2, halfHeight - 2);

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.drawRectangle(halfWidth + 2, 2, halfWidth - 2, halfHeight - 2);
			gc.drawRectangle(2, halfHeight + 2, halfWidth - 2, halfHeight - 2);

			gc.setForeground(swtColor);
			gc.drawRectangle(2, 2, width - 2, height - 2);
		}
	}

	public void setColor(Color color) {
		this.color = color;
		text.setText(color == null ? "" : color.toString());

		if (rgba == null) {

		}
	}

	public Color getColor() {
		return color;
	}

	public void setListener(Consumer<Color> listener) {
		this.listener = listener;
	}
}
