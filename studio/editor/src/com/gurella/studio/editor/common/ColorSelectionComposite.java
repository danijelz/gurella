package com.gurella.studio.editor.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorSelectionComposite extends Composite {
	private Composite image;

	private Text r;
	private Slider rSlider;

	private Text g;
	private Slider gSlider;

	private Text b;
	private Slider bSlider;

	private Text a;
	private Slider aSlider;

	private Text color;

	private Color current;

	public ColorSelectionComposite(Composite parent, int style) {
		super(parent, style);

		setLayout(new GridLayout(3, false));

		image = UiUtils.createComposite(this, SWT.BORDER);
		GridData layoutData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1);
		layoutData.heightHint = 100;
		layoutData.widthHint = 100;
		image.setLayoutData(layoutData);
		current = new Color(getDisplay(), new RGBA(255, 255, 255, 255));
		addDisposeListener(e -> current.dispose());
		image.setBackground(current);

		Label label = UiUtils.createLabel(this, "R:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		rSlider = new Slider(this, SWT.HORIZONTAL);
		rSlider.setMinimum(0);
		rSlider.setMaximum(255);
		rSlider.addListener(SWT.Selection, e -> sliderValueChanged(rSlider, r));
		rSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		r = UiUtils.createIntegerWidget(this);
		r.addModifyListener(e -> textValueChanged(rSlider, r));
		r.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		label = UiUtils.createLabel(this, "G:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		gSlider = new Slider(this, SWT.HORIZONTAL);
		gSlider.setMinimum(0);
		gSlider.setMaximum(255);
		gSlider.addListener(SWT.Selection, e -> sliderValueChanged(gSlider, g));
		gSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		g = UiUtils.createIntegerWidget(this);
		g.addModifyListener(e -> textValueChanged(gSlider, g));
		g.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		label = UiUtils.createLabel(this, "R:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		bSlider = new Slider(this, SWT.HORIZONTAL);
		bSlider.setMinimum(0);
		bSlider.setMaximum(255);
		bSlider.addListener(SWT.Selection, e -> sliderValueChanged(bSlider, b));
		bSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		b = UiUtils.createIntegerWidget(this);
		b.addModifyListener(e -> textValueChanged(bSlider, b));
		b.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		label = UiUtils.createLabel(this, "R:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		aSlider = new Slider(this, SWT.HORIZONTAL);
		aSlider.setMinimum(0);
		aSlider.setMaximum(255);
		aSlider.addListener(SWT.Selection, e -> sliderValueChanged(aSlider, a));
		aSlider.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
		a = UiUtils.createIntegerWidget(this);
		a.addModifyListener(e -> textValueChanged(aSlider, a));
		a.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		color = UiUtils.createText(this);
		color.setEditable(false);
		layoutData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1);
		layoutData.widthHint = 60;
		color.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
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
		current.dispose();
		current = new Color(getDisplay(), new RGBA(getInt(r), getInt(g), getInt(b), getInt(a)));
	}

	private static int getInt(Text text) {
		String val = text.getText();
		return Values.isBlank(val) ? 0 : Integer.parseInt(val);
	}
}
