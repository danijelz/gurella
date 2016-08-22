package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.editor.EditorComposite;
import com.gurella.engine.editor.EditorControl;
import com.gurella.engine.utils.Values;

public abstract class SwtEditorControl<T extends Control> implements EditorControl {
	static final IdentityMap<Control, EditorControl> instances = new IdentityMap<>();

	T control;

	SwtEditorControl() {
	}

	public SwtEditorControl(SwtEditorComposite parent, FormToolkit toolkit) {
		control = createControl(parent.control, toolkit);
		control.addListener(SWT.Dispose, e -> instances.remove(control));
		instances.put(control, this);
	}

	abstract T createControl(Composite parent, FormToolkit toolkit);

	@Override
	public EditorComposite getParent() {
		Composite parent = control.getParent();
		return (EditorComposite) instances.get(parent);
	}

	@Override
	public boolean isDisposed() {
		return control.isDisposed();
	}

	@Override
	public void dispose() {
		control.dispose();
	}

	@Override
	public <V> V getData(String key) {
		return Values.cast(control.getData(key));
	}

	@Override
	public void setData(String key, Object value) {
		control.setData(key, value);
	}
}
