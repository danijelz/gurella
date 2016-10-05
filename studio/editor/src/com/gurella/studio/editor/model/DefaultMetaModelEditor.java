package com.gurella.studio.editor.model;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.property.CompositePropertyEditor;
import com.gurella.studio.editor.property.PropertyEditor;
import com.gurella.studio.editor.property.PropertyEditorContext;
import com.gurella.studio.editor.property.SimplePropertyEditor;

public class DefaultMetaModelEditor<T> extends MetaModelEditor<T> {
	public DefaultMetaModelEditor(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new ModelEditorContext<>(sceneEditorContext, modelInstance));
	}

	public DefaultMetaModelEditor(Composite parent, ModelEditorContext<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.verticalSpacing = 2;
		setLayout(layout);

		Property<?>[] array = context.model.getProperties().toArray(Property.class);
		int length = array.length;
		if (length > 0) {
			Arrays.stream(array).sequential().filter(p -> p.isEditable()).forEach(p -> addEditor(p));
		}
	}

	private <V> void addEditor(Property<V> property) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(this, new PropertyEditorContext<>(context, property));
		GridData contentLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite composite = editor.getComposite();
		composite.setLayoutData(contentLayoutData);

		PropertyEditorContext<?, V> editorContext = editor.getContext();
		Class<V> propertyType = editorContext.getPropertyType();
		boolean required = propertyType.isPrimitive() ? false
				: (!editorContext.isNullable() && !editorContext.isFixedValue());
		String name = editor.getDescriptiveName() + (required ? "*" : "");

		if (editor instanceof SimplePropertyEditor) {
			boolean longName = name.length() > 20;

			Label label = toolkit.createLabel(this, name + ":");
			label.setAlignment(SWT.RIGHT);
			label.setFont(createFont(label, SWT.BOLD));
			GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
			label.moveAbove(composite);
			label.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));

			if (longName) {
				labelLayoutData.horizontalAlignment = SWT.BEGINNING;
				labelLayoutData.horizontalSpan = 2;
				contentLayoutData.horizontalSpan = 2;
			}
		} else if (editor instanceof CompositePropertyEditor) {
			Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			section.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			section.setLayoutData(sectionLayoutData);
			section.setText(name);
			composite.setParent(section);
			section.setClient(composite);
			section.setExpanded(true);
			section.layout(true, true);
			section.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));
		} else {
			contentLayoutData.horizontalSpan = 2;
		}
	}
}
