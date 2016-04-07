package com.gurella.studio.editor.model;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TITLE_BAR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.property.ModelEditorContext;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.model.property.SimplePropertyEditor;

public class ModelEditorContainer<T> extends ScrolledForm {
	private ModelEditorContext<T> context;

	private Array<PropertyEditor<?>> editors = new Array<>();

	public ModelEditorContainer(Composite parent, T modelInstance) {
		this(parent, new ModelEditorContext<>(modelInstance));
	}

	public ModelEditorContainer(Composite parent, ModelEditorContext<T> context) {
		super(parent, SWT.NONE);
		this.context = context;
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
		ImmutableArray<Property<?>> properties = context.model.getProperties();

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
		PropertyEditor<V> editor = createEditor(getBody(),
				new PropertyEditorContext<>(context, context.model, context.modelInstance, property));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		editor.setLayoutData(layoutData);
		editor.pack();
		editors.add(editor);

		if (editor instanceof SimplePropertyEditor) {
			Label label = toolkit.createLabel(body, editor.getDescriptiveName() + ":");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
			label.setFont(
					GurellaStudioPlugin.createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			label.moveAbove(editor);
		} else {
			Section componentSection = toolkit.createSection(body, TWISTIE | TITLE_BAR);
			componentSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			componentSection.setText(editor.getDescriptiveName());
			editor.setParent(componentSection);
			componentSection.setClient(editor);
			editor.layout(true, true);
			componentSection.setExpanded(true);
		}

		Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}
}
