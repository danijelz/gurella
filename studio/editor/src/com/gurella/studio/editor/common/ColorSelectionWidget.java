package com.gurella.studio.editor.common;

import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorSelectionWidget extends Composite {
	private Text text;
	private Composite button;

	private Color color;

	private org.eclipse.swt.graphics.Color swtColor;
	private RGBA rgba;

	private Consumer<Color> colorChangeListener;
	private Color defaultColor;

	private boolean alphaEnabled;
	private ModifyListener modifyTextListener;

	public ColorSelectionWidget(Composite parent) {
		this(parent, null, null, true);
	}

	public ColorSelectionWidget(Composite parent, Color color, Color defaultColor, boolean alphaEnabled) {
		super(parent, SWT.NONE);

		this.defaultColor = defaultColor;
		this.alphaEnabled = alphaEnabled;

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		setLayout(layout);

		text = UiUtils.createText(this);
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 100;
		layoutData.heightHint = 14;
		text.setLayoutData(layoutData);

		Point textSize = text.computeSize(60, 14, true);

		button = UiUtils.createComposite(this, SWT.NONE);
		layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = textSize.y;
		layoutData.heightHint = textSize.y;
		button.setLayoutData(layoutData);
		button.addPaintListener(e -> paintButton(e));
		button.addListener(SWT.MouseUp, e -> openDialog());
		button.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		UiUtils.paintBordersFor(this);
		UiUtils.adapt(this);

		modifyTextListener = e -> modifyColor(text.getText());
		presentColor(color);

		text.addVerifyListener(e -> UiUtils.verifyHexRgba(e, text.getText()));
		text.addModifyListener(modifyTextListener);
	}

	private void paintButton(PaintEvent e) {
		GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		Rectangle clientArea = button.getClientArea();
		int width = clientArea.width;
		int height = clientArea.height;

		if (swtColor == null) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setLineWidth(2);
			int widthBorder = width - 2;
			int heightBorder = height - 2;
			gc.drawLine(widthBorder, 2, 2, heightBorder);
			int radius = Math.min(widthBorder, heightBorder);
			gc.drawOval(4, 3, radius - 6, radius - 4);
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

		gc.setAlpha(255);
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		gc.drawRectangle(0, 0, width - 1, height - 1);
	}

	private void modifyColor(String hex) {
		String temp = hex.startsWith("#") ? hex.substring(1) : hex;
		int length = temp.length();
		if (length == 0) {
			updateColor(defaultColor);
		} else if (length == 6 || length == 8) {
			updateColor(Color.valueOf(temp));
		} else if (length < 6) {
			char[] missing = new char[6 - length];
			Arrays.fill(missing, '0');
			updateColor(Color.valueOf(temp + new String(missing)));
		} else if (length < 8) {
			char[] missing = new char[8 - length];
			Arrays.fill(missing, 'f');
			updateColor(Color.valueOf(temp + new String(missing)));
		}
		fireColorChanged();
	}

	private void fireColorChanged() {
		if (colorChangeListener != null) {
			colorChangeListener.accept(color);
		}
	}

	public void setColor(Color color) {
		text.removeModifyListener(modifyTextListener);
		presentColor(color);
		text.addModifyListener(modifyTextListener);
	}

	private void presentColor(Color color) {
		updateColor(color);
		text.setText(color == null ? "" : color.toString());
	}

	private void updateColor(Color color) {
		if (Values.isEqual(color, this.color)) {
			return;
		}

		this.color = color;
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

	public void openDialog() {
		ColorSelectionDialog colorDialog = new ColorSelectionDialog(getShell(), alphaEnabled);
		colorDialog.setColor(swtColor == null ? new RGBA(255, 255, 255, 255) : swtColor.getRGBA());
		RGBA newColor = colorDialog.open(button.toDisplay(0, 0));
		if (newColor != null) {
			RGB rgb = newColor.rgb;
			setColor(new Color(rgb.red / 255f, rgb.green / 255f, rgb.blue / 255f, newColor.alpha / 255f));
			fireColorChanged();
			button.redraw();
		}
	}

	public void setColorChangeListener(Consumer<Color> colorChangeListener) {
		this.colorChangeListener = colorChangeListener;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}
}
