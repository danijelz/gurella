package com.gurella.engine.scene.ui;

import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.OrderedIdentitySet;

public class CompositeComponent extends UiComponent implements Composite {
	Layout layout;

	transient final OrderedIdentitySet<UiComponent> _components = new OrderedIdentitySet<UiComponent>();
	public transient final ImmutableArray<UiComponent> components = _components.orderedItems();

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
		return null;
	}
}
