package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.event.SwtEditorEvent;
import com.gurella.studio.editor.model.extension.event.SwtListenerBridge;

public class SwtEditorExpandableComposite extends SwtEditorBaseExpandableComposite<ExpandableComposite> {
	SwtEditorExpandableComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	ExpandableComposite createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createSection(parent, style);
	}

	@Override
	public void addListener(EditorEventType eventType, EditorEventListener listener) {
		if (EditorEventType.Expand == eventType) {
			widget.addExpansionListener(new SwtExpansionListenerBridge(listener));
		} else {
			super.addListener(eventType, listener);
		}
	}

	@Override
	public void removeListener(EditorEventType eventType, EditorEventListener listener) {
		if (EditorEventType.Expand == eventType) {
			widget.removeExpansionListener(new SwtExpansionListenerBridge(listener));
		} else {
			super.removeListener(eventType, listener);
		}
	}

	public class SwtExpansionListenerBridge extends ExpansionAdapter {
		public final EditorEventListener listener;

		public SwtExpansionListenerBridge(EditorEventListener listener) {
			this.listener = listener;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent expansionEvent) {
			Event event = new Event();
			event.widget = widget;
			event.type = SWT.Expand;
			EditorEvent editorEvent = new SwtEditorEvent(event);
			listener.handleEvent(editorEvent);
		}

		@Override
		public int hashCode() {
			return 31 * listener.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}

			SwtExpansionListenerBridge other = (SwtExpansionListenerBridge) obj;
			return listener.equals(other.listener);
		}
	}
}
