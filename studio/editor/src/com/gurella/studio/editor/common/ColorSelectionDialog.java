package com.gurella.studio.editor.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

public class ColorSelectionDialog extends Composite {
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
	
	public ColorSelectionDialog(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(3, false));
		
		image = new Composite(this, SWT.BORDER);
		GridData layoutData = new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 3, 1);
		layoutData.heightHint = 100;
		layoutData.widthHint = 100;
		image.setLayoutData(layoutData);
		
		
		
		// TODO Auto-generated constructor stub
	}

}
