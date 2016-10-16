package com.gurella.studio.editor.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.utils.UiUtils;

//TODO unused
public class TypeSelectionWidget extends Composite {
	public TypeSelectionWidget(Composite parent) {
		super(parent, SWT.NONE);
		
		UiUtils.paintBordersFor(this);
		UiUtils.adapt(this);
	}
}
