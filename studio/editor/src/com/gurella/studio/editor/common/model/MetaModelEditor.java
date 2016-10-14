package com.gurella.studio.editor.common.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.common.property.PropertyEditor;

public abstract class MetaModelEditor<T> extends Composite {
	ModelEditorContext<T> context;

	private List<PropertyEditor<?>> hoverEditors = new ArrayList<PropertyEditor<?>>();
	private List<PropertyEditor<?>> hoverEditorsTemp = new ArrayList<PropertyEditor<?>>();

	public MetaModelEditor(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new ModelEditorContext<>(sceneEditorContext, modelInstance));
	}

	public MetaModelEditor(Composite parent, ModelEditorContext<T> context) {
		super(parent, SWT.NONE);
		this.context = context;
		GurellaStudioPlugin.getToolkit().adapt(this);
		Listener mouseMoveListener = e -> mouseMoved();
		Display display = getDisplay();
		display.addFilter(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Dispose, (e) -> display.removeFilter(SWT.MouseMove, mouseMoveListener));
		createContent();
	}

	protected abstract void createContent();

	private void mouseMoved() {
		extractHoveredEditors();

		hoverEditors.stream().filter(e -> !hoverEditorsTemp.contains(e)).forEach(e -> e.setHover(false));
		hoverEditorsTemp.stream().filter(e -> !hoverEditors.contains(e)).forEach(e -> e.setHover(true));

		hoverEditors.clear();
		List<PropertyEditor<?>> temp = hoverEditorsTemp;
		hoverEditorsTemp = hoverEditors;
		hoverEditors = temp;
	}

	private void extractHoveredEditors() {
		Control cursorControl = getDisplay().getCursorControl();
		if (cursorControl == null) {
			return;
		}

		Composite parent = cursorControl.getParent();

		while (parent != null && parent != this) {
			PropertyEditor<?> editor = (PropertyEditor<?>) parent.getData(PropertyEditor.class.getName());
			if (editor != null) {
				hoverEditorsTemp.add(editor);
			}

			parent = parent.getParent();
		}

		if (this != parent) {
			hoverEditorsTemp.clear();
		}
	}

	public ModelEditorContext<T> getContext() {
		return context;
	}
}
