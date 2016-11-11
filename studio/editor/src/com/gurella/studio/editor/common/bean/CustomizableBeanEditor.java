package com.gurella.studio.editor.common.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.common.property.PropertyEditorData.getDescriptiveName;
import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.Arrays;

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
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.common.property.CompositePropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditorContext;
import com.gurella.studio.editor.common.property.SimplePropertyEditor;

public abstract class CustomizableBeanEditor<T> extends BeanEditor<T> {
	private OrderedMap<String, ExpandablePropertyGroup> groups;

	public CustomizableBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
		GridLayoutFactory.swtDefaults().numColumns(2).margins(1, 1).spacing(5, 2).applyTo(this);
	}

	protected void addControl(Control control) {
		ExpandablePropertyGroup group = getFirstGroup();
		if (group != null) {
			control.moveAbove(group);
		}
	}

	private OrderedMap<String, ExpandablePropertyGroup> getGroups() {
		if (groups == null) {
			groups = new OrderedMap<>();
		}

		return groups;
	}

	private ExpandablePropertyGroup getOrCreateGroup(String groupName) {
		OrderedMap<String, ExpandablePropertyGroup> groups = getGroups();
		ExpandablePropertyGroup group = groups.get(groupName);
		if (group == null) {
			StringBuilder path = new StringBuilder();
			ExpandablePropertyGroup parent = null;
			int level = -1;

			for (String part : groupName.split("\\.")) {
				level++;
				if (Values.isNotBlank(part)) {
					path.append(path.length() == 0 ? "" : ".").append(part);
					String groupPath = path.toString();
					group = groups.get(groupPath);
					if (group == null) {
						group = createGroup(groupPath, part, level);
					}
					if (parent != null) {
						parent.add(group);
					}
					parent = group;
				}
			}
		}
		return group;
	}

	private ExpandablePropertyGroup createGroup(String groupPath, String name, int level) {
		OrderedMap<String, ExpandablePropertyGroup> groups = getGroups();
		ExpandablePropertyGroup group = new ExpandablePropertyGroup(this, name, false);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.BEGINNING).indent(15 * level, 0)
				.applyTo(group);
		groups.put(groupPath, group);
		return group;
	}

	private ExpandablePropertyGroup getFirstGroup() {
		OrderedMap<String, ExpandablePropertyGroup> groups = getGroups();
		return groups.size == 0 ? null : groups.get(groups.orderedKeys().get(0));
	}

	protected void createGroup(String groupName) {
		getOrCreateGroup(groupName);
	}

	protected void addControl(String groupName, Control control) {
		getOrCreateGroup(groupName).add(control);
	}

	protected void createPropertyControls(String propertyName) {
		Property<Object> property = getProperty(propertyName);
		createEditorControls(null, property);
	}

	private Property<Object> getProperty(String propertyName) {
		return context.model.getProperty(propertyName);
	}

	protected void createPropertyControls(String groupName, String propertyName) {
		createEditorControls(getOrCreateGroup(groupName), getProperty(propertyName));
	}

	protected void createPropertyLabel(String propertyName) {
		addControl(newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false));
	}

	protected void createPropertyLabel(String groupName, String propertyName) {
		Label label = newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false);
		getOrCreateGroup(groupName).add(label);
		indent(groupName, label);
	}

	protected void createLabel(String text) {
		addControl(newLabel(this, text, false));
	}

	protected void createLabel(String groupName, String text) {
		Label label = newLabel(this, text, false);
		getOrCreateGroup(groupName).add(label);
		indent(groupName, label);
	}

	protected void indent(String groupName, Control control) {
		int level = (int) Arrays.stream(groupName.split("\\.")).filter(s -> Values.isNotBlank(s)).count();
		((GridData) control.getLayoutData()).horizontalIndent = 15 * level;
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
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(expand ? 2 : 1, 1).applyTo(label);
		return label;
	}

	protected void createPropertyEditor(Composite parent, String propertyName) {
		Property<Object> property = getProperty(propertyName);
		createEditor(parent, new PropertyEditorContext<>(context, property));
	}

	public Section createSection(String name) {
		FormToolkit toolkit = getToolkit();

		Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
		section.setText(name);
		section.setSize(100, 100);
		section.setExpanded(true);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2, 1).hint(100, SWT.DEFAULT)
				.applyTo(section);

		Composite client = toolkit.createComposite(section);
		GridLayoutFactory.swtDefaults().numColumns(2).spacing(4, 0).margins(0, 0).applyTo(client);
		section.setClient(client);

		return section;
	}

	public Section createSection(String groupName, String name) {
		Section section = createSection(name);
		getOrCreateGroup(groupName).add(section);
		indent(groupName, section);
		return section;
	}

	private <V> void createEditorControls(ExpandablePropertyGroup group, Property<V> property) {
		PropertyEditor<V> editor = createEditor(this, new PropertyEditorContext<>(context, property));
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
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(hSpan, 1)
					.applyTo(editorBody);

			if (group != null) {
				indent(group.name, label);
				group.add(label);
				group.add(editorBody);
			}
		} else if (editor instanceof CompositePropertyEditor) {
			Section section = createSection(name);
			editorBody.setParent((Composite) section.getClient());
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).indent(0, 0).applyTo(editorBody);
			section.layout(true, true);

			if (group != null) {
				indent(group.name, section);
				group.add(section);
			}
		} else {
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).span(2, 1)
					.applyTo(editorBody);
			if (group != null) {
				indent(group.name, editorBody);
				group.add(editorBody);
			}
		}
	}
}
