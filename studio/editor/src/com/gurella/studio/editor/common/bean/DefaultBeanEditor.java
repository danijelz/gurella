package com.gurella.studio.editor.common.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.common.property.EditorPropertyData.getGroup;
import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.common.property.CompositePropertyEditor;
import com.gurella.studio.editor.common.property.EditorPropertyData;
import com.gurella.studio.editor.common.property.PropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditorContext;
import com.gurella.studio.editor.common.property.SimplePropertyEditor;

public class DefaultBeanEditor<T> extends BeanEditor<T> {
	private static final RGB separatorRgb = new RGB(88, 158, 255);

	public DefaultBeanEditor(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new BeanEditorContext<>(sceneEditorContext, modelInstance));
	}

	public DefaultBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		GridLayoutFactory.swtDefaults().numColumns(2).margins(1, 1).spacing(5, 2).applyTo(this);
		Property<?>[] array = context.model.getProperties().toArray(Property.class);
		if (array.length > 0) {
			Arrays.sort(array, (p0, p1) -> Integer.compare(getPrpertyIndex(p0), getPrpertyIndex(p1)));
			Map<String, List<Property<?>>> groups = createGroupsMap(array);
			groups.entrySet().stream().sequential().forEach(e -> addGroup(e.getKey(), e.getValue()));
			layout(true, true);
		}
	}

	private int getPrpertyIndex(Property<?> property) {
		return EditorPropertyData.getIndex(context, property);
	}

	private Map<String, List<Property<?>>> createGroupsMap(Property<?>[] array) {
		Map<String, List<Property<?>>> groups = new LinkedHashMap<>();
		groups.put("", new ArrayList<>());
		Arrays.stream(array).sequential().forEach(p -> addToGroups(groups, p));
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
			properties.stream().sequential().forEach(p -> addEditor(p));
		} else {
			ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, groupName, false);
			GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(group);
			properties.stream().sequential().forEach(p -> addEditor(group, p));
		}
	}

	private <V> void addEditor(Property<V> property) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(this, new PropertyEditorContext<>(context, property));
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

			Label label = toolkit.createLabel(this, name + ":");
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
			Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			section.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			section.setLayoutData(sectionLayoutData);
			section.setText(name);
			Composite client = toolkit.createComposite(section);
			GridLayoutFactory.swtDefaults().numColumns(2).spacing(4, 0).margins(0, 0).applyTo(client);
			Label separator = toolkit.createSeparator(client, SWT.VERTICAL | SWT.SHADOW_ETCHED_IN);
			separator.setForeground(GurellaStudioPlugin.getColor(separatorRgb));
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).hint(1, 2).applyTo(separator);
			editorBody.setParent(client);
			editorBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			section.setClient(client);
			section.setExpanded(true);
			section.layout(true, true);
			section.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
		} else {
			editorBodyLayoutData.horizontalSpan = 2;
		}
	}

	private <V> void addEditor(ExpandablePropertyGroup group, Property<V> property) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(this, new PropertyEditorContext<>(context, property));
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

			Label label = toolkit.createLabel(this, name + ":");
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

			group.add(label);
			group.add(editorBody);
		} else if (editor instanceof CompositePropertyEditor) {
			Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			section.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			section.setLayoutData(sectionLayoutData);
			section.setText(name);
			Composite client = toolkit.createComposite(section);
			GridLayoutFactory.swtDefaults().numColumns(2).spacing(4, 0).margins(0, 0).applyTo(client);
			Label separator = toolkit.createSeparator(client, SWT.VERTICAL | SWT.SHADOW_ETCHED_IN);
			separator.setForeground(GurellaStudioPlugin.getColor(separatorRgb));
			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).hint(1, 2).applyTo(separator);
			editorBody.setParent(client);
			editorBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			section.setClient(client);
			section.setExpanded(true);
			section.layout(true, true);
			section.addListener(SWT.MouseUp, e -> editor.showMenuOnMouseUp(e));
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
			group.add(section);
		} else {
			editorBodyLayoutData.horizontalSpan = 2;
			group.add(editorBody);
		}
	}
}
