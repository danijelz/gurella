package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graphics.render.RenderNode.Connection;

public class RenderPathIterator {
	private final ObjectSet<RenderNode> visited = new ObjectSet<RenderNode>(17);

	void iterate(Array<RenderNode> nodes, RenderNodeConsumer consumer) {
		try {
			for (int i = 0, n = nodes.size; i < n; i++) {
				iterate(nodes.get(i), consumer);
			}
		} finally {
			visited.clear();
		}
	}

	private void iterate(RenderNode node, RenderNodeConsumer consumer) {
		consumer.consume(node);
		visited.add(node);

		Array<Connection> outputs = node.outputsByIndex;
		for (int i = 0, n = outputs.size; i < n; i++) {
			RenderNode outNode = outputs.get(i).outNode;
			if (!visited.contains(outNode) && allInputsReady(outNode)) {
				iterate(outNode, consumer);
			}
		}
	}

	private boolean allInputsReady(RenderNode node) {
		Array<Connection> inputs = node.inputsByIndex;
		for (int i = 0, n = inputs.size; i < n; i++) {
			if (!visited.contains(inputs.get(i).inNode)) {
				return false;
			}
		}
		return true;
	}

	public interface RenderNodeConsumer {
		void consume(RenderNode node);
	}
}
