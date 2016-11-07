package com.gurella.engine.scene.ui;

import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;

public class Composite extends UiComponent {
	transient final OrderedIdentitySet<UiComponent> _components = new OrderedIdentitySet<UiComponent>();
	public transient final ImmutableArray<UiComponent> components = _components.orderedItems();
	
	public UiComponent findComponentAt(int x, int y) {
		return null;
	}
}
