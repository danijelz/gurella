package com.gurella.studio.editor.engine.property;

import static com.gurella.studio.editor.engine.ui.SwtEditorUi.createComposite;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.ui.property.PropertyEditor;
import com.gurella.studio.editor.ui.property.PropertyEditorContext;
import com.gurella.studio.editor.utils.UiUtils;

public class CustomPropertyEditor<P> extends PropertyEditor<P> {
	private PropertyEditorFactory<P> factory;

	public CustomPropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		this.factory = factory;
		buildUi();
	}

	private void buildUi() {
		factory.buildUi(createComposite(content), new CustomPropertyEditorContextAdapter<P>(context, this));
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true);
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
