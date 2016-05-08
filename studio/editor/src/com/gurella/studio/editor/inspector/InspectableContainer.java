package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.EditorMessageListener;
import com.gurella.studio.editor.GurellaSceneEditor;
import com.gurella.studio.editor.SceneEditorContext;

public abstract class InspectableContainer<T> extends ScrolledForm implements EditorMessageListener {
	protected T target;

	public InspectableContainer(InspectorView parent, T target) {
		super(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.target = target;
		setExpandHorizontal(true);
		setExpandVertical(true);
		setMinWidth(200);
		addListener(SWT.Resize, (e) -> reflow(true));
		addDisposeListener(e -> parent.removeEditorMessageListener(this));
		parent.addEditorMessageListener(this);
	}

	@Override
	public InspectorView getParent() {
		return (InspectorView) super.getParent();
	}

	@Override
	public boolean setParent(Composite parent) {
		if (parent instanceof InspectorView) {
			return super.setParent(parent);
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected final void postMessage(Object message) {
		getParent().postMessage(message);
	}

	@Override
	public void handleMessage(Object source, Object message) {
	}

	public GurellaSceneEditor getSceneEditor() {
		return getParent().getSceneEditor();
	}

	public SceneEditorContext getEditorContext() {
		return getSceneEditor().getContext();
	}
}