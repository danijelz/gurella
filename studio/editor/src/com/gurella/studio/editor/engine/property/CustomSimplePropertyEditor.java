package com.gurella.studio.editor.engine.property;

import static com.gurella.studio.editor.engine.ui.SwtEditorUi.createComposite;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.ui.property.PropertyEditorContext;
import com.gurella.studio.editor.ui.property.SimplePropertyEditor;
import com.gurella.studio.editor.utils.UiUtils;

public class CustomSimplePropertyEditor<P> extends SimplePropertyEditor<P> {
	private PropertyEditorFactory<P> factory;

	public CustomSimplePropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		this.factory = factory;

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

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
