package com.gurella.studio.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.studio.editor.GurellaEditor;

public class ModelPropertiesContainer<T> extends ScrolledForm {
	protected GurellaEditor editor;
	protected T modelInstance;
	protected Model<T> model;

	private Array<PropertyEditor<?>> editors = new Array<>();

	public ModelPropertiesContainer(GurellaEditor editor, Composite parent, T instance) {
		super(parent, SWT.NONE);
		this.editor = editor;
		this.modelInstance = instance;
		this.model = Models.getModel(instance);
		setExpandHorizontal(true);
		editor.getToolkit().adapt(this);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		getBody().setLayout(layout);
		initEditors();
		layout(true, true);
	}

	private void initEditors() {
		ImmutableArray<Property<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			Label label = editor.getToolkit().createLabel(getBody(), property.getDescriptiveName() + ":");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
			PropertyEditor<?> propertyEditor = PropertyEditorFactory.createEditor(this, property);
			propertyEditor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			propertyEditor.pack();
			editors.add(propertyEditor);
		}
	}
}
