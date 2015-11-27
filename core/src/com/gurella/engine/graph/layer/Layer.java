package com.gurella.engine.graph.layer;

import java.util.Comparator;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.IndexedValue;

public final class Layer implements Comparable<Layer> {
	private static IndexedValue<Layer> INDEXER = new IndexedValue<Layer>();
	private static ObjectMap<String, Layer> layersByName = new ObjectMap<String, Layer>();

	public static final Layer DEFAULT = new Layer(0, "Default");
	public static final Layer GUI = new Layer(100, "GUI");
	public static final Layer Dnd = new Layer(200, "Dnd");

	public final int id;
	public final int ordinal;
	public final String name;

	public Layer(int ordinal, String name) {
		if (layersByName.containsKey(name)) {
			throw new GdxRuntimeException("Layer name duplicate");
		}

		this.id = INDEXER.getIndex(this);
		this.name = name;
		this.ordinal = ordinal;

		layersByName.put(name, this);
	}

	public static Layer getLayer(int id) {
		return INDEXER.getValueByIndex(id);
	}

	public static Layer getLayer(String name) {
		return layersByName.get(name);
	}

	@Override
	public int compareTo(Layer other) {
		return Integer.compare(ordinal, other.ordinal);
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

	// TODO unused
	public enum CommonLayer {
		Default(0), GUI(100), Dnd(200);

		public final Layer layer;

		CommonLayer(int ordinal) {
			layer = new Layer(ordinal, name());
		}
	}
}
