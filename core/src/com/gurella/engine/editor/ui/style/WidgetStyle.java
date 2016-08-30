package com.gurella.engine.editor.ui.style;

import java.util.concurrent.atomic.AtomicInteger;

import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorScrollable;
import com.gurella.engine.editor.ui.EditorSpinner;
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

	// Label

	public static final class LabelStyle extends WidgetStyle<EditorLabel> {
		private static final int shadowBase = nextId();
		public static final LabelStyle SHADOW_IN = new LabelStyle(shadowBase);
		public static final LabelStyle SHADOW_OUT = new LabelStyle(shadowBase);

		private static final int alignBase = nextId();
		public static final LabelStyle RIGHT = new LabelStyle(alignBase);
		public static final LabelStyle LEFT = new LabelStyle(alignBase);
		public static final LabelStyle CENTER = new LabelStyle(alignBase);

		public static final LabelStyle WRAP = new LabelStyle(nextId());

		private LabelStyle(int baseId) {
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

	// Combo

	public static final class ComboStyle extends WidgetStyle<EditorCombo> {
		private static final int typeBase = nextId();

		public static final ComboStyle DROP_DOWN = new ComboStyle(typeBase);
		public static final ComboStyle SIMPLE = new ComboStyle(typeBase);

		public static final ComboStyle READ_ONLY = new ComboStyle(nextId());

		private ComboStyle(int baseId) {
			super(baseId);
		}
	}

	// Spinner

	public static final class SpinnerStyle extends WidgetStyle<EditorSpinner> {
		public static final SpinnerStyle READ_ONLY = new SpinnerStyle(nextId());
		public static final SpinnerStyle WRAP = new SpinnerStyle(nextId());

		private SpinnerStyle(int baseId) {
			super(baseId);
		}
	}
}
