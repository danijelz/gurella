package com.gurella.studio.editor.model;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.GurellaStudioPlugin;

//TODO unused
public class ExpandableGroup extends Composite {
	public ExpandableGroup(Composite parent, int style) {
		super(parent, style);
		setBackground(GurellaStudioPlugin.getColor(221, 234, 255));
	}
}
