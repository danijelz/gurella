package com.gurella.engine.graph.spatial.bvh;

import java.util.Comparator;

enum Axis {
	X(XAxisComparator.instance), Y(YAxisComparator.instance), Z(ZAxisComparator.instance);

	public final Comparator<BvhSpatial> comparator;

	private Axis(Comparator<BvhSpatial> comparator) {
		this.comparator = comparator;
	}

	private static final class XAxisComparator implements Comparator<BvhSpatial> {
		private static final XAxisComparator instance = new XAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPosition().x, o2.getPosition().x);
		}
	}

	private static final class YAxisComparator implements Comparator<BvhSpatial> {
		private static final YAxisComparator instance = new YAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPosition().y, o2.getPosition().y);
		}
	}

	private static final class ZAxisComparator implements Comparator<BvhSpatial> {
		private static final YAxisComparator instance = new YAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPosition().z, o2.getPosition().z);
		}
	}
}
