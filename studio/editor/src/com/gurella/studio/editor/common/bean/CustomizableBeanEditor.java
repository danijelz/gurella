package com.gurella.studio.editor.common.bean;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.engine.utils.Values.isNotBlank;
import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.GurellaStudioPlugin.showError;
import static com.gurella.studio.editor.common.property.PropertyEditorData.compare;
import static com.gurella.studio.editor.common.property.PropertyEditorData.getDescriptiveName;
import static com.gurella.studio.editor.common.property.PropertyEditorData.getGroup;
import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;
import static org.eclipse.swt.SWT.BEGINNING;
import static org.eclipse.swt.SWT.CENTER;
import static org.eclipse.swt.SWT.DEFAULT;
import static org.eclipse.swt.SWT.FILL;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.utils.OrderedMap;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.common.property.CompositePropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditorContext;
import com.gurella.studio.editor.common.property.PropertyEditorData;
import com.gurella.studio.editor.common.property.PropertyEditorFactory;
import com.gurella.studio.editor.common.property.SimplePropertyEditor;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public abstract class CustomizableBeanEditor<T> extends BeanEditor<T> {
	private OrderedMap<String, ExpandablePropertyGroup> groups;

	public CustomizableBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
		GridLayoutFactory.swtDefaults().numColumns(2).margins(1, 1).spacing(5, 2).applyTo(this);
	}

	private OrderedMap<String, ExpandablePropertyGroup> getGroups() {
		if (groups == null) {
			groups = new OrderedMap<>();
		}

		return groups;
	}

	private ExpandablePropertyGroup getOrCreateGroup(String groupName) {
		ExpandablePropertyGroup group = getGroups().get(groupName);
		if (group == null) {
			StringBuilder path = new StringBuilder();
			ExpandablePropertyGroup parent = null;

			for (String part : groupName.split("\\.")) {
				if (isNotBlank(part)) {
					path.append(path.length() == 0 ? "" : ".").append(part);
					group = getOrCreateGroup(parent, path.toString(), part);
					parent = group;
				}
			}
		}
		return group;
	}

	private ExpandablePropertyGroup getOrCreateGroup(ExpandablePropertyGroup parent, String path, String name) {
		ExpandablePropertyGroup group = getGroups().get(path);
		if (group == null) {
			group = createGroup(parent, path, name);
			if (parent != null) {
				parent.add(group);
			}
		}
		return group;
	}

	private ExpandablePropertyGroup createGroup(ExpandablePropertyGroup parent, String groupPath, String name) {
		ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, parent, name, false);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.BEGINNING)
				.indent(15 * group.level, 0).applyTo(group);
		getGroups().put(groupPath, group);
		return group;
	}

	private ExpandablePropertyGroup getFirstGroup() {
		OrderedMap<String, ExpandablePropertyGroup> groups = getGroups();
		return groups.size == 0 ? null : groups.get(groups.orderedKeys().get(0));
	}

	protected void createGroup(String groupName) {
		getOrCreateGroup(groupName);
	}

	protected void addControl(Control control) {
		Optional.ofNullable(getFirstGroup()).ifPresent(g -> control.moveAbove(g));
	}

	protected void addControl(String groupName, Control control) {
		getOrCreateGroup(groupName).add(control);
	}

	protected void addControlRow(String labelText, Control control) {
		createLabel(labelText);
		addControl(control);
	}

	protected void addControlRow(String groupName, String labelText, Control control) {
		createLabel(groupName, labelText);
		addControl(groupName, control);
	}

	protected void createPropertyControls(String propertyName) {
		createPropertyControls(propertyName, true);
	}

	protected <P> void createPropertyControls(Property<P> property) {
		createPropertyControls(property, true);
	}

	protected void createPropertyControls(String propertyName, boolean considerPropertyGroup) {
		Property<Object> property = getProperty(propertyName);
		createPropertyControls(property, considerPropertyGroup);
	}

	protected <P> void createPropertyControls(Property<P> property, boolean considerPropertyGroup) {
		ExpandablePropertyGroup group = considerPropertyGroup ? getOrCreateGroup(getGroup(context, property)) : null;
		createEditorControls(group, property);
	}

	private Property<Object> getProperty(String propertyName) {
		return context.model.getProperty(propertyName);
	}

	protected void createPropertyControls(String groupName, String propertyName) {
		createPropertyControls(groupName, propertyName, true);
	}

	protected void createPropertyControls(String groupName, String propertyName, boolean considerPropertyGroup) {
		Property<Object> property = getProperty(propertyName);
		String resolvedGroupName = considerPropertyGroup ? groupName + "." + getGroup(context, property) : groupName;
		createEditorControls(getOrCreateGroup(resolvedGroupName), property);
	}

	protected void createPropertyLabel(String propertyName) {
		addControl(newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false));
	}

	protected void createPropertyLabel(String groupName, String propertyName) {
		Label label = newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false);
		ExpandablePropertyGroup group = getOrCreateGroup(groupName);
		group.add(label);
		indent(label, group.level + 1);
	}

	protected void createLabel(String text) {
		addControl(newLabel(this, text, false));
	}

	protected void createLabel(String groupName, String text) {
		Label label = newLabel(this, text, false);
		ExpandablePropertyGroup group = getOrCreateGroup(groupName);
		group.add(label);
		indent(label, group.level + 1);
	}

	protected void indent(Control control, int level) {
		((GridData) control.getLayoutData()).horizontalIndent = 17 * level;
	}

	protected void createPropertyLabel(Composite parent, String propertyName) {
		newLabel(parent, getDescriptiveName(context, getProperty(propertyName)), false);
	}

	protected void createLabel(Composite parent, String text) {
		newLabel(parent, text, false);
	}

	private static Label newLabel(Composite parent, String text, boolean expand) {
		Label label = getToolkit().createLabel(parent, text + ":");
		label.setAlignment(SWT.LEFT);
		Font font = createFont(label, SWT.BOLD);
		label.addDisposeListener(e -> destroyFont(font));
		label.setFont(font);
		GridDataFactory.swtDefaults().align(FILL, CENTER).span(expand ? 2 : 1, 1).applyTo(label);
		return label;
	}

	protected void createPropertyEditor(Composite parent, String propertyName) {
		Property<Object> property = getProperty(propertyName);
		createEditor(parent, new PropertyEditorContext<>(context, property));
	}

	protected Section createSection(String name) {
		FormToolkit toolkit = getToolkit();

		Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
		section.setText(name);
		section.setSize(100, 100);
		section.setExpanded(true);
		GridDataFactory.swtDefaults().align(FILL, BEGINNING).grab(true, false).span(2, 1).hint(100, DEFAULT)
				.applyTo(section);

		Composite client = toolkit.createComposite(section);
		GridLayoutFactory.swtDefaults().numColumns(2).spacing(4, 0).margins(0, 0).applyTo(client);
		section.setClient(client);
		addControl(section);

		return section;
	}

	protected Section createSection(String groupName, String name) {
		Section section = createSection(name);
		ExpandablePropertyGroup group = getOrCreateGroup(groupName);
		group.add(section);
		indent(section, group.level + 1);
		return section;
	}

	private <V> void createEditorControls(ExpandablePropertyGroup group, Property<V> property) {
		createEditorControls(group, new PropertyEditorContext<>(context, property));
	}

	protected <V> void createEditorControls(ExpandablePropertyGroup group,
			PropertyEditorContext<T, V> propertyContext) {
		if (PropertyEditorFactory.hasReflectionEditor(propertyContext)) {
			createCompositeEditors(group, propertyContext);
		} else {
			createSimpleEditor(group, propertyContext);
		}
	}

	protected <V> void createSimpleEditor(ExpandablePropertyGroup group, PropertyEditorContext<T, V> propertyContext) {
		PropertyEditor<V> editor = createEditor(this, propertyContext);
		Composite editorBody = editor.getBody();

		PropertyEditorContext<?, V> context = editor.getContext();
		Class<V> propertyType = context.getPropertyType();
		boolean required = propertyType.isPrimitive() ? false : (!context.isNullable() && !context.isFixedValue());
		String name = editor.getDescriptiveName() + (required ? "*" : "");

		if (editor instanceof SimplePropertyEditor) {
			boolean longName = name.length() > 20;
			Label label = newLabel(this, name, longName);
			label.moveAbove(editorBody);
			label.addListener(SWT.MouseUp, e -> editor.showMenu());
			int hSpan = longName ? 2 : 1;
			GridDataFactory.swtDefaults().align(FILL, BEGINNING).grab(true, false).span(hSpan, 1).applyTo(editorBody);

			if (group != null) {
				indent(label, group.level + 1);
				group.add(label);
				group.add(editorBody);
			}
		} else if (editor instanceof CompositePropertyEditor) {
			Section section = createSection(name);
			editorBody.setParent((Composite) section.getClient());
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).indent(0, 0).applyTo(editorBody);
			section.layout(true, true);

			if (group != null) {
				indent(section, group.level + 1);
				group.add(section);
			}
		} else {
			GridDataFactory.swtDefaults().align(FILL, BEGINNING).grab(true, false).span(2, 1).applyTo(editorBody);
			if (group != null) {
				indent(editorBody, group.level + 1);
				group.add(editorBody);
			}
		}
	}

	private <V> void createCompositeEditors(ExpandablePropertyGroup parentGroup,
			PropertyEditorContext<T, V> propertyContext) {
		Property<V> property = propertyContext.property;
		String name = PropertyEditorData.getDescriptiveName(context, property);
		ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, parentGroup, name + ":", true);
		getGroups().put(group.qualifiedName, group);
		GridDataFactory.fillDefaults().align(BEGINNING, CENTER).indent(0, 0).applyTo(group);

		V value = propertyContext.getValue();
		Class<V> selected = value == null ? null : cast(value.getClass());

		SceneEditorContext sceneContext = context.sceneEditorContext;
		TypeSelectionWidget<V> selector = new TypeSelectionWidget<>(this, sceneContext, property.getType(), selected);
		GridDataFactory.fillDefaults().align(FILL, BEGINNING).grab(true, false).indent(0, 0).applyTo(selector);
		selector.addTypeSelectionListener(t -> typeSelectionChanged(t, group, selector, propertyContext));

		Optional.ofNullable(value).ifPresent(v -> createCompositeEditors(group, selector, propertyContext, v));
	}

	private <V> void typeSelectionChanged(Class<? extends V> type, ExpandablePropertyGroup group,
			TypeSelectionWidget<V> selector, PropertyEditorContext<T, V> parent) {
		group.clear();
		String message = "Error occurred while creating value";
		V value = Try.ofFailable(() -> type.newInstance()).onFailure(e -> showError(e, message)).orElse(null);
		parent.setValue(value);
		Optional.ofNullable(value).ifPresent(v -> createCompositeEditors(group, selector, parent, v));
		UiUtils.reflow(this);
	}

	private <V> void createCompositeEditors(ExpandablePropertyGroup group, TypeSelectionWidget<V> selector,
			PropertyEditorContext<T, V> parent, V value) {
		Property<?>[] properties = Models.getModel(value.getClass()).getProperties().toArray(Property.class);
		Arrays.stream(properties).filter(p -> p.isEditable()).sorted((p1, p2) -> compare(context, p1, p2))
				.forEach(p -> createEditorControls(getPropertyGroup(group, p),
						new PropertyEditorContext<>(parent, cast(value), p)));
		selector.moveBelow(group);
	}

	private ExpandablePropertyGroup getPropertyGroup(ExpandablePropertyGroup group, Property<?> property) {
		String propertyGroup = getGroup(context, property);
		return Values.isBlank(propertyGroup) ? group : getOrCreateGroup(group.qualifiedName + "." + propertyGroup);
	}
}
