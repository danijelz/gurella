package com.gurella.engine.scene.tag;

import static com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment.FILL;
import static com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment.LEFT;
import static com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment.TOP;

import java.util.Arrays;

import com.badlogic.gdx.math.MathUtils;
import com.gurella.engine.editor.model.ModelEditorContext;
import com.gurella.engine.editor.model.ModelEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorInputValidator.BlankTextValidator;
import com.gurella.engine.editor.ui.EditorScrolledForm;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.metatype.Models;
import com.gurella.engine.metatype.Property;

public class TagComponentEditorFactory implements ModelEditorFactory<TagComponent> {
	private static final BlankTextValidator tagNameValidator = new BlankTextValidator("Empty name in invalid.");
	private static final Property<String[]> tagsProperty = Models.getModel(TagComponent.class).getProperty("tags");

	@Override
	public void buildUi(final EditorComposite parent, final ModelEditorContext<TagComponent> context) {
		parent.setLayout(1);
		final EditorUi uiFactory = context.getEditorUi();

		final EditorScrolledForm scroll = uiFactory.createScrolledForm(parent);
		new EditorLayoutData().alignment(LEFT, TOP).applyTo(scroll);
		scroll.setLayoutData(createScrollLayoutData());
		scroll.setExpandHorizontal(true);

		final EditorComposite content = scroll.getForm().getBody();
		content.setLayout(1);

		buildTagComponents(context, content);
		EditorButton addButton = uiFactory.createButton(parent);
		new EditorLayoutData().alignment(LEFT, TOP).applyTo(addButton);
		addButton.setText("Add");
		addButton.addListener(EditorEventType.Selection, new AddButtonSelectionListener(scroll, context));
		scroll.reflow();
	}

	private static EditorLayoutData createScrollLayoutData() {
		int size = MathUtils.clamp(Tag.values().size(), 1, 7);
		return new EditorLayoutData().alignment(FILL, TOP).grab(true, false).minSize(150, 10).sizeHint(150, size * 21);
	}

	private static void buildTagComponents(final ModelEditorContext<TagComponent> context,
			final EditorComposite parent) {
		final TagComponent tagComponent = context.getModelInstance();
		String[] tags = tagComponent.getTags();
		Arrays.sort(tags);

		Tag[] allTags = Tag.values().toArray(Tag.class);
		Arrays.sort(allTags);

		for (int i = 0; i < allTags.length; i++) {
			buildTagComponent(context, parent, tags, allTags[i]);
		}
	}

	protected static void buildTagComponent(final ModelEditorContext<TagComponent> context,
			final EditorComposite parent, String[] tags, Tag tag) {
		final EditorButton checkBox = context.getEditorUi().createCheckBox(parent);
		new EditorLayoutData().alignment(FILL, TOP).grab(true, false).applyTo(checkBox);
		checkBox.setText(tag.name);
		checkBox.setSelection(Arrays.binarySearch(tags, tag.name) > -1);
		checkBox.addListener(EditorEventType.Selection, new TagSelectionListener(context, checkBox));
	}

	private static final class AddButtonSelectionListener implements EditorEventListener {
		private final EditorScrolledForm scroll;
		private final ModelEditorContext<TagComponent> context;

		private AddButtonSelectionListener(EditorScrolledForm scroll, ModelEditorContext<TagComponent> context) {
			this.scroll = scroll;
			this.context = context;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			String tagName = context.getEditorUi().showInputDialog("Add tag", "Select new tag name", "",
					tagNameValidator);
			if (tagName == null) {
				return;
			}

			EditorComposite content = scroll.getForm().getBody();
			context.getModelInstance().addTag(tagName);
			context.propertyValueChanged(tagsProperty, null, null);
			content.disposeAllChildren();
			buildTagComponents(context, content);
			scroll.setLayoutData(createScrollLayoutData());
			scroll.reflow();
		}
	}

	private static final class TagSelectionListener implements EditorEventListener {
		private final ModelEditorContext<TagComponent> context;
		private final EditorButton checkBox;

		private TagSelectionListener(ModelEditorContext<TagComponent> context, EditorButton checkBox) {
			this.context = context;
			this.checkBox = checkBox;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			TagComponent tagComponent = context.getModelInstance();
			String tagName = checkBox.getText();
			if (checkBox.getSelection()) {
				tagComponent.addTag(tagName);
			} else {
				tagComponent.removeTag(tagName);
			}
			context.propertyValueChanged(tagsProperty, null, null);
		}
	}
}
