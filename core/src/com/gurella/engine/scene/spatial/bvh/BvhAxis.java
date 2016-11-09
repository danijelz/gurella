package com.gurella.engine.scene.spatial.bvh;

import java.util.Comparator;

enum BvhAxis {
	X(XAxisComparator.instance), Y(YAxisComparator.instance), Z(ZAxisComparator.instance);

	public final Comparator<BvhSpatial> comparator;

	private BvhAxis(Comparator<BvhSpatial> comparator) {
		this.comparator = comparator;
	}

	private static final class XAxisComparator implements Comparator<BvhSpatial> {
		private static final XAxisComparator instance = new XAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPositionX(), o2.getPositionX());
		}
	}

	private static final class YAxisComparator implements Comparator<BvhSpatial> {
		private static final YAxisComparator instance = new YAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPositionY(), o2.getPositionY());
		}
	}

	private static final class ZAxisComparator implements Comparator<BvhSpatial> {
		private static final YAxisComparator instance = new YAxisComparator();

		@Override
		public int compare(BvhSpatial o1, BvhSpatial o2) {
			return Float.compare(o1.getPositionZ(), o2.getPositionZ());
		}
	}
}
