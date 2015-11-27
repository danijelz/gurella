package com.gurella.studio.inspector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener1Event;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.ValueUtils;
import com.kotcrab.vis.ui.widget.VisTable;

public class InspectorContainer extends VisTable {
	private Actor spacer = new Actor();
	private InspectorPropertiesContainer inspectorPropertiesContainer;

	public InspectorContainer() {
		setWidth(100);
		EventService.addListener(PresentInspectableValueEvent.class, new PresentInspectableValueListener());
	}

	public void present(InspectorPropertiesContainer container) {
		clearSelection();
		inspectorPropertiesContainer = container;

		if (inspectorPropertiesContainer instanceof Actor) {
			add((Actor) inspectorPropertiesContainer).top().left().fill().expand();
		} else {
			return;
		}

		add(spacer).top().left().fill().expand();
	}

	private void clearSelection() {
		save();
		inspectorPropertiesContainer = null;
		clearChildren();
	}

	public void save() {
		if (inspectorPropertiesContainer != null) {
			inspectorPropertiesContainer.save();
		}
	}

	public static class PresentInspectableValueEvent extends Listener1Event<InspectableValue> {
		public PresentInspectableValueEvent(InspectableValue value) {
			super(value);
		}
	}

	private class PresentInspectableValueListener implements Listener1<InspectableValue> {
		private InspectableValue lastSelection;

		@Override
		public void handle(InspectableValue selection) {
			if (!ValueUtils.isEqual(lastSelection, selection)) {
				lastSelection = selection;
				if (selection == null) {
					clear();
				} else {
					present(selection.container);
				}
			}
		}
	}

	public static class InspectableValue {
		public InspectorPropertiesContainer container;

		public InspectableValue(InspectorPropertiesContainer container) {
			this.container = container;
		}

		@Override
		public int hashCode() {
			return container == null ? 0 : container.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InspectableValue other = (InspectableValue) obj;
			if (container == null) {
				if (other.container != null)
					return false;
			} else if (!container.equals(other.container))
				return false;
			return true;
		}
	}
}
