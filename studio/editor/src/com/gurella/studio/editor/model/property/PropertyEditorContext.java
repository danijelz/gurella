package com.gurella.studio.editor.model.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.base.model.PropertyChangeListener.PropertyChangeEvent;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ModelEditorContext;

public class PropertyEditorContext<M, P> extends ModelEditorContext<M> {
	public Property<P> property;
	public Supplier<P> valueExtractor;
	public Consumer<P> valueUpdater;

	public PropertyEditorContext(ModelEditorContext<M> parent, Property<P> property) {
		super(parent.sceneEditorContext, parent, parent.model, parent.modelInstance);
		this.property = property;
		valueExtractor = this::getValueDefault;
		valueUpdater = this::setValueDefault;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, M modelInstance, Property<P> property) {
		super(parent, modelInstance);
		this.property = property;
		valueExtractor = this::getValueDefault;
		valueUpdater = this::setValueDefault;
	}

	public PropertyEditorContext(ModelEditorContext<?> parent, Model<M> model, M modelInstance, Property<P> property) {
		super(parent.sceneEditorContext, parent, model, modelInstance);
		this.property = property;
		valueExtractor = this::getValueDefault;
		valueUpdater = this::setValueDefault;
	}

	protected P getValue() {
		return valueExtractor.get();
	}

	private P getValueDefault() {
		return property.getValue(modelInstance);
	}

	protected void setValue(P newValue) {
		valueUpdater.accept(newValue);
	}

	private void setValueDefault(P newValue) {
		P oldValue = getValue();
		if (!Values.isEqual(oldValue, newValue)) {
			property.setValue(modelInstance, newValue);
			propertyValueChanged(oldValue, newValue);
		}
	}

	public String getDescriptiveName() {
		return property.getDescriptiveName();
	}

	public void propertyValueChanged(Object oldValue, Object newValue) {
		signal.dispatch(new PropertyValueChangedEvent(model, property, modelInstance, oldValue, newValue));

		ModelEditorContext<?> temp = this;
		PropertyChangeEvent event = new PropertyChangeEvent();
		event.oldValue = oldValue;
		event.newValue = newValue;
		Array<Object> propertyPath = new Array<Object>();
		event.propertyPath = propertyPath;

		while (temp != null) {
			if (temp instanceof PropertyEditorContext) {
				propertyPath.insert(0, temp.modelInstance);
				if (temp.modelInstance instanceof PropertyChangeListener) {
					PropertyChangeListener listener = (PropertyChangeListener) temp.modelInstance;
					listener.propertyChanged(event);
				}
			}
			temp = temp.parent;
		}
	}
}
