package com.gurella.studio.editor.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorSelectionDialog extends Dialog {
	private Composite image;

	private Text r;
	private Slider rSlider;

	private Text g;
	private Slider gSlider;

	private Text b;
	private Slider bSlider;

	private Text a;
	private Slider aSlider;

	private Text colorText;

	private Color color;
	private RGBA rgba;

	public ColorSelectionDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("Color");
	}

	public RGBA open() {
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return rgba;
	}

	public void setColor(RGBA rgba) {
		if (color != null) {
			color.dispose();
		}
		color = new Color(getDisplay(), rgba);
	}

	private void createContents(Shell shell) {
		if (color == null) {
			color = new Color(getDisplay(), new RGBA(255, 255, 255, 255));
		}

		shell.addDisposeListener(e -> color.dispose());

		shell.setLayout(new GridLayout(3, false));

		image = UiUtils.createComposite(shell, SWT.BORDER);
		GridData layoutData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1);
		layoutData.heightHint = 100;
		layoutData.widthHint = 100;
		image.setLayoutData(layoutData);
		image.addPaintListener(e -> paintImage(e));

		Label label = UiUtils.createLabel(shell, "R:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		rSlider = new Slider(shell, SWT.HORIZONTAL);
		rSlider.setMinimum(0);
		rSlider.setMaximum(255);
		rSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		r = UiUtils.createIntegerWidget(shell);
		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 20;
		r.setLayoutData(layoutData);

		label = UiUtils.createLabel(shell, "G:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		gSlider = new Slider(shell, SWT.HORIZONTAL);
		gSlider.setMinimum(0);
		gSlider.setMaximum(255);
		gSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		g = UiUtils.createIntegerWidget(shell);
		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 20;
		g.setLayoutData(layoutData);

		label = UiUtils.createLabel(shell, "B:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		bSlider = new Slider(shell, SWT.HORIZONTAL);
		bSlider.setMinimum(0);
		bSlider.setMaximum(255);
		bSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		b = UiUtils.createIntegerWidget(shell);
		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 20;
		b.setLayoutData(layoutData);

		label = UiUtils.createLabel(shell, "A:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		aSlider = new Slider(shell, SWT.HORIZONTAL);
		aSlider.setMinimum(0);
		aSlider.setMaximum(255);
		aSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		a = UiUtils.createIntegerWidget(shell);
		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		layoutData.widthHint = 20;
		a.setLayoutData(layoutData);

		colorText = UiUtils.createText(shell);
		colorText.setEditable(false);
		layoutData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1);
		layoutData.widthHint = 60;
		colorText.setLayoutData(layoutData);
		GurellaStudioPlugin.getToolkit().adapt(shell);
		UiUtils.paintBordersFor(shell);

		if (color != null) {
			r.setText(Integer.toString(color.getRed()));
			rSlider.setSelection(color.getRed());

			g.setText(Integer.toString(color.getGreen()));
			gSlider.setSelection(color.getGreen());

			b.setText(Integer.toString(color.getBlue()));
			bSlider.setSelection(color.getBlue());

			a.setText(Integer.toString(color.getAlpha()));
			aSlider.setSelection(color.getAlpha());
			
			colorText.setText(toColorString());
		}

		image.redraw();

		rSlider.addListener(SWT.Selection, e -> sliderValueChanged(rSlider, r));
		r.addModifyListener(e -> textValueChanged(rSlider, r));

		gSlider.addListener(SWT.Selection, e -> sliderValueChanged(gSlider, g));
		g.addModifyListener(e -> textValueChanged(gSlider, g));

		bSlider.addListener(SWT.Selection, e -> sliderValueChanged(bSlider, b));
		b.addModifyListener(e -> textValueChanged(bSlider, b));

		aSlider.addListener(SWT.Selection, e -> sliderValueChanged(aSlider, a));
		a.addModifyListener(e -> textValueChanged(aSlider, a));

		Composite buttonsComposite = UiUtils.createComposite(shell);
		buttonsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1));
		buttonsComposite.setLayout(new GridLayout(2, true));

		Button ok = new Button(buttonsComposite, SWT.PUSH);
		ok.setText("OK");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(layoutData);
		ok.addListener(SWT.Selection, e -> confirm(shell));

		Button cancel = new Button(buttonsComposite, SWT.PUSH);
		cancel.setText("Cancel");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(layoutData);
		cancel.addListener(SWT.Selection, e -> shell.close());

		shell.setDefaultButton(ok);
	}

	private void confirm(Shell shell) {
		rgba = color.getRGBA();
		shell.close();
	}

	private void paintImage(PaintEvent e) {
		GC gc = e.gc;
		Rectangle clientArea = image.getClientArea();
		int width = clientArea.width;
		int height = clientArea.height;

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

		gc.setForeground(color);
		gc.setBackground(color);
		gc.setAlpha(color.getAlpha());
		gc.fillRectangle(0, 0, width, height);
	}

	private Display getDisplay() {
		return getParent().getDisplay();
	}

	private void sliderValueChanged(Slider slider, Text text) {
		text.setText(Integer.toString(slider.getSelection()));
		updateColor();
	}

	private void textValueChanged(Slider slider, Text text) {
		slider.setSelection(getInt(text));
		updateColor();
	}

	private void updateColor() {
		color.dispose();
		color = new Color(getDisplay(), new RGBA(getInt(r), getInt(g), getInt(b), getInt(a)));
		colorText.setText(toColorString());
		image.redraw();
	}

	private static int getInt(Text text) {
		String val = text.getText();
		return Values.isBlank(val) ? 0 : Integer.parseInt(val);
	}

	private String toColorString() {
		String value = Integer.toHexString(((color.getRed()) << 24) | ((color.getGreen()) << 16)
				| ((color.getBlue()) << 8) | ((color.getAlpha())));
		while (value.length() < 8) {
			value = "0" + value;
		}
		return value;
	}
}
