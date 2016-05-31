package com.gurella.engine.scene.layer;

import java.util.Comparator;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ValueRegistry;
import com.gurella.engine.utils.Values;

public final class Layer implements Comparable<Layer> {
	private static ValueRegistry<Layer> registry = new ValueRegistry<Layer>();
	private static IntMap<Layer> layersByOrdnal = new IntMap<Layer>();
	private static ObjectMap<String, Layer> layersByName = new ObjectMap<String, Layer>();

	public static final Layer DEFAULT = new Layer(0, "Default");
	public static final Layer SKY = new Layer(100, "Sky");
	public static final Layer GUI = new Layer(200, "GUI");
	public static final Layer DnD = new Layer(300, "DnD");

	public final int id;
	public final int ordinal;
	public final String name;

	public Layer(int ordinal, String name) {
		if (layersByOrdnal.containsKey(ordinal)) {
			throw new GdxRuntimeException("Layer ordinal duplicate.");
		}

		if (layersByName.containsKey(name)) {
			throw new GdxRuntimeException("Layer name duplicate.");
		}

		this.id = registry.getId(this);
		this.name = name;
		this.ordinal = ordinal;

		layersByOrdnal.put(ordinal, this);
		layersByName.put(name, this);
	}

	public static Layer getLayer(int id) {
		return registry.getValue(id);
	}

	public static Layer getLayer(String name) {
		return layersByName.get(name);
	}

	@Override
	public int compareTo(Layer other) {
		return Values.compare(ordinal, other.ordinal);
	}

	public static final class LayerOrdinalComparator implements Comparator<Layer> {
		public static final LayerOrdinalComparator instance = new LayerOrdinalComparator();

		private LayerOrdinalComparator() {
		}

		@Override
		public int compare(Layer layer1, Layer layer2) {
			return Values.compare(layer1.ordinal, layer2.ordinal);
		}
	}

	public static final class DescendingLayerOrdinalComparator implements Comparator<Layer> {
		public static final DescendingLayerOrdinalComparator instance = new DescendingLayerOrdinalComparator();

		private DescendingLayerOrdinalComparator() {
		}

		@Override
		public int compare(Layer layer1, Layer layer2) {
			return Values.compare(layer2.ordinal, layer1.ordinal);
		}
	}
}
