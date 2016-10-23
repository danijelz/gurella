package com.gurella.studio.editor.common.bean.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.test.TestEditorComponent;
import com.gurella.studio.editor.common.bean.BeanEditorContext;
import com.gurella.studio.editor.common.bean.CustomizableBeanEditor;

public class TestCustomizableBeanEditor extends CustomizableBeanEditor<TestEditorComponent> {
	public TestCustomizableBeanEditor(Composite parent, BeanEditorContext<TestEditorComponent> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		createPropertyControls("testInt");
		createPropertyControls("group", "testIntInGroup");

		createLabel("label");
		addControl(new Button(this, SWT.CHECK));

		createLabel("group", "label");
		addControl("group", new Button(this, SWT.CHECK));
	}
}
