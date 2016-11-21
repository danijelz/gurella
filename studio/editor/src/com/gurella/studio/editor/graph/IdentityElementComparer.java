package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.IElementComparer;

class IdentityElementComparer implements IElementComparer {
	static final IdentityElementComparer instance = new IdentityElementComparer();

	private IdentityElementComparer() {
	}

	@Override
	public boolean equals(Object a, Object b) {
		return a == b;
	}

	@Override
	public int hashCode(Object element) {
		return System.identityHashCode(element);
	}
}
