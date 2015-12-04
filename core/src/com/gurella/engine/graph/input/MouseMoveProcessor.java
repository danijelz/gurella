package com.gurella.engine.graph.input;

import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.mouseMoved;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onMouseOverEnd;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onMouseOverEndGlobal;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onMouseOverMove;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onMouseOverMoveGlobal;
import static com.gurella.engine.graph.behaviour.BehaviourEventCallbacks.onMouseOverStartGlobal;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.renderable.RenderableComponent;

public class MouseMoveProcessor {
	private SceneNode mouseOverNode;

	private InputSystem inputSystem;

	MouseMoveProcessor(InputSystem inputSystem) {
		this.inputSystem = inputSystem;
	}

	void mouseMoved(int screenX, int screenY, SceneNode pointerNode, Vector3 intersection) {
		for (BehaviourComponent behaviourComponent : inputSystem.getListeners(mouseMoved)) {
			behaviourComponent.mouseMoved(screenX, screenY);
		}

		if (mouseOverNode != null) {
			RenderableComponent renderableComponent = mouseOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == mouseOverNode) {
				for (BehaviourComponent behaviourComponent : inputSystem.getListeners(onMouseOverMoveGlobal)) {
					behaviourComponent.onMouseOverMove(renderableComponent, screenX, screenY, intersection);
				}

				for (BehaviourComponent behaviourComponent : inputSystem.getListeners(mouseOverNode, onMouseOverMove)) {
					behaviourComponent.onMouseOverMove(screenX, screenY, intersection);
				}
			} else {
				for (BehaviourComponent behaviourComponent : inputSystem.getListeners(onMouseOverEndGlobal)) {
					behaviourComponent.onMouseOverEnd(renderableComponent, screenX, screenY);
				}

				for (BehaviourComponent behaviourComponent : inputSystem.getListeners(mouseOverNode, onMouseOverEnd)) {
					behaviourComponent.onMouseOverEnd(screenX, screenY);
				}

				mouseOverNode = null;
			}
		}

		if (pointerNode != null && pointerNode != mouseOverNode) {
			mouseOverNode = pointerNode;
			RenderableComponent renderableComponent = pointerNode.getComponent(RenderableComponent.class);
			for (BehaviourComponent behaviourComponent : inputSystem.getListeners(onMouseOverStartGlobal)) {
				behaviourComponent.onMouseOverStart(renderableComponent, screenX, screenY, intersection);
			}

			for (BehaviourComponent behaviourComponent : inputSystem.getListeners(pointerNode, onMouseOverEnd)) {
				behaviourComponent.onMouseOverStart(screenX, screenY, intersection);
			}
		}
	}

	void reset() {
		mouseOverNode = null;
	}
}
