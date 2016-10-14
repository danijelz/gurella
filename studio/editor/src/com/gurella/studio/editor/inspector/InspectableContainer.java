package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.SceneEditorContext;

public abstract class InspectableContainer<T> extends ScrolledForm {
	protected T target;

	public InspectableContainer(InspectorView parent, T target) {
		super(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		this.target = target;
		setExpandHorizontal(true);
		setExpandVertical(true);
		setMinWidth(200);
		setSize(200, 100);

		addListener(SWT.Resize, (e) -> reflow(true));
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

	public SceneEditorContext getSceneEditorContext() {
		return getParent().getSceneEditor().getEditorContext();
	}
}