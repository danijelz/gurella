package com.gurella.studio.editor.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.studio.editor.SceneEditorContext;

public abstract class InspectableContainer<T> extends ScrolledForm {
	protected final SceneEditorContext editorContext;
	protected final T target;

	public InspectableContainer(InspectorView parent, T target) {
		super(parent.getContent(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		editorContext = parent.context;
		this.target = target;
		setExpandHorizontal(true);
		setExpandVertical(true);
		setMinWidth(200);
		setSize(200, 100);

		addListener(SWT.Resize, (e) -> reflow(true));
	}
}