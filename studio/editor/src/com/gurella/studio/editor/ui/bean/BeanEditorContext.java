package com.gurella.studio.editor.ui.bean;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaProject;

import com.gurella.engine.event.Signal1;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.Property;
import com.gurella.studio.gdx.GdxContext;

public class BeanEditorContext<T> {
	public final int gdxContextId;

	public final BeanEditorContext<?> parent;
	public final MetaType<T> metaType;
	public final T bean;

	public final IJavaProject javaProject;

	public final Signal1<PropertyValueChangedEvent> propertiesSignal = new Signal1<>();

	public BeanEditorContext(int gdxContextId, T bean) {
		this(gdxContextId, null, MetaTypes.getMetaType(bean), bean);
	}

	public BeanEditorContext(BeanEditorContext<?> parent, T bean) {
		this(parent.gdxContextId, parent, MetaTypes.getMetaType(bean), bean);
	}

	public BeanEditorContext(int gdxContextId, BeanEditorContext<?> parent, MetaType<T> metaType, T bean) {
		this.gdxContextId = gdxContextId;
		this.javaProject = GdxContext.getJavaProject(gdxContextId);
		this.parent = parent;
		this.metaType = metaType;
		this.bean = bean;
		Optional.ofNullable(parent).ifPresent(p -> propertiesSignal.addListener(p.propertiesSignal::dispatch));
	}

	public void propertyValueChanged(Property<?> property, Object oldValue, Object newValue) {
		propertiesSignal.dispatch(new PropertyValueChangedEvent(metaType, property, bean, oldValue, newValue));
	}

	public String getQualifiedName() {
		return metaType.getType().getName();
	}

	public BeanEditorContext<?> getRoot() {
		return parent == null ? this : parent.getRoot();
	}

	public static final class PropertyValueChangedEvent {
		public final MetaType<?> metaType;
		public final Property<?> property;
		public final Object bean;
		public final Object oldValue;
		public final Object newValue;

		public PropertyValueChangedEvent(MetaType<?> metaType, Property<?> property, Object bean, Object oldValue,
				Object newValue) {
			this.metaType = metaType;
			this.property = property;
			this.bean = bean;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}
