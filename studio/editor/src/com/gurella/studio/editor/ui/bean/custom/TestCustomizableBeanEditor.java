package com.gurella.studio.editor.ui.bean.custom;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.test.TestEditorComponent;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.ui.bean.CustomizableBeanEditor;

//TODO remove
public class TestCustomizableBeanEditor extends CustomizableBeanEditor<TestEditorComponent> {
	public TestCustomizableBeanEditor(Composite parent, BeanEditorContext<TestEditorComponent> context) {
		super(parent, context);
		createContent();
	}

	private void createContent() {
		//createPropertyControls("testInt");
		//createPropertyControls("group", "intGroup");
		createPropertyControls("testObj");
		//createPropertyControls("testObj2");

//		createLabel("label");
//		addControl(new Button(this, SWT.CHECK));
//
//		createLabel("group", "label");
//		addControl("group", new Button(this, SWT.CHECK));
//
//		createLabel("group.nested", "nested");
//		addControl("group.nested", new Button(this, SWT.CHECK));
//		
//		Section section = createSection("section");
//		Label label = new Label((Composite) section.getClient(), SWT.NONE);
//		label.setText("sectionLabel");
	}
}
