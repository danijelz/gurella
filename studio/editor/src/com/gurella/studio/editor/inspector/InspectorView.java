package com.gurella.studio.editor.inspector;

import java.util.Optional;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.inspector.Inspectable.EmptyInspectable;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class InspectorView extends DockableView implements EditorSelectionListener {
	private Object target;
	private InspectableContainer<?> content;

	public InspectorView(SceneEditorContext context) {
		super(context);
	}

	@Override
	protected void initControl(Composite control) {
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(control);
		GurellaStudioPlugin.getToolkit().adapt(control);
		control.addDisposeListener(e -> GdxContext.unsubscribe(editorId, editorId, this));
		GdxContext.subscribe(editorId, editorId, this);
		presentInspectable(EmptyInspectable.getInstance());
	}

	@Override
	protected String getTitle() {
		return "Inspector";
	}

	@Override
	protected Image getImage() {
		return GurellaStudioPlugin.getImage("icons/showproperties_obj.gif");
	}

	@Override
	public void selectionChanged(Object selection) {
		Optional.of(toInspectable(selection)).filter(i -> Values.isNotEqual(target, i.getTarget(), true))
				.ifPresent(i -> Try.run(() -> presentInspectable(i), this::presentException));
	}

	private static Inspectable<Object> toInspectable(Object selection) {
		return Optional.ofNullable(selection).filter(Inspectable.class::isInstance)
				.map(s -> Values.<Inspectable<Object>> cast(s)).orElse(EmptyInspectable.getInstance());
	}

	private <T> void presentInspectable(Inspectable<T> inspectable) {
		Optional.ofNullable(content).ifPresent(c -> c.dispose());
		target = inspectable.getTarget();
		content = inspectable.createControl(this);
		layoutContent();
	}

	private void layoutContent() {
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(content);
		layout();
	}

	private void presentException(Throwable e) {
		UiUtils.disposeChildren(getContent());
		target = e;
		content = new ErrorInspectableContainer(this, e);
		layoutContent();
	}
}
