package com.gurella.studio;

import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

//TODO unused
public class AddProjectDialog extends VisDialog {
	VisTextField nameField = new VisTextField();
	VisTextField pathField = new VisTextField();
	VisTextButton selectPathButton = new VisTextButton("Directory");
	
	public AddProjectDialog() {
		super("New project");
	}
}
