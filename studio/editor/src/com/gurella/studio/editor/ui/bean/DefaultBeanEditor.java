package com.gurella.studio.editor.ui.bean;

import static com.gurella.studio.editor.ui.property.PropertyEditorData.compare;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.metatype.Property;

public class DefaultBeanEditor<T> extends CustomizableBeanEditor<T> {
	public DefaultBeanEditor(Composite parent, int gdxContextId, T bean) {
		this(parent, new BeanEditorContext<>(gdxContextId, bean));
	}

	public DefaultBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
		Property<?>[] properties = context.metaType.getProperties().toArray(Property.class);
		Arrays.stream(properties).filter(p -> p.isEditable()).sorted((p1, p2) -> compare(context, p1, p2))
				.forEachOrdered(p -> createPropertyControls(p, true));
		layout(true, true);
	}
}
