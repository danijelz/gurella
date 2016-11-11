package com.gurella.studio.editor.common.bean;

import static java.lang.Integer.compare;

import java.util.Arrays;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.common.property.PropertyEditorData;

public class DefaultBeanEditor<T> extends CustomizableBeanEditor<T> {
	public DefaultBeanEditor(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new BeanEditorContext<>(sceneEditorContext, modelInstance));
	}

	public DefaultBeanEditor(Composite parent, BeanEditorContext<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		GridLayoutFactory.swtDefaults().numColumns(2).margins(1, 1).spacing(5, 2).applyTo(this);
		Property<?>[] array = context.model.getProperties().toArray(Property.class);
		if (array.length == 0) {
			return;
		}

		Arrays.stream(array).filter(p -> p.isEditable())
				.sorted((p0, p1) -> compare(getPropertyIndex(p0), getPropertyIndex(p1)))
				.forEach(p -> createPropertyControls(p, true));
		layout(true, true);
	}

	private int getPropertyIndex(Property<?> property) {
		return PropertyEditorData.getIndex(context, property);
	}

	private boolean isGroupExpanded(String groupName) {
		T modelInstance = context.modelInstance;
		if (modelInstance instanceof ManagedObject) {
			ManagedObject managedObject = (ManagedObject) modelInstance;
			return context.sceneEditorContext.getSceneBooleanPreference(groupName, managedObject.ensureUuid(), true);
		} else {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
