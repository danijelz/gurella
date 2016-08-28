package com.gurella.studio.editor.model.extension.style;

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.utils.ObjectIntMap;
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
}
