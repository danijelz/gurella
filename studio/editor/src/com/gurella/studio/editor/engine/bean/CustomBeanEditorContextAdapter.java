package com.gurella.studio.editor.engine.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.ui.property.PropertyEditorData.getDescriptiveName;
import static com.gurella.studio.editor.ui.property.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.editor.bean.BeanEditorFactory;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.engine.ui.SwtEditorComposite;
import com.gurella.studio.editor.engine.ui.SwtEditorLabel;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.engine.ui.SwtEditorWidget;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.ui.bean.DefaultBeanEditor;
import com.gurella.studio.editor.ui.property.PropertyEditor;
import com.gurella.studio.editor.ui.property.PropertyEditorContext;

public class CustomBeanEditorContextAdapter<T> extends BeanEditorContext<T>
		implements com.gurella.engine.editor.bean.BeanEditorContext<T> {
	final BeanEditorFactory<T> factory;

	public CustomBeanEditorContextAdapter(BeanEditorContext<?> parent, T bean, BeanEditorFactory<T> factory) {
		super(parent, bean);
		this.factory = factory;
	}

	public CustomBeanEditorContextAdapter(SceneEditorContext sceneEditorContext, T bean, BeanEditorFactory<T> factory) {
		super(sceneEditorContext, bean);
		this.factory = factory;
	}

	@Override
	public MetaType<T> getMetaType() {
		return this.metaType;
	}

	@Override
	public T getBean() {
		return this.bean;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(this, property));
		propertyEditor.getBody().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		Composite body = propertyEditor.getContent();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property,
			EditorLayoutData layoutData) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(this, property));
		propertyEditor.getBody().setLayoutData(SwtEditorUi.transformLayoutData(layoutData));
		Composite body = propertyEditor.getContent();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite createBeanEditor(EditorComposite parent, Object bean) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		BeanEditorContext<Object> context = new BeanEditorContext<>(this, bean);
		DefaultBeanEditor<Object> beanEditor = new DefaultBeanEditor<>(swtParent, context);
		return new SwtEditorComposite(beanEditor);
	}

	@Override
	public EditorComposite createBeanEditor(EditorComposite parent, Object bean,
			EditorLayoutData layoutData) {
		EditorComposite beanEditor = createBeanEditor(parent, bean);
		beanEditor.setLayoutData(layoutData);
		return beanEditor;
	}

	@Override
	public EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property) {
		EditorUi uiFactory = parent.getUiFactory();
		SwtEditorLabel editorLabel = (SwtEditorLabel) uiFactory.createLabel(parent, getDescriptiveName(this, property));
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
		SwtEditorLabel editorLabel = (SwtEditorLabel) uiFactory.createLabel(parent, getDescriptiveName(this, property));
		editorLabel.setLayoutData(layoutData);
		return editorLabel;
	}

	@Override
	public SwtEditorUi getEditorUi() {
		return SwtEditorUi.instance;
	}
}
