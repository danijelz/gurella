package com.gurella.studio.editor.model;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

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
			if (property.isEditorEnabled()) {

				addEditor(property);
			}
		}

		layout(true, true);
	}

	private void addEditor(Property<?> property) {
		FormToolkit toolkit = editor.getToolkit();
		Composite body = getBody();
		PropertyEditor<?> propertyEditor = PropertyEditorFactory.createEditor(this, property);
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		propertyEditor.setLayoutData(layoutData);
		propertyEditor.pack();
		editors.add(propertyEditor);

		if (propertyEditor instanceof SimplePropertyEditor) {
			Label label = toolkit.createLabel(body, property.getDescriptiveName() + ":");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
			FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);// TODO
			label.setFont(boldDescriptor.createFont(label.getDisplay()));
			label.moveAbove(propertyEditor);
		} else {
			Section componentSection = toolkit.createSection(body,
					ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
			componentSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			componentSection.setText(propertyEditor.property.getDescriptiveName());
			propertyEditor.setParent(componentSection);
			componentSection.setClient(propertyEditor);
			propertyEditor.layout(true, true);
			componentSection.setExpanded(true);
		}

		Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	public T getModelInstance() {
		return modelInstance;
	}
}
