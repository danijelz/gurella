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
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.model.property.SimplePropertyEditor;

public class ModelEditorContainer<T> extends ScrolledForm {
	public GurellaEditor editor;
	private T modelInstance;
	protected Model<T> model;

	private Array<PropertyEditor<?>> editors = new Array<>();

	public ModelEditorContainer(GurellaEditor editor, Composite parent, T instance) {
		super(parent, SWT.NONE);
		this.editor = editor;
		this.modelInstance = instance;
		this.model = Models.getModel(instance);
		setExpandHorizontal(true);
		GurellaStudioPlugin.getToolkit().adapt(this);
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
			if (property.isEditable()) {

				addEditor(property);
			}
		}

		layout(true, true);
	}

	private <V> void addEditor(Property<V> property) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Composite body = getBody();
		PropertyEditor<V> propertyEditor = PropertyEditorFactory.createEditor(getBody(),
				new PropertyEditorContext<>(model, property, modelInstance), this);
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		propertyEditor.setLayoutData(layoutData);
		propertyEditor.pack();
		editors.add(propertyEditor);

		if (propertyEditor instanceof SimplePropertyEditor) {
			Label label = toolkit.createLabel(body, propertyEditor.getDescriptiveName() + ":");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
			FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);// TODO
			label.setFont(boldDescriptor.createFont(label.getDisplay()));
			label.moveAbove(propertyEditor);
		} else {
			Section componentSection = toolkit.createSection(body,
					ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
			componentSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			componentSection.setText(propertyEditor.getDescriptiveName());
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
