package com.gurella.engine.editor.ui.style;

import java.util.concurrent.atomic.AtomicInteger;

import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorScrollable;
import com.gurella.engine.editor.ui.EditorText;
import com.gurella.engine.editor.ui.EditorWidget;

public abstract class WidgetStyle<T extends EditorWidget> {
	static AtomicInteger index = new AtomicInteger();

	static int nextId() {
		return index.getAndIncrement();
	}

	public final int baseId;

	public WidgetStyle(int baseId) {
		this.baseId = baseId;
	}

	// Control

	public static final class ControlStyle extends WidgetStyle<EditorControl> {
		private static final int textDirectionBase = nextId();
		public static final ControlStyle LEFT_TO_RIGHT = new ControlStyle(textDirectionBase);
		public static final ControlStyle RIGHT_TO_LEFT = new ControlStyle(textDirectionBase);

		public static final ControlStyle BORDER = new ControlStyle(nextId());

		public static final ControlStyle FLIP_TEXT_DIRECTION = new ControlStyle(nextId());

		private ControlStyle(int baseId) {
			super(baseId);
		}
	}

	// Scrollable

	public static final class ScrollableStyle extends WidgetStyle<EditorScrollable> {
		public static final ScrollableStyle H_SCROLL = new ScrollableStyle();
		public static final ScrollableStyle V_SCROLL = new ScrollableStyle();

		private ScrollableStyle() {
			super(nextId());
		}
	}

	// Button

	public static final class ButtonStyle extends WidgetStyle<EditorButton> {
		private static final int alignBase = nextId();
		public static final ButtonStyle RIGHT = new ButtonStyle(alignBase);
		public static final ButtonStyle LEFT = new ButtonStyle(alignBase);
		public static final ButtonStyle CENTER = new ButtonStyle(alignBase);

		public static final ButtonStyle FLAT = new ButtonStyle(nextId());
		public static final ButtonStyle WRAP = new ButtonStyle(nextId());

		private ButtonStyle(int baseId) {
			super(baseId);
		}
	}

	// Text

	public static final class TextStyle extends WidgetStyle<EditorText> {
		private static final int alignBase = nextId();

		public static final TextStyle RIGHT = new TextStyle(alignBase);
		public static final TextStyle LEFT = new TextStyle(alignBase);
		public static final TextStyle CENTER = new TextStyle(alignBase);

		public static final TextStyle WRAP = new TextStyle(nextId());

		public static final TextStyle READ_ONLY = new TextStyle(nextId());

		public static final TextStyle PASSWORD = new TextStyle(nextId());

		private TextStyle(int baseId) {
			super(baseId);
		}
	}
}
