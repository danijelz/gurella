package com.gurella.engine.scene.ui;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneService;
import com.gurella.engine.subscriptions.base.object.ObjectsParentListener;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;

public class UiSystem extends SceneService implements ComponentActivityListener, ObjectsParentListener, Composite {
	UiFocusManager uiFocusManager;

	transient final OrderedIdentitySet<UiComponent> _components = new OrderedIdentitySet<UiComponent>();
	public transient final ImmutableArray<UiComponent> components = _components.orderedItems();

	Layout layout;

	public UiSystem(Scene scene) {
		super(scene);
		uiFocusManager = new UiFocusManager(scene);
	}

	@Override
	protected void serviceActivated() {
		uiFocusManager.activate();
	}

	@Override
	protected void serviceDeactivated() {
		uiFocusManager.deactivate();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof UiComponent) {
			UiComponent uiComponent = (UiComponent) component;
			uiComponent.uiSystem = this;
			addComponentToParent(uiComponent);
		}
	}

	private void addComponentToParent(UiComponent child) {
		Composite parent = findParentComposite(child);
		if (parent == this) {
			child.parent = this;
			_components.add(child);
		} else {
			child.parent = parent;
			((CompositeComponent) parent)._components.add(child);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof UiComponent) {
			UiComponent uiComponent = (UiComponent) component;
			uiComponent.uiSystem = null;
			removeComponentFromParent(uiComponent);
		}
	}

	private void removeComponentFromParent(UiComponent child) {
		Composite parent = child.parent;
		child.parent = null;
		if (parent == this) {
			_components.remove(child);
		} else {
			((CompositeComponent) parent)._components.remove(child);
		}
	}

	@Override
	public void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		if (!(object instanceof UiComponent)) {
			return;
		}

		UiComponent uiComponent = (UiComponent) object;
		Composite oldUiParent = uiComponent.parent;
		Composite newUiParent = findParentComposite(uiComponent);

		if (oldUiParent == newUiParent) {
			return;
		}

		if (oldUiParent != null) {
			removeComponentFromParent(uiComponent);
		}

		if (newUiParent != null) {
			addComponentToParent(uiComponent);
		}
	}

	private Composite findParentComposite(UiComponent uiComponent) {
		SceneNode parentNode = uiComponent.getNode();
		while (parentNode != null) {
			CompositeComponent composite = parentNode.getComponent(CompositeComponent.class);
			if (composite != null) {
				return composite;
			}
			parentNode = parentNode.getParentNode();
		}
		return this;
	}

	@Override
	public ImmutableArray<UiComponent> components() {
		return _components.orderedItems();
	}

	@Override
	public void layout() {
		if (layout != null) {
			layout.layout(this, true);
		}
	}

	@Override
	public UiComponent findComponentAt(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}
}
