package com.gurella.engine.graph.input;

import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.mouseMoved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onMouseOverEnd;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onMouseOverEndResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onMouseOverMove;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onMouseOverMoveResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onMouseOverStartResolved;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.behaviour.ScriptComponent;
import com.gurella.engine.graph.renderable.RenderableComponent;

public class MouseMoveProcessor {
	private SceneNode mouseOverNode;

	private InputSystem inputSystem;

	MouseMoveProcessor(InputSystem inputSystem) {
		this.inputSystem = inputSystem;
	}

	void mouseMoved(int screenX, int screenY, SceneNode pointerNode, Vector3 intersection) {
		for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(mouseMoved)) {
			scriptComponent.mouseMoved(screenX, screenY);
		}

		if (mouseOverNode != null) {
			RenderableComponent renderableComponent = mouseOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == mouseOverNode) {
				for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onMouseOverMoveResolved)) {
					scriptComponent.onMouseOverMove(renderableComponent, screenX, screenY, intersection);
				}

				for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(mouseOverNode, onMouseOverMove)) {
					scriptComponent.onMouseOverMove(screenX, screenY, intersection);
				}
			} else {
				for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onMouseOverEndResolved)) {
					scriptComponent.onMouseOverEnd(renderableComponent, screenX, screenY);
				}

				for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(mouseOverNode, onMouseOverEnd)) {
					scriptComponent.onMouseOverEnd(screenX, screenY);
				}

				mouseOverNode = null;
			}
		}

		if (pointerNode != null && pointerNode != mouseOverNode) {
			mouseOverNode = pointerNode;
			RenderableComponent renderableComponent = pointerNode.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onMouseOverStartResolved)) {
				scriptComponent.onMouseOverStart(renderableComponent, screenX, screenY, intersection);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(pointerNode, onMouseOverEnd)) {
				scriptComponent.onMouseOverStart(screenX, screenY, intersection);
			}
		}
	}

	void reset() {
		mouseOverNode = null;
	}
}
