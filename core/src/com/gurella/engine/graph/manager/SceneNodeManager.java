package com.gurella.engine.graph.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.utils.ImmutableArray;

public class SceneNodeManager extends GraphListenerSystem {
	private ObjectMap<NodeGroup, Array<SceneNode>> nodeGroups = new ObjectMap<NodeGroup, Array<SceneNode>>();

	@Override
	public void componentActivated(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (Entry<NodeGroup, Array<SceneNode>> entry : nodeGroups.entries()) {
			NodeGroup nodeGroup = entry.key;
			if (nodeGroup.isValidNode(node)) {
				Array<SceneNode> groupedNodes = entry.value;
				if (!groupedNodes.contains(node, true)) {
					groupedNodes.add(node);
					Comparator<SceneNode> comparator = nodeGroup.getComparator();
					if (comparator != null) {
						groupedNodes.sort(comparator);
					}
				}
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		SceneNode node = component.getNode();
		for (Entry<NodeGroup, Array<SceneNode>> entry : nodeGroups.entries()) {
			NodeGroup nodeGroup = entry.key;
			if (nodeGroup.isValidNode(node)) {
				Array<SceneNode> groupedNodes = entry.value;
				groupedNodes.removeValue(node, true);
			}
		}
	}

	public void registerNodeGroup(NodeGroup nodeGroup) {
		Array<SceneNode> groupedNodes = nodeGroups.get(nodeGroup);

		if (groupedNodes == null) {
			groupedNodes = new Array<SceneNode>();
			nodeGroups.put(nodeGroup, groupedNodes);

			SceneGraph graph = getGraph();
			for (SceneNode node : graph.activeNodes) {
				if (nodeGroup.isValidNode(node)) {
					groupedNodes.add(node);
				}
			}

			Comparator<SceneNode> comparator = nodeGroup.getComparator();
			if (comparator != null) {
				groupedNodes.sort(comparator);
			}
		}
	}

	public void unregisterNodeGroup(NodeGroup nodeGroup) {
		nodeGroups.remove(nodeGroup);
	}

	public Array<SceneNode> getNodes(NodeGroup nodeGroup) {
		return nodeGroups.get(nodeGroup);
	}

	public static final class SceneNodeFamily {
		private static int INDEXER = 0;

		public final int id;
		public final Comparator<SceneNode> comparator;
		public final Predicate<SceneNode> predicate;

		Array<SceneNode> nodes = new Array<SceneNode>();
		ImmutableArray<SceneNode> immutableNodes = new ImmutableArray<SceneNode>(nodes);

		public SceneNodeFamily(Predicate<SceneNode> predicate) {
			id = INDEXER++;
			this.predicate = predicate;
			comparator = null;
		}

		public SceneNodeFamily(Predicate<SceneNode> predicate, Comparator<SceneNode> comparator) {
			id = INDEXER++;
			this.predicate = predicate;
			this.comparator = comparator;
		}

		void handle(SceneNode node) {
			boolean belongsToFamily = predicate.evaluate(node);
			boolean containsNode = nodes.contains(node, true);
			if (belongsToFamily && !containsNode) {
				nodes.add(node);
				if (comparator != null) {
					nodes.sort(comparator);
				}
			} else if (!belongsToFamily && containsNode) {
				nodes.removeValue(node, true);
			}
		}

		void remove(SceneNode node) {
			nodes.removeValue(node, true);
		}
	}

	public interface NodeGroup {
		boolean isValidNode(SceneNode node);

		Comparator<SceneNode> getComparator();
	}

	public static class ComponentBitsNodeGroup implements NodeGroup {
		private Bits componentBits;
		private Comparator<SceneNode> comparator;

		public ComponentBitsNodeGroup(Class<? extends SceneNodeComponent>... componentClasses) {
			this(null, componentClasses);
		}

		public ComponentBitsNodeGroup(Comparator<SceneNode> comparator,
				Class<? extends SceneNodeComponent>... componentClasses) {
			componentBits = SceneNodeComponent.getBitsFor(componentClasses);
			this.comparator = comparator;
		}

		@Override
		public boolean isValidNode(SceneNode node) {
			return node.getComponentBits().containsAll(componentBits);
		}

		@Override
		public Comparator<SceneNode> getComparator() {
			return comparator;
		}
	}
}
