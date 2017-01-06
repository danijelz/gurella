package com.gurella.studio.editor.inspector;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;

import com.gurella.engine.event.EventService;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.utils.UiUtils;

public class InspectorView extends DockableView implements EditorSelectionListener {
	private Object target;
	private InspectableContainer<?> content;

	public InspectorView(Dock dock, SceneEditorContext context, int style) {
		super(dock, context, style | SWT.BORDER);
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(this);
		GurellaStudioPlugin.getToolkit().adapt(this);
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);
	}

	@Override
	protected String getTitle() {
		return "Inspector";
	}

	@Override
	protected Image getImage() {
		return GurellaStudioPlugin.getImage("icons/showproperties_obj.gif");
	}

	private <T> void presentInspectable(Inspectable<T> inspectable) {
		if (Values.isNotEqual(target, inspectable.getTarget(), true)) {
			try {
				replaceInspectable(inspectable);
			} catch (Exception e) {
				UiUtils.disposeChildren(this);
				target = null;
				content = new ErrorInspectableContainer(this, e);
				content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				layout(true, true);
			}
		}
	}

	private <T> void replaceInspectable(Inspectable<T> inspectable) {
		clearCurrentSelection();
		target = inspectable.getTarget();
		if (target == null) {
			return;
		}

		content = Values.cast(inspectable.createContainer(this, inspectable.getTarget()));
		if (content != null) {
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.widthHint = 200;
			content.setLayoutData(gridData);
			layout(true, true);
		}
	}

	private void clearCurrentSelection() {
		target = null;
		if (content != null) {
			content.dispose();
			content = null;
		}
	}

	@Override
	public void selectionChanged(Object selection) {
		if (selection instanceof Inspectable) {
			presentInspectable(Values.<Inspectable<Object>> cast(selection));
		} else {
			clearCurrentSelection();
		}
	}
}
