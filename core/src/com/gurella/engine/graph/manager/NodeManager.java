package com.gurella.engine.graph.manager;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneGraph;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;

public class NodeManager extends GraphListenerSystem {
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
