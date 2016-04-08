package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.math.Matrix3;

public class Matrix3PropertyEditor extends ComplexPropertyEditor<Matrix3> {
	private Text m00;
	private Text m01;
	private Text m02;
	private Text m10;
	private Text m11;
	private Text m12;
	private Text m20;
	private Text m21;
	private Text m22;
	
	public Matrix3PropertyEditor(Composite parent, PropertyEditorContext<?, Matrix3> context) {
		super(parent, context);
		
		body.setLayout(new GridLayout(6, false));
	}

}
