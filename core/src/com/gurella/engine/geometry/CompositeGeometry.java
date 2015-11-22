package com.gurella.engine.geometry;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;

public class CompositeGeometry<V extends Vector<V>> implements Geometry<V> {
	private Array<Geometry<V>> composites = new Array<Geometry<V>>();
	
	public void addComposite(Geometry<V> composite) {
		composites.add(composite);
	}

	@Override
	public Bounds<V> getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(V point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(Geometry<V> geometry) {
		// TODO Auto-generated method stub
		return false;
	}
}
