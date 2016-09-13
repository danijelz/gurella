package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorExpandableComposite;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.studio.editor.model.extension.event.SwtEditorEvent;

public abstract class SwtEditorBaseExpandableComposite<T extends ExpandableComposite>
		extends SwtEditorLayoutComposite<T> implements EditorExpandableComposite {
	SwtEditorBaseExpandableComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorControl<?> getClient() {
		return getEditorWidget(widget.getClient());
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public SwtEditorControl<?> getTextClient() {
		return getEditorWidget(widget.getTextClient());
	}

	@Override
	public int getTextClientHeightDifference() {
		return widget.getTextClientHeightDifference();
	}

	@Override
	public Color getTitleBarForeground() {
		return toGdxColor(widget.getTitleBarForeground());
	}

	@Override
	public boolean isExpanded() {
		return widget.isExpanded();
	}

	@Override
	public void setActiveToggleColor(Color color) {
		widget.setActiveToggleColor(toSwtColor(color));
	}

	@Override
	public void setActiveToggleColor(int r, int g, int b, int a) {
		widget.setActiveToggleColor(toSwtColor(r, g, b, a));
	}

	@Override
	public void setClient(EditorControl client) {
		widget.setClient(client == null ? null : ((SwtEditorControl<?>) client).widget);
	}

	@Override
	public void setExpanded(boolean expanded) {
		widget.setExpanded(expanded);
	}

	@Override
	public void setText(String title) {
		widget.setText(title);
	}

	@Override
	public void setTextClient(EditorControl textClient) {
		widget.setTextClient(textClient == null ? null : ((SwtEditorControl<?>) textClient).widget);
	}

	@Override
	public void setTitleBarForeground(Color color) {
		widget.setTitleBarForeground(toSwtColor(color));
	}

	@Override
	public void setTitleBarForeground(int r, int g, int b, int a) {
		widget.setTitleBarForeground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setToggleColor(Color color) {
		widget.setToggleColor(toSwtColor(color));
	}

	@Override
	public void setToggleColor(int r, int g, int b, int a) {
		widget.setToggleColor(toSwtColor(r, g, b, a));
	}

	@Override
	public void addListener(EditorEventType eventType, EditorEventListener listener) {
		if (listener == null) {
			return;
		}

		if (EditorEventType.Expand == eventType) {
			widget.addExpansionListener(new SwtExpandListenerBridge(listener));
		} else if (EditorEventType.Collapse == eventType) {
			widget.addExpansionListener(new SwtCollapseListenerBridge(listener));
		} else {
			super.addListener(eventType, listener);
		}
	}

	@Override
	public void removeListener(EditorEventType eventType, EditorEventListener listener) {
		if (listener == null) {
			return;
		}

		if (EditorEventType.Expand == eventType) {
			widget.removeExpansionListener(new SwtExpandListenerBridge(listener));
		} else if (EditorEventType.Collapse == eventType) {
			widget.addExpansionListener(new SwtCollapseListenerBridge(listener));
		} else {
			super.removeListener(eventType, listener);
		}
	}

	public class SwtExpandListenerBridge extends ExpansionAdapter {
		public final EditorEventListener listener;

		public SwtExpandListenerBridge(EditorEventListener listener) {
			this.listener = listener;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent expansionEvent) {
			if (expansionEvent.getState()) {
				return;
			}

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

			SwtExpandListenerBridge other = cast(obj);
			return listener.equals(other.listener);
		}
	}

	public class SwtCollapseListenerBridge extends ExpansionAdapter {
		public final EditorEventListener listener;

		public SwtCollapseListenerBridge(EditorEventListener listener) {
			this.listener = listener;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent expansionEvent) {
			if (!expansionEvent.getState()) {
				return;
			}

			Event event = new Event();
			event.widget = widget;
			event.type = SWT.Collapse;
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

			SwtCollapseListenerBridge other = cast(obj);
			return listener.equals(other.listener);
		}
	}
}
