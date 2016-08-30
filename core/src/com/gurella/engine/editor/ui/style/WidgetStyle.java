package com.gurella.engine.editor.ui.style;

import java.util.concurrent.atomic.AtomicInteger;

import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorMenu;
import com.gurella.engine.editor.ui.EditorScrollable;
import com.gurella.engine.editor.ui.EditorWidget;

public abstract class WidgetStyle<T extends EditorWidget> {
	static AtomicInteger index = new AtomicInteger();

	static int nextId() {
		return index.getAndIncrement();
	}

	public final int baseId;
	public final int id;

	public WidgetStyle(int baseId) {
		this.baseId = baseId;
		this.id = nextId();
	}

	// Control

	static abstract class ControlStyle extends WidgetStyle<EditorControl> {
		ControlStyle(int baseId) {
			super(baseId);
		}
	}

	public static final class ControlBorder extends ControlStyle {
		public static ControlBorder BORDER = new ControlBorder();

		private ControlBorder() {
			super(nextId());
		}
	}

	public static final class ControlTextDirection extends ControlStyle {
		private static final int base = nextId();

		public static ControlTextDirection LEFT_TO_RIGHT = new ControlTextDirection();
		public static ControlTextDirection RIGHT_TO_LEFT = new ControlTextDirection();

		private ControlTextDirection() {
			super(base);
		}
	}

	public static final class ControlFlipTextDirection extends ControlStyle {
		public static ControlFlipTextDirection FLIP_TEXT_DIRECTION = new ControlFlipTextDirection();

		private ControlFlipTextDirection() {
			super(nextId());
		}
	}

	// Scrollable

	public static final class ScrollableBar extends WidgetStyle<EditorScrollable> {
		private static final int base = nextId();

		public static ScrollableBar H_SCROLL = new ScrollableBar();
		public static ScrollableBar V_SCROLL = new ScrollableBar();

		private ScrollableBar() {
			super(base);
		}
	}

	// Button

	static abstract class ButtonStyle extends WidgetStyle<EditorButton> {
		ButtonStyle(int baseId) {
			super(baseId);
		}
	}

	// TODO remove
	public static final class ButtonType extends ButtonStyle {
		private static final int base = nextId();

		public static ButtonType ARROW_UP = new ButtonType();
		public static ButtonType ARROW_DOWN = new ButtonType();
		public static ButtonType ARROW_LEFT = new ButtonType();
		public static ButtonType ARROW_RIGHT = new ButtonType();
		public static ButtonType CHECK = new ButtonType();
		public static ButtonType PUSH = new ButtonType();
		public static ButtonType RADIO = new ButtonType();
		public static ButtonType TOGGLE = new ButtonType();

		private ButtonType() {
			super(base);
		}
	}

	public static final class ButtonTextAlign extends ButtonStyle {
		private static final int base = nextId();

		public static ButtonTextAlign LEFT = new ButtonTextAlign();
		public static ButtonTextAlign RIGHT = new ButtonTextAlign();
		public static ButtonTextAlign CENTER = new ButtonTextAlign();

		private ButtonTextAlign() {
			super(base);
		}
	}

	// Label

	static abstract class LabelStyle extends WidgetStyle<EditorLabel> {
		LabelStyle(int baseId) {
			super(baseId);
		}
	}

	public static final class LabelOrientation extends LabelStyle {
		private static final int base = nextId();

		public static LabelOrientation HORIZONTAL = new LabelOrientation();
		public static LabelOrientation VERTICAL = new LabelOrientation();

		private LabelOrientation() {
			super(base);
		}
	}

	public static final class LabelShadow extends LabelStyle {
		private static final int base = nextId();

		public static LabelShadow SHADOW_IN = new LabelShadow();
		public static LabelShadow SHADOW_OUT = new LabelShadow();

		private LabelShadow() {
			super(base);
		}
	}

	public static final class LabelTextAlign extends LabelStyle {
		private static final int base = nextId();

		public static LabelTextAlign RIGHT = new LabelTextAlign();
		public static LabelTextAlign LEFT = new LabelTextAlign();
		public static LabelTextAlign CENTER = new LabelTextAlign();

		private LabelTextAlign() {
			super(base);
		}
	}

	public static final class LabelWrap extends LabelStyle {
		public static LabelWrap WRAP = new LabelWrap();

		private LabelWrap() {
			super(nextId());
		}
	}
}
