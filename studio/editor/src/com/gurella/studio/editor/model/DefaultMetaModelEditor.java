package com.gurella.studio.editor.model;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.property.EditorPropertyData.getGroup;
import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.property.CompositePropertyEditor;
import com.gurella.studio.editor.property.EditorPropertyData;
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
		if (array.length > 0) {
			Arrays.sort(array, (p0, p1) -> Integer.compare(getPrpertyIndex(p0), getPrpertyIndex(p1)));
			Map<String, List<Property<?>>> groups = createGroupsMap(array);
			groups.entrySet().stream().sequential().forEach(e -> addGroup(e.getKey(), e.getValue()));
			// Arrays.stream(array).sequential().filter(p -> p.isEditable()).forEach(p -> addEditor(this, p));
		}
	}

	private int getPrpertyIndex(Property<?> property) {
		return EditorPropertyData.getIndex(context, property);
	}

	private Map<String, List<Property<?>>> createGroupsMap(Property<?>[] array) {
		Map<String, List<Property<?>>> groups = new LinkedHashMap<>();
		groups.put("", new ArrayList<>());
		Arrays.stream(array).forEach(p -> addToGroups(groups, p));
		return groups;
	}

	private void addToGroups(Map<String, List<Property<?>>> groups, Property<?> property) {
		if (!property.isEditable()) {
			return;
		}

		String group = getGroup(context, property);
		List<Property<?>> groupProperties = groups.get(group);
		if (groupProperties == null) {
			groupProperties = new ArrayList<>();
			groups.put(group, groupProperties);
		}

		groupProperties.add(property);
	}

	private void addGroup(String groupName, List<Property<?>> properties) {
		if (groupName.length() == 0) {
			properties.stream().sequential().forEach(p -> addEditor(this, p));
		} else {
			FormToolkit toolkit = getToolkit();

			Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			section.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			section.setLayoutData(sectionLayoutData);
			section.setText(groupName);
			section.setExpanded(false);

			Composite client = toolkit.createComposite(section);
			section.setClient(client);
			GridLayoutFactory.swtDefaults().numColumns(2).spacing(0, 0).margins(0, 0).applyTo(client);

			Label separator = toolkit.createSeparator(client, SWT.VERTICAL | SWT.SHADOW_ETCHED_IN);
			separator.setForeground(GurellaStudioPlugin.getColor(88, 158, 255));
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).hint(1, 2).applyTo(separator);

			Composite editorBody = toolkit.createComposite(client);
			GridData editorBodyLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
			editorBody.setLayoutData(editorBodyLayoutData);
			editorBody.setBackground(GurellaStudioPlugin.createColor(255, 0, 0));

			properties.stream().sequential().forEach(p -> addEditor(editorBody, p));
		}
	}

	private <V> void addEditor(Composite parent, Property<V> property) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(parent, new PropertyEditorContext<>(context, property));
		GridData editorBodyLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite editorBody = editor.getBody();
		editorBody.setLayoutData(editorBodyLayoutData);

		PropertyEditorContext<?, V> editorContext = editor.getContext();
		Class<V> propertyType = editorContext.getPropertyType();
		boolean required = propertyType.isPrimitive() ? false
				: (!editorContext.isNullable() && !editorContext.isFixedValue());
		String name = editor.getDescriptiveName() + (required ? "*" : "");

		if (editor instanceof SimplePropertyEditor) {
			boolean longName = name.length() > 20;

			Label label = toolkit.createLabel(parent, name + ":");
			label.setAlignment(SWT.RIGHT);
			Font font = createFont(label, SWT.BOLD);
			label.addDisposeListener(e -> destroyFont(font));
			label.setFont(font);
			GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
			label.moveAbove(editorBody);
			label.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));

			if (longName) {
				labelLayoutData.horizontalAlignment = SWT.BEGINNING;
				labelLayoutData.horizontalSpan = 2;
				editorBodyLayoutData.horizontalSpan = 2;
			}
		} else if (editor instanceof CompositePropertyEditor) {
			Section section = toolkit.createSection(parent, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			section.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			section.setLayoutData(sectionLayoutData);
			section.setText(name);
			Composite client = toolkit.createComposite(section);
			GridLayoutFactory.swtDefaults().numColumns(2).spacing(0, 0).margins(0, 0).applyTo(client);
			Label separator = toolkit.createSeparator(client, SWT.VERTICAL | SWT.SHADOW_ETCHED_IN);
			separator.setForeground(GurellaStudioPlugin.getColor(88, 158, 255));
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).hint(1, 2).applyTo(separator);
			editorBody.setParent(client);
			editorBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			section.setClient(client);
			section.setExpanded(false);
			section.layout(true, true);
			section.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
		} else {
			editorBodyLayoutData.horizontalSpan = 2;
		}
	}
}
