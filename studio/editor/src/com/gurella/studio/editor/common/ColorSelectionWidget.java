package com.gurella.studio.editor.common;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.graphics.Color;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorSelectionWidget extends Composite {
	private Text text;
	private Composite button;

	private Color color;

	private org.eclipse.swt.graphics.Color swtColor;
	private RGBA rgba;

	private Consumer<Color> colorChangeListener;

	private Transform transform;

	public ColorSelectionWidget(Composite parent) {
		super(parent, SWT.NONE);

		transform = new Transform(getDisplay());
		addListener(SWT.Dispose, (e) -> transform.dispose());

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		setLayout(layout);

		text = UiUtils.createText(this);
		text.setEditable(false);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		Point textSize = text.computeSize(60, 16, true);

		button = GurellaStudioPlugin.getToolkit().createComposite(this, SWT.BORDER);
		layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = textSize.y;
		layoutData.heightHint = textSize.y;
		button.setLayoutData(layoutData);
		button.addPaintListener(e -> paintButton(e));
		button.addListener(SWT.MouseUp, e -> openDialog());

		UiUtils.paintBordersFor(this);
	}

	private void paintButton(PaintEvent e) {
		GC gc = e.gc;
		Rectangle clientArea = button.getClientArea();
		int width = clientArea.width;
		int height = clientArea.height;

		if (swtColor == null) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.setLineWidth(2);
			gc.drawLine(2, 2, width - 2, height - 2);
			gc.drawLine(width - 2, 2, 2, height - 2);
		} else {
			int halfWidth = width / 2;
			int halfHeight = height / 2;

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.fillRectangle(0, 0, halfWidth, halfHeight);
			gc.fillRectangle(halfWidth, halfHeight, halfWidth, halfHeight);

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillRectangle(halfWidth, 0, halfWidth, halfHeight);
			gc.fillRectangle(0, halfHeight, halfWidth, halfHeight);

			gc.setForeground(swtColor);
			gc.setBackground(swtColor);
			gc.setAlpha(rgba.alpha);
			gc.fillRectangle(0, 0, width, height);
		}
	}

	public void setColor(Color color) {
		this.color = color;
		text.setText(color == null ? "" : color.toString());

		if (color == null && swtColor != null) {
			swtColor.dispose();
			swtColor = null;
			rgba = null;
			button.redraw();
		} else if (swtColor == null || swtColor.getRGBA().equals(rgba)) {
			if (swtColor != null) {
				swtColor.dispose();
			}
			rgba = toRgba();
			swtColor = new org.eclipse.swt.graphics.Color(getDisplay(), rgba);
			button.redraw();
		}
	}

	private RGBA toRgba() {
		return new RGBA((int) (color.r * 255), (int) (color.g * 255), (int) (color.b * 255), (int) (color.a * 255));
	}

	public Color getColor() {
		return color;
	}

	public void setColorChangeListener(Consumer<Color> colorChangeListener) {
		this.colorChangeListener = colorChangeListener;
	}

	public void openDialog() {
		ColorSelectionDialog colorDialog = new ColorSelectionDialog(getShell());
		colorDialog.setColor(swtColor == null ? new RGBA(255, 255, 255, 255) : swtColor.getRGBA());
		RGBA newColor = colorDialog.open(button.toDisplay(0, 0));
		if (newColor != null) {
			RGB rgb = newColor.rgb;
			setColor(new Color(rgb.red / 255f, rgb.green / 255f, rgb.blue / 255f, newColor.alpha / 255f));
			colorChangeListener.accept(color);
			button.redraw();
		}
	}
}
