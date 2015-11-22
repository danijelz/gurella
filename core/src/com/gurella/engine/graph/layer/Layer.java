package com.gurella.engine.graph.layer;

import java.util.Comparator;

import com.gurella.engine.utils.IndexedValue;

public final class Layer implements Comparable<Layer> {
	private static IndexedValue<Layer> INDEXER = new IndexedValue<Layer>();
	public static final Layer DEFAULT = new Layer(0, "Default");

	public final int id;
	public final int ordinal;
	public final String name;

	public Layer(int ordinal, String name) {
		this.id = INDEXER.getIndex(this);
		this.name = name;
		this.ordinal = ordinal;
	}

	public static Layer getLayerByType(int layerType) {
		return INDEXER.getValueByIndex(layerType);
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Layer other) {
		return Integer.compare(id, other.id);
	}

	public static final class LayerOrdinalComparator implements Comparator<Layer> {
		public static final LayerOrdinalComparator instance = new LayerOrdinalComparator();

		private LayerOrdinalComparator() {
		}

		@Override
		public int compare(Layer layer1, Layer layer2) {
			return Integer.compare(layer1.ordinal, layer2.ordinal);
		}
	}

	public static final class DescendingLayerOrdinalComparator implements Comparator<Layer> {
		public static final DescendingLayerOrdinalComparator instance = new DescendingLayerOrdinalComparator();

		private DescendingLayerOrdinalComparator() {
		}

		@Override
		public int compare(Layer layer1, Layer layer2) {
			return Integer.compare(layer2.ordinal, layer1.ordinal);
		}
	}
	
	//TODO unused
	public enum BuiltInLayer {
		Default(0), GUI(100), Dnd(200);
		
		public final Layer layer;

		BuiltInLayer(int ordinal) {
			layer = new Layer(ordinal, name());
		}
	}
}
