package com.gurella.studio.editor.model.extension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;

public abstract class SwtEditorBaseComposite<T extends Composite> extends SwtEditorScrollable<T>
		implements EditorComposite {
	public SwtEditorBaseComposite(T composite) {
		init(composite);
	}

	public SwtEditorBaseComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public List<EditorControl> getChildren() {
		return Arrays.<Control> stream(widget.getChildren()).map(c -> (EditorControl) instances.get(c))
				.collect(Collectors.toList());
	}

	@Override
	public void layout() {
		widget.layout(true, true);
	}

	@Override
	public EditorControl[] getTabList() {
		return Arrays.stream(widget.getTabList()).map(c -> getEditorWidget(c)).filter(ec -> ec != null)
				.toArray(i -> new EditorControl[i]);
	}

	@Override
	public void setTabList(EditorControl[] tabList) {
		widget.setTabList(
				Arrays.stream(tabList).map(c -> ((SwtEditorControl<?>) c).widget).toArray(i -> new Control[i]));
	}
}
