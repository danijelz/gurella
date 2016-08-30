package com.gurella.studio.editor.model.extension.style;

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorControl.ControlStyle;
import com.gurella.engine.editor.ui.EditorScrollable.ScrollableStyle;
import com.gurella.engine.editor.ui.EditorToolBar.ToolBarStyle;
import com.gurella.engine.editor.ui.style.WidgetStyle;
import com.gurella.engine.utils.Reflection;

public class SwtWidgetStyle {
	private static final ObjectIntMap<WidgetStyle<?>> styleToSwtMap = new ObjectIntMap<>();

	static {
		Class<SWT> swtClass = SWT.class;
		HashMap<String, Integer> swtValuesByName = new HashMap<>();
		Arrays.stream(Reflection.getDeclaredFields(swtClass)).filter(f -> int.class.equals(f.getType()))
				.forEach(f -> swtValuesByName.put(f.getName(), (Integer) Reflection.getFieldValue(f, null)));

		Class<?>[] styleClasses = WidgetStyle.class.getClasses();
		Arrays.stream(styleClasses).filter(c -> WidgetStyle.class.isAssignableFrom(c))
				.forEach(c -> Arrays.stream(Reflection.getDeclaredFields(c))
						.filter(f -> WidgetStyle.class.isAssignableFrom(f.getType())).forEach(f -> styleToSwtMap
								.put(Reflection.getFieldValue(f, null), swtValuesByName.get(f.getName()).intValue())));
	}

	public static int getSwtStyle(WidgetStyle<?> style) {
		return styleToSwtMap.get(style, SWT.DEFAULT);
	}

	public static int getSwtStyle(WidgetStyle<?>... styles) {
		int result = 0;
		for (WidgetStyle<?> style : styles) {
			result |= getSwtStyle(style);
		}
		return result;
	}

	public static int getSwtStyle(int initialStyle, WidgetStyle<?>... styles) {
		int result = initialStyle;
		for (WidgetStyle<?> style : styles) {
			result |= getSwtStyle(style);
		}
		return result;
	}

	private static int extractControlStyle(ControlStyle style) {
		int result = 0;
		if (style.textDirection != null) {
			result |= style.textDirection == Direction.leftToRight ? SWT.LEFT_TO_RIGHT : SWT.RIGHT_TO_LEFT;
		}

		if (style.border) {
			result |= SWT.BORDER;
		}

		if (style.flipTextDirection) {
			result |= SWT.FLIP_TEXT_DIRECTION;
		}

		return result;
	}

	private static int extractScrollableStyle(ScrollableStyle style) {
		int result = extractControlStyle(style);

		if (style.hScroll) {
			result |= SWT.H_SCROLL;
		}

		if (style.vScroll) {
			result |= SWT.V_SCROLL;
		}

		return result;
	}

	public static int extractToolBarStyle(ToolBarStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		if (style.right) {
			result |= SWT.RIGHT;
		}

		if (style.flat) {
			result |= SWT.FLAT;
		}

		if (style.shadowOut) {
			result |= SWT.SHADOW_OUT;
		}

		return result;
	}
}
