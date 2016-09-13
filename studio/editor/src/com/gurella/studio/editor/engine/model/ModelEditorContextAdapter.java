package com.gurella.studio.editor.engine.model;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.engine.ui.SwtEditorComposite;
import com.gurella.studio.editor.engine.ui.SwtEditorLabel;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.engine.ui.SwtEditorWidget;
import com.gurella.studio.editor.model.ModelEditorContext;
import com.gurella.studio.editor.property.PropertyEditor;
import com.gurella.studio.editor.property.PropertyEditorContext;

class ModelEditorContextAdapter<T> extends ModelEditorContext<T>
		implements com.gurella.engine.editor.model.ModelEditorContext<T> {

	public ModelEditorContextAdapter(SceneEditorContext sceneEditorContext, T modelInstance) {
		super(sceneEditorContext, modelInstance);
	}

	@Override
	public Model<T> model() {
		return this.model;
	}

	@Override
	public T getModelInstance() {
		return this.modelInstance;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(this, property));
		propertyEditor.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		Composite body = propertyEditor.getBody();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property,
			EditorLayoutData layoutData) {
		Composite swtParent = ((SwtEditorComposite) parent).getWidget();
		PropertyEditor<?> propertyEditor = createEditor(swtParent, new PropertyEditorContext<>(this, property));
		propertyEditor.getComposite().setLayoutData(SwtEditorUi.transformLayoutData(layoutData));
		Composite body = propertyEditor.getBody();
		EditorComposite editorBody = SwtEditorWidget.getEditorWidget(body);
		return editorBody == null ? new SwtEditorComposite(body) : editorBody;
	}

	@Override
	public EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property) {
		SwtEditorLabel editorLabel = (SwtEditorLabel) parent.getUiFactory().createLabel(parent);
		Label label = editorLabel.getWidget();
		label.setAlignment(SWT.RIGHT);
		label.setFont(createFont(label, SWT.BOLD));
		GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(labelLayoutData);
		return editorLabel;
	}

	@Override
	public EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property, EditorLayoutData layoutData) {
		SwtEditorLabel editorLabel = (SwtEditorLabel) parent.getUiFactory().createLabel(parent);
		editorLabel.setLayoutData(layoutData);
		return editorLabel;
	}
}
