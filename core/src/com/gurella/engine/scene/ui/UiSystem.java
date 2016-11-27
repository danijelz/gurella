package com.gurella.engine.scene.ui;

import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.SceneService2;
import com.gurella.engine.subscriptions.base.object.ObjectsParentListener;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;

public class UiSystem extends SceneService2 implements ComponentActivityListener, ObjectsParentListener {
	UiFocusManager uiFocusManager;

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
	public void componentActivated(SceneNodeComponent2 component) {
		if (component instanceof UiComponent) {
			UiComponent uiComponent = (UiComponent) component;
			uiComponent.uiSystem = this;
			uiComponent.parent = findParentComposite(uiComponent.getParent());
			uiComponent.parent._components.add(uiComponent);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent2 component) {
		if (component instanceof UiComponent) {
			UiComponent uiComponent = (UiComponent) component;
			uiComponent.uiSystem = null;
			if (uiComponent.parent != null) {
				uiComponent.parent._components.remove(uiComponent);
			}
			uiComponent.parent = null;
		}
	}

	@Override
	public void parentChanged(ManagedObject object, ManagedObject oldParent, ManagedObject newParent) {
		if (object instanceof UiComponent) {
			UiComponent uiComponent = (UiComponent) object;
			if (uiComponent.parent != null) {
				uiComponent.parent._components.remove(uiComponent);
			}
			uiComponent.parent = findParentComposite(newParent);
		}
	}

	private CompositeComponent findParentComposite(ManagedObject newParent) {
		// TODO Auto-generated method stub
		return null;
	}
}
