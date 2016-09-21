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
		createCheck(context, parent, "tap", (byte) 1);
		createCheck(context, parent, "touch", (byte) 2);
	}

	private static void createCheck(PropertyEditorContext<Byte> context, EditorComposite parent, String text,
			byte index) {
		EditorUi uiFactory = parent.getUiFactory();
		EditorButton check = uiFactory.createCheckBox(parent);
		check.setText(text);
		byte byteValue = context.getPropertyValue().byteValue();
		check.setSelection((byteValue & (1 << index)) != 0);
		check.addListener(Selection, new InputEventsSelectionListener(context, check, index));
	}

	private static class InputEventsSelectionListener implements EditorEventListener {
		private PropertyEditorContext<Byte> contex;
		private EditorButton check;
		private byte index;

		public InputEventsSelectionListener(PropertyEditorContext<Byte> contex, EditorButton check, byte index) {
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
