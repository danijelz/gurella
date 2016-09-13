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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.property.ComplexPropertyEditor;
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
		Property<?>[] array = context.model.getProperties().toArray(Property.class);
		int length = array.length;
		if (length > 0) {
			Property<?> last = array[array.length - 1];
			Arrays.stream(array).filter(p -> p.isEditable()).forEach(p -> addEditor(p, p != last));
		}
	}

	private <V> void addEditor(Property<V> property, boolean addSeperator) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(this, new PropertyEditorContext<>(context, property));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite composite = editor.getComposite();
		composite.setLayoutData(layoutData);

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
				layoutData.horizontalSpan = 2;
			}
		} else if (editor instanceof ComplexPropertyEditor) {
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
			layoutData.horizontalSpan = 2;
		}

		if (addSeperator) {
			Label separator = toolkit.createSeparator(this, SWT.HORIZONTAL);
			separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		}
	}
}
