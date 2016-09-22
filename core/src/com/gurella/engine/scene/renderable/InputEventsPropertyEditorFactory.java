package com.gurella.engine.scene.renderable;

import static com.gurella.engine.editor.ui.event.EditorEventType.Selection;

import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;

class InputEventsPropertyEditorFactory implements PropertyEditorFactory<Byte> {
	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
		parent.setLayout(7);

		createLabel(parent, "tap");
		createLabel(parent, "touch");
		createLabel(parent, "dbl. touch");
		createLabel(parent, "long press");
		createLabel(parent, "mouse move");
		createLabel(parent, "scroll");
		createLabel(parent, "drag");

		for (int i = 0; i < 7; i++) {
			createCheck(context, parent, i);
		}
	}

	private static void createLabel(EditorComposite parent, String text) {
		EditorUi uiFactory = parent.getUiFactory();
		uiFactory.createLabel(parent, text);
	}

	private static void createCheck(PropertyEditorContext<Byte> context, EditorComposite parent, int index) {
		EditorUi uiFactory = parent.getUiFactory();
		EditorButton check = uiFactory.createCheckBox(parent);
		byte byteValue = context.getPropertyValue().byteValue();
		check.setSelection((byteValue & (1 << index)) != 0);
		check.addListener(Selection, new InputEventsSelectionListener(context, check, index));
	}

	private static class InputEventsSelectionListener implements EditorEventListener {
		private PropertyEditorContext<Byte> contex;
		private EditorButton check;
		private int index;

		public InputEventsSelectionListener(PropertyEditorContext<Byte> contex, EditorButton check, int index) {
			this.contex = contex;
			this.check = check;
			this.index = index;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			byte byteValue = contex.getPropertyValue().byteValue();
			if (check.getSelection()) {
				byteValue = (byte) (byteValue | (1 << index));
			} else {
				byteValue = (byte) (byteValue & ~(1 << index));
			}
			contex.setPropertyValue(Byte.valueOf(byteValue));
		}
	}
}
