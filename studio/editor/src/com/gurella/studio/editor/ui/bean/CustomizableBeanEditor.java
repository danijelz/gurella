package com.gurella.studio.editor.ui.bean;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.engine.utils.Values.isNotBlank;
import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.GurellaStudioPlugin.showError;
import static com.gurella.studio.editor.ui.property.PropertyEditorData.compare;
import static com.gurella.studio.editor.ui.property.PropertyEditorData.getDescriptiveName;
import static com.gurella.studio.editor.ui.property.PropertyEditorData.getGroup;
import static com.gurella.studio.editor.ui.property.PropertyEditorFactory.createEditor;
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
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.utils.OrderedMap;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.ui.property.CompositePropertyEditor;
import com.gurella.studio.editor.ui.property.PropertyEditor;
import com.gurella.studio.editor.ui.property.PropertyEditorContext;
import com.gurella.studio.editor.ui.property.PropertyEditorData;
import com.gurella.studio.editor.ui.property.PropertyEditorFactory;
import com.gurella.studio.editor.ui.property.SimplePropertyEditor;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public abstract class CustomizableBeanEditor<T> extends BeanEditor<T> implements PreferencesExtension {
	private OrderedMap<String, ExpandableGroup> groups;

	private PreferencesStore preferencesStore;
	private PreferencesNode preferences;

	public CustomizableBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
		addDisposeListener(e -> Workbench.deactivate(this));
		Workbench.activate(this);
		GridLayoutFactory.swtDefaults().numColumns(2).margins(1, 1).spacing(5, 2).applyTo(this);
	}

	private PreferencesNode getPreferences() {
		if (preferences != null) {
			return preferences;
		}

		T bean = context.bean;
		PreferencesNode node = preferencesStore.sceneNode().node(CustomizableBeanEditor.class);
		if (bean instanceof ManagedObject) {
			preferences = node.node(((ManagedObject) bean).ensureUuid());
		} else {
			preferences = node.node(bean.getClass());
		}

		return preferences;
	}

	@Override
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		this.preferencesStore = preferencesStore;
	}

	private OrderedMap<String, ExpandableGroup> getGroups() {
		if (groups == null) {
			groups = new OrderedMap<>();
		}

		return groups;
	}

	private ExpandableGroup getOrCreateGroup(String groupName) {
		ExpandableGroup group = getGroups().get(groupName);
		if (group != null) {
			return group;
		}

		StringBuilder path = new StringBuilder();
		ExpandableGroup parent = null;

		for (String part : groupName.split("\\.")) {
			if (isNotBlank(part)) {
				path.append(path.length() == 0 ? "" : ".").append(part);
				group = getOrCreateGroup(parent, path.toString(), part);
				parent = group;
			}
		}

		return group;
	}

	private ExpandableGroup getOrCreateGroup(ExpandableGroup parent, String path, String name) {
		ExpandableGroup group = getGroups().get(path);
		if (group == null) {
			group = createGroup(parent, path, name);
			if (parent != null) {
				parent.add(group);
			}
		}
		return group;
	}

	private ExpandableGroup createGroup(ExpandableGroup parent, String groupPath, String name) {
		ExpandableGroup group = new ExpandableGroup(this, parent, name, false);
		group.setExpanded(getPreferences().getBoolean(groupPath, false));
		group.addExpandListener(b -> getPreferences().putBoolean(groupPath, b.booleanValue()));
		int h = 15 * group.level;
		GridDataFactory.swtDefaults().align(FILL, BEGINNING).span(2, 1).grab(true, false).indent(h, 0).applyTo(group);
		getGroups().put(groupPath, group);
		return group;
	}

	private ExpandableGroup getFirstGroup() {
		OrderedMap<String, ExpandableGroup> groups = getGroups();
		return groups.size == 0 ? null : groups.get(groups.orderedKeys().get(0));
	}

	protected void createGroup(String groupName) {
		getOrCreateGroup(groupName);
	}

	protected void addControl(Control control) {
		Optional.ofNullable(getFirstGroup()).ifPresent(g -> control.moveAbove(g));
	}

	protected void addControl(String groupName, Control control) {
		ExpandableGroup group = getOrCreateGroup(groupName);
		group.add(control);
		indent(control, group.level + 1);
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

	protected void createPropertyControls(Property<?> property) {
		createPropertyControls(property, true);
	}

	protected void createPropertyControls(String propertyName, boolean considerPropertyGroup) {
		Property<?> property = getProperty(propertyName);
		createPropertyControls(property, considerPropertyGroup);
	}

	protected void createPropertyControls(Property<?> property, boolean considerPropertyGroup) {
		ExpandableGroup group = considerPropertyGroup ? getOrCreateGroup(getGroup(context, property)) : null;
		createEditorControls(group, property);
	}

	private Property<?> getProperty(String propertyName) {
		return context.metaType.getProperty(propertyName);
	}

	protected void createPropertyControls(String groupName, String propertyName) {
		createPropertyControls(groupName, propertyName, true);
	}

	protected void createPropertyControls(String groupName, Property<?> property) {
		createPropertyControls(groupName, property, true);
	}

	protected void createPropertyControls(String groupName, String propertyName, boolean considerPropertyGroup) {
		Property<?> property = getProperty(propertyName);
		createPropertyControls(groupName, property, considerPropertyGroup);
	}

	protected void createPropertyControls(String groupName, Property<?> property, boolean considerPropertyGroup) {
		String resolvedGroupName = considerPropertyGroup ? groupName + "." + getGroup(context, property) : groupName;
		createEditorControls(getOrCreateGroup(resolvedGroupName), property);
	}

	protected void createPropertyLabel(String propertyName) {
		addControl(newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false));
	}

	protected void createPropertyLabel(String groupName, String propertyName) {
		Label label = newLabel(this, getDescriptiveName(context, getProperty(propertyName)), false);
		ExpandableGroup group = getOrCreateGroup(groupName);
		group.add(label);
		indent(label, group.level + 1);
	}

	protected void createLabel(String text) {
		addControl(newLabel(this, text, false));
	}

	protected void createLabel(String groupName, String text) {
		Label label = newLabel(this, text, false);
		ExpandableGroup group = getOrCreateGroup(groupName);
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
		Property<?> property = getProperty(propertyName);
		createEditor(parent, new PropertyEditorContext<>(context, property));
	}

	protected Section createSection(String name) {
		FormToolkit toolkit = getToolkit();

		Section section = toolkit.createSection(this, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
		section.setText(name);
		section.setSize(100, 100);
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
		ExpandableGroup group = getOrCreateGroup(groupName);
		group.add(section);
		indent(section, group.level + 1);
		return section;
	}

	private <V> void createEditorControls(ExpandableGroup group, Property<V> property) {
		createEditorControls(group, new PropertyEditorContext<>(context, property));
	}

	protected <V> void createEditorControls(ExpandableGroup group, PropertyEditorContext<T, V> propertyContext) {
		if (PropertyEditorFactory.hasReflectionEditor(propertyContext)) {
			createCompositeEditors(group, propertyContext);
		} else {
			createFactoryEditor(group, propertyContext);
		}
	}

	protected <V> void createFactoryEditor(ExpandableGroup group, PropertyEditorContext<T, V> propertyContext) {
		PropertyEditor<V> editor = createEditor(this, propertyContext);
		Composite editorBody = editor.getBody();
		String name = editor.getDescriptiveName() + (isPropertyRequired(propertyContext) ? "*" : "");

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
			GridDataFactory.swtDefaults().align(FILL, FILL).grab(true, true).applyTo(editorBody);
			String qualifiedName = propertyContext.getQualifiedName();
			section.setExpanded(getPreferences().getBoolean(qualifiedName, true));
			section.addExpansionListener(new ExpansionListener(qualifiedName));
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

	private <V> boolean isPropertyRequired(PropertyEditorContext<T, V> propertyContext) {
		Class<V> propertyType = propertyContext.getPropertyType();
		return propertyType.isPrimitive() ? false : (!propertyContext.isNullable() && !propertyContext.isFixedValue());
	}

	private <V> void createCompositeEditors(ExpandableGroup parentGroup, PropertyEditorContext<T, V> propertyContext) {
		Property<V> property = propertyContext.property;
		String name = PropertyEditorData.getDescriptiveName(context, property);
		ExpandableGroup group = new ExpandableGroup(this, parentGroup, name + ":", true);
		group.setExpanded(getPreferences().getBoolean(group.qualifiedName, false));
		group.addExpandListener(b -> getPreferences().putBoolean(group.qualifiedName, b.booleanValue()));
		getGroups().put(group.qualifiedName, group);
		GridDataFactory.fillDefaults().align(BEGINNING, CENTER).indent(0, 0).applyTo(group);

		V value = propertyContext.getValue();
		Class<V> selected = Optional.ofNullable(value).map(v -> Values.<Class<V>> cast(v.getClass())).orElse(null);

		SceneEditorContext sceneContext = context.sceneContext;
		TypeSelectionWidget<V> selector = new TypeSelectionWidget<>(this, sceneContext, property.getType(), selected);
		GridDataFactory.fillDefaults().align(FILL, BEGINNING).grab(true, false).indent(0, 0).applyTo(selector);
		selector.addSelectionListener(t -> typeSelectionChanged(t, group, selector, propertyContext));

		Optional.ofNullable(value).ifPresent(v -> createCompositeEditors(group, selector, propertyContext, v));
	}

	private <V> void typeSelectionChanged(Class<? extends V> type, ExpandableGroup group,
			TypeSelectionWidget<V> selector, PropertyEditorContext<T, V> parent) {
		group.clear();
		String message = "Error occurred while creating value";
		V value = Try.ofFailable(() -> type.newInstance()).onFailure(e -> showError(e, message)).orElse(null);
		parent.setValue(value);
		Optional.ofNullable(value).ifPresent(v -> createCompositeEditors(group, selector, parent, v));
		UiUtils.reflow(this);
	}

	private <V> void createCompositeEditors(ExpandableGroup group, TypeSelectionWidget<V> selector,
			PropertyEditorContext<T, V> parent, V value) {
		Property<?>[] properties = MetaTypes.getMetaType(value.getClass()).getProperties().toArray(Property.class);
		Arrays.stream(properties).filter(p -> p.isEditable()).sorted((p1, p2) -> compare(context, p1, p2))
				.forEachOrdered(p -> createEditorControls(getPropertyGroup(group, p),
						new PropertyEditorContext<>(parent, cast(value), p)));
		selector.moveBelow(group);
	}

	private ExpandableGroup getPropertyGroup(ExpandableGroup group, Property<?> property) {
		String propertyGroup = getGroup(context, property);
		return Values.isBlank(propertyGroup) ? group : getOrCreateGroup(group.qualifiedName + "." + propertyGroup);
	}

	private class ExpansionListener extends ExpansionAdapter {
		final String qualifiedName;

		ExpansionListener(String qualifiedName) {
			this.qualifiedName = qualifiedName;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			getPreferences().putBoolean(qualifiedName, e.getState());
		}
	}
}
