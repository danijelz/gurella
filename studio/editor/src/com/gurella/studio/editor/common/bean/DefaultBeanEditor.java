package com.gurella.studio.editor.common.bean;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.GurellaStudioPlugin.showError;
import static com.gurella.studio.editor.common.property.PropertyEditorData.getGroup;
import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.common.TypeSelectionWidget;
import com.gurella.studio.editor.common.property.CompositePropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditorContext;
import com.gurella.studio.editor.common.property.PropertyEditorData;
import com.gurella.studio.editor.common.property.PropertyEditorFactory;
import com.gurella.studio.editor.common.property.SimplePropertyEditor;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

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
		return PropertyEditorData.getIndex(context, property);
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
		if (Values.isBlank(groupName)) {
			properties.stream().sequential().forEach(p -> addEditor(p));
		} else {
			// int indent = -1;
			// int index = 0;
			// while(index >= 0) {
			// index = groupName.indexOf('.', index);
			// indent++;
			// }
			//
			// StringBuilder builder = new StringBuilder();
			// IntStream.range(0, indent).forEach(i -> builder.append('\t'));
			// builder.append(groupName);

			ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, groupName, false);
			GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(group);
			properties.stream().sequential().forEach(p -> addEditor(group, p));
		}
	}

	private <V> void addEditor(Property<V> property) {
		PropertyEditorContext<T, V> propertyContext = new PropertyEditorContext<>(context, property);
		if (PropertyEditorFactory.hasReflectionEditor(propertyContext)) {
			createGroupedRefelectionProperty(property, propertyContext);
			return;
		}

		PropertyEditor<V> editor = createEditor(this, propertyContext);
		GridData editorBodyLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite editorBody = editor.getBody();
		editorBody.setLayoutData(editorBodyLayoutData);

		PropertyEditorContext<?, V> editorContext = editor.getContext();
		Class<V> propertyType = editorContext.getPropertyType();
		boolean required = propertyType.isPrimitive() ? false
				: (!editorContext.isNullable() && !editorContext.isFixedValue());
		String name = editor.getDescriptiveName() + (required ? "*" : "");
		FormToolkit toolkit = getToolkit();

		if (editor instanceof SimplePropertyEditor) {
			boolean longName = name.length() > 20;

			Label label = toolkit.createLabel(this, name + ":");
			label.setAlignment(SWT.LEFT);
			Font font = createFont(label, SWT.BOLD);
			label.addDisposeListener(e -> destroyFont(font));
			label.setFont(font);
			GridData labelLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
			label.moveAbove(editorBody);
			label.addListener(SWT.MouseUp, e -> editor.showMenu());

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
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
		} else {
			editorBodyLayoutData.horizontalSpan = 2;
		}
	}

	private <V> void createGroupedRefelectionProperty(Property<V> property,
			PropertyEditorContext<T, V> propertyContext) {
		String name = PropertyEditorData.getDescriptiveName(context, property);
		ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, name + ":", true);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(0, 0).applyTo(group);

		V value = propertyContext.getValue();
		Class<V> selected = value == null ? null : cast(value.getClass());

		SceneEditorContext sceneContext = context.sceneEditorContext;
		TypeSelectionWidget<V> selector = new TypeSelectionWidget<>(this, sceneContext, property.getType(), selected);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).hint(150, 18).indent(0, 0)
				.applyTo(selector);
		selector.addTypeSelectionListener(t -> typeSelectionChanged(t, group, selector, propertyContext));

		Optional.ofNullable(value).ifPresent(v -> createRefelectionEditors(group, selector, propertyContext, v));
	}

	private <V> void typeSelectionChanged(Class<? extends V> type, ExpandablePropertyGroup group,
			TypeSelectionWidget<V> selector, PropertyEditorContext<T, V> parent) {
		group.clear();
		if (type == null) {
			return;
		}

		String message = "Error occurred while creating value";
		V value = Try.ofFailable(() -> type.newInstance()).onFailure(e -> showError(e, message)).orElse(null);
		parent.setValue(value);
		createRefelectionEditors(group, selector, parent, value);
		UiUtils.reflow(this);
	}

	protected <V> void createRefelectionEditors(ExpandablePropertyGroup group, TypeSelectionWidget<V> selector,
			PropertyEditorContext<T, V> parent, V value) {
		Property<?>[] properties = Models.getModel(value.getClass()).getProperties().toArray(Property.class);
		Arrays.stream(properties).sequential()
				.forEach(p -> addEditor(group, new PropertyEditorContext<>(parent, cast(value), p)));
		selector.moveBelow(group);
	}

	private <V> void addEditor(ExpandablePropertyGroup group, Property<V> property) {
		PropertyEditorContext<T, V> propartyContext = new PropertyEditorContext<>(context, property);
		addEditor(group, propartyContext);
	}

	protected <V> void addEditor(ExpandablePropertyGroup group, PropertyEditorContext<T, V> propartyContext) {
		FormToolkit toolkit = getToolkit();
		PropertyEditor<V> editor = createEditor(this, propartyContext);
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

			Label label = toolkit.createLabel(this, "\t" + name + ":");
			label.setAlignment(SWT.LEFT);
			Font font = createFont(label, SWT.BOLD);
			label.addDisposeListener(e -> destroyFont(font));
			label.setFont(font);
			GridData labelLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
			label.moveAbove(editorBody);
			label.addListener(SWT.MouseUp, e -> editor.showMenu());

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
			editorBodyLayoutData.horizontalIndent = 0;
			editorBodyLayoutData.verticalIndent = 0;
			group.add(section);
		} else {
			editorBodyLayoutData.horizontalSpan = 2;
			group.add(editorBody);
		}
	}
}
