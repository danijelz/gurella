package com.gurella.engine.scene.renderable;

import static com.gurella.engine.editor.ui.event.EditorEventType.Selection;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.doubleTouch;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.drag;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.longPress;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.mouseMove;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.scroll;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.tap;
import static com.gurella.engine.scene.renderable.RenderableComponentInputEventsSensitivity.touch;

import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.layout.EditorLayout;

class InputEventsPropertyEditorFactory implements PropertyEditorFactory<Byte> {
	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
		new EditorLayout().numColumns(4).columnsEqualWidth(true).applyTo(parent);

		createLabel(parent, "tap");
		createLabel(parent, "touch");
		createLabel(parent, "drag");
		createLabel(parent, "scroll");

		createCheck(context, parent, tap);
		createCheck(context, parent, touch);
		createCheck(context, parent, drag);
		createCheck(context, parent, scroll);

		createLabel(parent, "long\npress");
		createLabel(parent, "double\ntouch");
		createLabel(parent, "mouse\nmove");
		createLabel(parent, " ");

		createCheck(context, parent, longPress);
		createCheck(context, parent, doubleTouch);
		createCheck(context, parent, mouseMove);
	}

	private static void createLabel(EditorComposite parent, String text) {
		EditorUi uiFactory = parent.getUiFactory();
		uiFactory.createLabel(parent, text);
	}

	private static void createCheck(PropertyEditorContext<Byte> context, EditorComposite parent,
			RenderableComponentInputEventsSensitivity index) {
		EditorUi uiFactory = parent.getUiFactory();
		EditorButton check = uiFactory.createCheckBox(parent);
		byte byteValue = context.getPropertyValue().byteValue();
		check.setSelection((byteValue & (1 << index.ordinal())) != 0);
		check.addListener(Selection, new InputEventsSelectionListener(context, check, index));
	}

	private static class InputEventsSelectionListener implements EditorEventListener {
		private PropertyEditorContext<Byte> contex;
		private EditorButton check;
		private RenderableComponentInputEventsSensitivity index;

		public InputEventsSelectionListener(PropertyEditorContext<Byte> contex, EditorButton check,
				RenderableComponentInputEventsSensitivity index) {
			this.contex = contex;
			this.check = check;
			this.index = index;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			byte byteValue = contex.getPropertyValue().byteValue();
			if (check.getSelection()) {
				byteValue = (byte) (byteValue | (1 << index.ordinal()));
			} else {
				byteValue = (byte) (byteValue & ~(1 << index.ordinal()));
			}
			contex.setPropertyValue(Byte.valueOf(byteValue));
		}
	}
}
