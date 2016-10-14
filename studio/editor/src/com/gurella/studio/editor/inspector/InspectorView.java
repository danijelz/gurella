package com.gurella.studio.editor.inspector;

import static com.gurella.studio.GurellaStudioPlugin.getImage;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.gurella.engine.event.EventService;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.GurellaSceneEditor;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.subscription.SelectionListener;
import com.gurella.studio.editor.utils.UiUtils;

public class InspectorView extends SceneEditorView implements SelectionListener {
	private Object target;
	private InspectableContainer<?> content;

	public InspectorView(GurellaSceneEditor editor, int style) {
		super(editor, "Inspector", getImage("icons/showproperties_obj.gif"), style | SWT.BORDER);
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(this);
		GurellaStudioPlugin.getToolkit().adapt(this);
		addDisposeListener(e -> EventService.unsubscribe(editor.id, this));
		EventService.subscribe(editor.id, this);
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

	public interface Inspectable<T> {
		T getTarget();

		InspectableContainer<T> createContainer(InspectorView parent, T target);
	}
}
