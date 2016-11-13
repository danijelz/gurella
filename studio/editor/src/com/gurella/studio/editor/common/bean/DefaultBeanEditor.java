package com.gurella.studio.editor.common.bean;

import static com.gurella.studio.editor.common.property.PropertyEditorData.compare;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.SceneEditorContext;

public class DefaultBeanEditor<T> extends CustomizableBeanEditor<T> {
	public DefaultBeanEditor(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new BeanEditorContext<>(sceneEditorContext, modelInstance));
	}

	public DefaultBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		super.createContent();
		Property<?>[] properties = context.model.getProperties().toArray(Property.class);
		Arrays.stream(properties).filter(p -> p.isEditable()).sorted((p1, p2) -> compare(context, p1, p2))
				.forEach(p -> createPropertyControls(p, true));
		layout(true, true);
	}
}
