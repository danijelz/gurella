package com.gurella.engine.scene.bullet.rigidbody;

import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayout;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment;
import com.gurella.engine.scene.bullet.rigidbody.shape.Box2dCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.BoxCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.BulletCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.CapsuleCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.ConeCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.CylinderCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.EmptyCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.PlaneCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.SphereCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.TetrahedronCollisionShape;
import com.gurella.engine.scene.bullet.rigidbody.shape.TriangleCollisionShape;
import com.gurella.engine.utils.Reflection;

public class CollisionShapePropertyEditorFactory implements PropertyEditorFactory<BulletCollisionShape> {
	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<BulletCollisionShape> context) {
		new EditorLayout().columnsEqualWidth(false).numColumns(2).margins(0, 0).applyTo(parent);
		buildContent(parent, context);

		for (CollisionShapeType value : CollisionShapeType.values()) {
			context.addMenuItem("Set " + value.name(), new MenuItemAction(parent, context, value));
		}
	}

	private void updateValue(EditorComposite parent, PropertyEditorContext<BulletCollisionShape> context,
			Class<? extends BulletCollisionShape> type) {
		context.setPropertyValue(Reflection.newInstance(type));
		rebuildUi(parent, context);
	}

	private void rebuildUi(EditorComposite parent, PropertyEditorContext<BulletCollisionShape> context) {
		parent.disposeAllChildren();
		buildContent(parent, context);
	}

	private void buildContent(EditorComposite parent, PropertyEditorContext<BulletCollisionShape> context) {
		BulletCollisionShape value = context.getPropertyValue();
		if (value == null) {
			value = new EmptyCollisionShape();
		}

		EditorUi uiFactory = parent.getUiFactory();

		EditorLabel label = uiFactory.createLabel(parent, "shape:");
		label.setAlignment(Alignment.RIGHT);
		label.setFont(label.getFont().getHeight(), true, false);
		new EditorLayoutData().alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER).grab(false, false)
				.applyTo(label);

		EditorCombo<CollisionShapeType> combo = uiFactory.createEnumCombo(parent, CollisionShapeType.class);
		combo.setSelection(CollisionShapeType.valuesByType.get(value.getClass()));
		combo.addListener(EditorEventType.Selection, new SelectionChangedListener(this, parent, context, combo));
		new EditorLayoutData().alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER).grab(true, false)
				.applyTo(combo);

		if (!(value instanceof EmptyCollisionShape)) {
			EditorComposite modelEditor = context.createModelEditor(parent, value);
			new EditorLayoutData().alignment(HorizontalAlignment.FILL, VerticalAlignment.TOP).grab(true, false)
					.span(2, 1).applyTo(modelEditor);
		}

		parent.layout();
	}

	private final class MenuItemAction implements Runnable {
		private final EditorComposite parent;
		private final PropertyEditorContext<BulletCollisionShape> context;
		private final CollisionShapeType value;

		private MenuItemAction(EditorComposite parent, PropertyEditorContext<BulletCollisionShape> context,
				CollisionShapeType value) {
			this.parent = parent;
			this.context = context;
			this.value = value;
		}

		@Override
		public void run() {
			updateValue(parent, context, value.type);
		}
	}

	private enum CollisionShapeType {
		empty(EmptyCollisionShape.class),

		box(BoxCollisionShape.class),

		box2d(Box2dCollisionShape.class),

		sphere(SphereCollisionShape.class),

		capsule(CapsuleCollisionShape.class),

		cone(ConeCollisionShape.class),

		cylinder(CylinderCollisionShape.class),

		plane(PlaneCollisionShape.class),

		triangle(TriangleCollisionShape.class),

		tetrahedron(TetrahedronCollisionShape.class),

		;

		static final ObjectMap<Class<? extends BulletCollisionShape>, CollisionShapeType> valuesByType = new ObjectMap<Class<? extends BulletCollisionShape>, CollisionShapeType>();

		static {
			for (CollisionShapeType value : values()) {
				valuesByType.put(value.type, value);
			}
		}

		final Class<? extends BulletCollisionShape> type;

		private CollisionShapeType(Class<? extends BulletCollisionShape> type) {
			this.type = type;
		}
	}

	private static class SelectionChangedListener implements EditorEventListener {
		CollisionShapePropertyEditorFactory factory;
		EditorComposite parent;
		PropertyEditorContext<BulletCollisionShape> context;
		EditorCombo<CollisionShapeType> combo;

		public SelectionChangedListener(CollisionShapePropertyEditorFactory factory, EditorComposite parent,
				PropertyEditorContext<BulletCollisionShape> context, EditorCombo<CollisionShapeType> combo) {
			this.factory = factory;
			this.parent = parent;
			this.context = context;
			this.combo = combo;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			CollisionShapeType selection = combo.getSelection().get(0);
			factory.updateValue(parent, context, selection.type);
		}
	}
}
