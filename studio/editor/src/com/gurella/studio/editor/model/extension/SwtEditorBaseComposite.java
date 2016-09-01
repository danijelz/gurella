package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.gurella.engine.editor.ui.EditorBaseComposite;
import com.gurella.engine.editor.ui.EditorControl;

public abstract class SwtEditorBaseComposite<T extends Composite> extends SwtEditorScrollable<T>
		implements EditorBaseComposite {
	public SwtEditorBaseComposite() {
	}

	public SwtEditorBaseComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorControl<?>[] getChildren() {
		return Arrays.<Control> stream(widget.getChildren()).sequential().map(c -> (EditorControl) instances.get(c))
				.toArray(i -> new SwtEditorControl<?>[i]);
	}

	@Override
	public void layout() {
		widget.layout(true, true);
	}

	@Override
	public EditorControl[] getTabList() {
		return Arrays.stream(widget.getTabList()).sequential().map(c -> getEditorWidget(c)).filter(ec -> ec != null)
				.toArray(i -> new EditorControl[i]);
	}

	@Override
	public void setTabList(EditorControl[] tabList) {
		widget.setTabList(Arrays.stream(tabList).sequential().map(c -> ((SwtEditorControl<?>) c).widget)
				.toArray(i -> new Control[i]));
	}
}
