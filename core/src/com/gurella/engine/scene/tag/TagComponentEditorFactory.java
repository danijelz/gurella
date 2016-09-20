package com.gurella.engine.scene.tag;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.editor.model.ModelEditorContext;
import com.gurella.engine.editor.model.ModelEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorScrolledComposite;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment;

public class TagComponentEditorFactory implements ModelEditorFactory<TagComponent> {
	private static final Property<String[]> tagsProperty = Models.getModel(TagComponent.class).getProperty("tags");

	@Override
	public void buildUi(EditorComposite parent, final ModelEditorContext<TagComponent> context) {
		parent.setLayout(1);

		EditorUi uiFactory = context.getEditorUi();
		EditorScrolledComposite scrolledComposite = uiFactory.createScrolledComposite(parent);
		new EditorLayoutData().alignment(HorizontalAlignment.FILL, VerticalAlignment.FILL).applyTo(scrolledComposite);

		scrolledComposite.setLayout(1);
		EditorComposite content = uiFactory.createComposite(scrolledComposite);
		content.setSize(100, 100);
		content.setLayout(1);

		final TagComponent tagComponent = context.getModelInstance();
		String[] tags = tagComponent.getTags();

		for (int i = 0; i < tags.length; i++) {
			uiFactory.createLabel(content, tags[i]);
		}

		EditorButton addButton = uiFactory.createButton(content);
		addButton.setText("Add");
		addButton.addListener(EditorEventType.Selection, new EditorEventListener() {
			@Override
			public void handleEvent(EditorEvent event) {
				tagComponent.addTag(Tag.get("Ddd"));
				context.propertyValueChanged(tagsProperty, null, null);
			}
		});
	}
}
