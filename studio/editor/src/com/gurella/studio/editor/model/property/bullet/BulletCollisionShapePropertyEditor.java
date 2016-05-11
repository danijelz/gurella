package com.gurella.studio.editor.model.property.bullet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.scene.bullet.shapes.BoxCollisionShape;
import com.gurella.engine.scene.bullet.shapes.BulletCollisionShape;
import com.gurella.engine.scene.bullet.shapes.CapsuleCollisionShape;
import com.gurella.engine.scene.bullet.shapes.EmptyCollisionShape;
import com.gurella.engine.scene.bullet.shapes.SphereCollisionShape;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.model.ModelEditorContext;
import com.gurella.studio.editor.model.property.ComplexPropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.utils.UiUtils;

public class BulletCollisionShapePropertyEditor extends ComplexPropertyEditor<BulletCollisionShape> {
	public BulletCollisionShapePropertyEditor(Composite parent,
			PropertyEditorContext<?, BulletCollisionShape> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set value", () -> updateValue(new EmptyCollisionShape()));
			if (context.isNullable()) {
				addMenuItem("Set null", () -> updateValue(null));
			}
		}
	}

	private void buildUi() {
		BulletCollisionShape value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		} else {
			FormToolkit toolkit = getToolkit();
			Label label = toolkit.createLabel(body, "type:");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

			ComboViewer comboViewer = UiUtils.createEnumComboViewer(body, CollisionShapeType.class);
			comboViewer.setSelection(new StructuredSelection(CollisionShapeType.valuesByType.get(value.getClass())));
			comboViewer.addSelectionChangedListener(e -> selectionChanged(e.getSelection()));
			comboViewer.getCombo().setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

			if (!(value instanceof EmptyCollisionShape)) {
				ModelEditorContext<BulletCollisionShape> shapeContext = new ModelEditorContext<>(context, value);
				ModelEditorContainer<BulletCollisionShape> shapeModelEditor = new ModelEditorContainer<>(body,
						shapeContext);
				shapeModelEditor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			}

			UiUtils.adapt(body);
			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private void selectionChanged(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			CollisionShapeType shapeType = (CollisionShapeType) ((IStructuredSelection) selection).getFirstElement();
			updateValue(shapeType.shapeConstructor.get());
		} else {
			updateValue(null);
		}
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	private void updateValue(BulletCollisionShape value) {
		setValue(value);
		rebuildUi();
	}

	private enum CollisionShapeType {
		empty(EmptyCollisionShape::new, EmptyCollisionShape.class),

		box(BoxCollisionShape::new, BoxCollisionShape.class),

		sphere(SphereCollisionShape::new, SphereCollisionShape.class),

		capsule(CapsuleCollisionShape::new, CapsuleCollisionShape.class),

		;

		static final Map<Class<? extends BulletCollisionShape>, CollisionShapeType> valuesByType = new HashMap<>();
		static {
			Arrays.stream(values()).forEach(v -> valuesByType.put(v.type, v));
		}

		final Supplier<BulletCollisionShape> shapeConstructor;
		final Class<? extends BulletCollisionShape> type;

		private CollisionShapeType(Supplier<BulletCollisionShape> shapeConstructor,
				Class<? extends BulletCollisionShape> type) {
			this.shapeConstructor = shapeConstructor;
			this.type = type;
		}
	}
}
