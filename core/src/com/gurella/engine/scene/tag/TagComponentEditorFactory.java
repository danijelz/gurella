package com.gurella.engine.scene.tag;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.editor.model.ModelEditorContext;
import com.gurella.engine.editor.model.ModelEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorInputValidator.BlankTextValidator;
import com.gurella.engine.editor.ui.EditorScrolledComposite;
import com.gurella.engine.editor.ui.EditorScrolledComposite.ScrolledCompositeStyle;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment;

public class TagComponentEditorFactory implements ModelEditorFactory<TagComponent> {
	private static final BlankTextValidator tagNameValidator = new BlankTextValidator("Tag name must not be empty.");
	private static final Property<String[]> tagsProperty = Models.getModel(TagComponent.class).getProperty("tags");

	@Override
	public void buildUi(final EditorComposite parent, final ModelEditorContext<TagComponent> context) {
		parent.setLayout(1);

		final EditorUi uiFactory = context.getEditorUi();
		final EditorScrolledComposite scroll = uiFactory.createScrolledComposite(parent,
				new ScrolledCompositeStyle().scroll(false, false));
		new EditorLayoutData().alignment(HorizontalAlignment.FILL, VerticalAlignment.TOP).grab(true, false)
				.sizeHint(150, 150).applyTo(scroll);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		scroll.setMinSize(150, 150);
		scroll.setSize(150, 150);
		scroll.setBackground(200, 0, 0, 150);

		final EditorComposite content = uiFactory.createComposite(scroll);
		scroll.setContent(content);
		new EditorLayoutData().alignment(HorizontalAlignment.LEFT, VerticalAlignment.TOP).applyTo(content);
		content.setLayout(1);

		buildTagChecks(context, content);
		EditorButton addButton = uiFactory.createButton(parent);
		addButton.setText("Add");
		addButton.addListener(EditorEventType.Selection, new EditorEventListener() {
			@Override
			public void handleEvent(EditorEvent event) {
				String tagName = uiFactory.showInputDialog("Add tag", "Select new tag name", "", tagNameValidator);
				if (tagName != null) {
					context.getModelInstance().addTag(tagName);
					context.propertyValueChanged(tagsProperty, null, null);
					content.removeAllChildren();
					buildTagChecks(context, content);
					content.layout();
				}
			}
		});
	}

	protected void buildTagChecks(final ModelEditorContext<TagComponent> context, final EditorComposite content) {
		final EditorUi uiFactory = context.getEditorUi();
		final TagComponent tagComponent = context.getModelInstance();
		String[] tags = tagComponent.getTags();
		Arrays.sort(tags);

		Array<Tag> allTags = Tag.values();

		for (int i = 0; i < allTags.size; i++) {
			Tag tag = allTags.get(i);
			final String tagName = tag.name;
			final EditorButton checkBox = uiFactory.createCheckBox(content);
			new EditorLayoutData().alignment(HorizontalAlignment.FILL, VerticalAlignment.TOP).grab(true, false)
					.applyTo(checkBox);
			checkBox.setText(tagName);
			checkBox.setSelection(Arrays.binarySearch(tags, tag.name) > -1);
			checkBox.addListener(EditorEventType.Selection, new EditorEventListener() {
				@Override
				public void handleEvent(EditorEvent event) {
					if (checkBox.getSelection()) {
						tagComponent.addTag(tagName);
					} else {
						tagComponent.removeTag(tagName);
					}
					context.propertyValueChanged(tagsProperty, null, null);
				}
			});
		}
	}
}
