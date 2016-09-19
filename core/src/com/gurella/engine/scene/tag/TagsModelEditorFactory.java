package com.gurella.engine.scene.tag;

import com.gurella.engine.editor.model.ModelEditorContext;
import com.gurella.engine.editor.model.ModelEditorFactory;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorScrolledComposite;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment;

//TODO
public class TagsModelEditorFactory implements ModelEditorFactory<TagComponent> {
	@Override
	public void buildUi(EditorComposite parent, ModelEditorContext<TagComponent> context) {
		EditorUi editorUi = context.getEditorUi();
		EditorScrolledComposite scrolledComposite = editorUi.createScrolledComposite(parent);
		new EditorLayoutData().alignment(HorizontalAlignment.FILL, VerticalAlignment.FILL).applyTo(scrolledComposite);
		scrolledComposite.setLayout(1);

		EditorComposite content = editorUi.createComposite(scrolledComposite);
		content.setSize(100, 150);
		TagComponent instance = context.getModelInstance();
	}
}
