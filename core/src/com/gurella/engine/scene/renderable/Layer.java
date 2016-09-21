package com.gurella.engine.scene.renderable;

import java.util.Comparator;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ValueRegistry;
import com.gurella.engine.utils.Values;

public final class Layer implements Comparable<Layer> {
	public static final Comparator<Layer> nameComparator = new LayerNameComparator();
	public static final Comparator<Layer> descendingOdinalComparator = new DescendingLayerOrdinalComparator();

	private static final ValueRegistry<Layer> registry = new ValueRegistry<Layer>();
	private static final IntMap<Layer> layersByOrdnal = new IntMap<Layer>();
	private static final ObjectMap<String, Layer> layersByName = new ObjectMap<String, Layer>();
	private static final ArrayExt<Layer> values = new ArrayExt<Layer>();

	public static final Layer DEFAULT = new Layer(0, "Default");
	public static final Layer SKY = new Layer(100, "Sky");
	public static final Layer GUI = new Layer(200, "GUI");
	public static final Layer DnD = new Layer(300, "DnD");

	public final int id;
	public final int ordinal;
	public final String name;

	Layer(int ordinal, String name) {
		this.id = registry.getId(this);
		this.name = name;
		this.ordinal = ordinal;

		layersByOrdnal.put(ordinal, this);
		layersByName.put(name, this);
		values.add(this);
	}

	public static Layer valueOf(int id) {
		return registry.getValue(id);
	}

	public static Layer valueOf(String name) {
		return layersByName.get(name);
	}

	public static Layer valueOf(int ordinal, String name) {
		Layer layer = layersByOrdnal.get(ordinal);
		if (layer == null) {
			layer = new Layer(ordinal, name);
		}
		return layer;
	}

	public static ImmutableArray<Layer> values() {
		return values.immutable();
	}

	@Override
	public int compareTo(Layer other) {
		return Values.compare(ordinal, other.ordinal);
	}

	private static final class LayerNameComparator implements Comparator<Layer> {
		@Override
		public int compare(Layer layer1, Layer layer2) {
			return layer1.name.compareTo(layer2.name);
		}
	}

	private static final class DescendingLayerOrdinalComparator implements Comparator<Layer> {
		@Override
		public int compare(Layer layer1, Layer layer2) {
			return Values.compare(layer2.ordinal, layer1.ordinal);
		}
	}
}
