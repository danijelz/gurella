package com.gurella.studio.editor.engine.property;

import static com.gurella.studio.editor.engine.ui.SwtEditorUi.createComposite;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.property.CompositePropertyEditor;
import com.gurella.studio.editor.property.PropertyEditorContext;

public class CustomCompositePropertyEditor<P> extends CompositePropertyEditor<P> {
	private PropertyEditorFactory<P> factory;

	public CustomCompositePropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		this.factory = factory;
		buildUi();
	}

	private void buildUi() {
		factory.buildUi(createComposite(body), new CustomPropertyEditorContextAdapter<P>(context, this));
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
		body.layout(true);
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
