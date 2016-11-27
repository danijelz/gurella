package com.gurella.studio.editor.engine.property;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.common.property.PropertyEditorData.getDescriptiveName;
import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.metatype.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.bean.BeanEditorContext;
import com.gurella.studio.editor.common.bean.DefaultBeanEditor;
import com.gurella.studio.editor.common.property.PropertyEditor;
import com.gurella.studio.editor.common.property.PropertyEditorContext;
import com.gurella.studio.editor.engine.ui.SwtEditorComposite;
import com.gurella.studio.editor.engine.ui.SwtEditorLabel;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.engine.ui.SwtEditorWidget;

class CustomPropertyEditorContextAdapter<P> implements com.gurella.engine.editor.property.PropertyEditorContext<P> {
	private PropertyEditorContext<?, P> context;
	private PropertyEditor<P> editor;

	public CustomPropertyEditorContextAdapter(PropertyEditorContext<?, P> context, PropertyEditor<P> editor) {
		this.context = context;
		this.editor = editor;
	}

	@Override
	public Property<P> getProperty() {
		return context.property;
	}

	@Override
	public Object getModelInstance() {
		return context.model;
	}

	@Override
	public P getPropertyValue() {
		return context.getValue();
	}

	@Override
	public void setPropertyValue(P value) {
		editor.setValue(value);
	}

	@Override
	public void addMenuItem(String text, Runnable action) {
		editor.addMenuItem(text, action);
	}

	@Override
	public void removeMenuItem(String text) {
		editor.removeMenuItem(text);
	}

	@Override
	public SwtEditorUi getEditorUi() {
		return SwtEditorUi.instance;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(context, property));
		propertyEditor.getBody().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		Composite body = propertyEditor.getContent();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property,
			EditorLayoutData layoutData) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(context, property));
		propertyEditor.getBody().setLayoutData(SwtEditorUi.transformLayoutData(layoutData));
		Composite body = propertyEditor.getContent();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite createModelEditor(EditorComposite parent, Object modelInstance) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		BeanEditorContext<Object> modelContext = new BeanEditorContext<>(context, modelInstance);
		DefaultBeanEditor<Object> modelEditor = new DefaultBeanEditor<>(swtParent, modelContext);
		return new SwtEditorComposite(modelEditor);
	}

	@Override
	public EditorComposite createModelEditor(EditorComposite parent, Object modelInstance,
			EditorLayoutData layoutData) {
		EditorComposite modelEditor = createModelEditor(parent, modelInstance);
		modelEditor.setLayoutData(layoutData);
		return modelEditor;
	}

	@Override
	public EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property) {
		EditorUi uiFactory = parent.getUiFactory();
		SwtEditorLabel editorLabel = (SwtEditorLabel) uiFactory.createLabel(parent, getDescriptiveName(context));
		Label label = editorLabel.getWidget();
		label.setAlignment(SWT.RIGHT);
		Font font = createFont(label, SWT.BOLD);
		label.setFont(font);
		label.addDisposeListener(e -> GurellaStudioPlugin.destroyFont(font));
		GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(labelLayoutData);
		return editorLabel;
	}

	@Override
	public EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property, EditorLayoutData layoutData) {
		EditorUi uiFactory = parent.getUiFactory();
		SwtEditorLabel editorLabel = (SwtEditorLabel) uiFactory.createLabel(parent, getDescriptiveName(context));
		editorLabel.setLayoutData(layoutData);
		return editorLabel;
	}
}