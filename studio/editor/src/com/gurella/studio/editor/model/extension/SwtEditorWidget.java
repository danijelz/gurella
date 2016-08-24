package com.gurella.studio.editor.model.extension;

import static com.gurella.studio.editor.model.extension.event.SwtEditorEventType.toSwtConstant;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.editor.ui.EditorWidget;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.extension.event.SwtListenerBridge;

public abstract class SwtEditorWidget<T extends Widget> implements EditorWidget {
	static final IdentityMap<Widget, EditorWidget> instances = new IdentityMap<>();

	T widget;

	SwtEditorWidget() {
	}

	public SwtEditorWidget(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		init(createWidget(parent.widget, toolkit));
	}

	void init(T widget) {
		this.widget = widget;
		widget.addListener(SWT.Dispose, e -> instances.remove(widget));
		instances.put(widget, this);
	}

	abstract T createWidget(Composite parent, FormToolkit toolkit);
	
	public T getWidget() {
		return widget;
	}

	public static <T extends EditorWidget> T getEditorWidget(Widget widget) {
		return Values.cast(instances.get(widget));
	}

	@Override
	public void addListener(EditorEventType eventType, EditorEventListener listener) {
		widget.addListener(toSwtConstant(eventType), new SwtListenerBridge(listener));
	}

	@Override
	public EditorEventListener[] getListeners(EditorEventType eventType) {
		Listener[] listeners = widget.getListeners(toSwtConstant(eventType));
		return Arrays.stream(listeners).filter(l -> l instanceof SwtListenerBridge)
				.map(l -> ((SwtListenerBridge) l).listener).toArray(i -> new EditorEventListener[i]);
	}

	@Override
	public void removeListener(EditorEventType eventType, EditorEventListener listener) {
		int swtEvent = toSwtConstant(eventType);
		Listener[] listeners = widget.getListeners(swtEvent);
		Arrays.stream(listeners).filter(l -> equalsBridge(listener, l)).findFirst()
				.ifPresent(l -> widget.removeListener(swtEvent, l));
	}

	private static boolean equalsBridge(EditorEventListener listener, Listener l) {
		return l instanceof SwtListenerBridge && ((SwtListenerBridge) l).listener == listener;
	}

	@Override
	public boolean isListening(EditorEventType eventType) {
		return widget.isListening(toSwtConstant(eventType));
	}

	@Override
	public boolean isDisposed() {
		return widget.isDisposed();
	}

	@Override
	public void dispose() {
		widget.dispose();
	}

	@Override
	public <V> V getData(String key) {
		return Values.cast(widget.getData(key));
	}

	@Override
	public void setData(String key, Object value) {
		widget.setData(key, value);
	}
}
