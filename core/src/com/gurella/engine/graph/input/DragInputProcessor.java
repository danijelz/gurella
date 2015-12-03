package com.gurella.engine.graph.input;

import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragEnd;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragEndResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragMove;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragMoveResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragOverEnd;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragOverEndResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragOverMove;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragOverMoveResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragOverStartResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragStart;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.onDragStartResolved;
import static com.gurella.engine.graph.behaviour.DefaultScriptMethod.touchDragged;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.behaviour.ScriptComponent;
import com.gurella.engine.graph.renderable.RenderableComponent;

public class DragInputProcessor implements PointerActivityListener {
	private final IntMap<SceneNode> dragStartNodes = new IntMap<SceneNode>(10);
	private final IntMap<SceneNode> dragOverNodes = new IntMap<SceneNode>(10);

	private final TouchEvent touchEvent = new TouchEvent();
	private final IntersectionTouchEvent intersectionTouchEvent = new IntersectionTouchEvent();

	private InputSystem inputSystem;

	public DragInputProcessor(InputSystem inputSystem) {
		this.inputSystem = inputSystem;
	}

	@Override
	public void onPointerActivity(int pointer, int button, PointerTrack pointerTrack) {
		int key = pointer + button * 100;
		switch (pointerTrack.getPhase()) {
		case begin:
			begin(key, pointer, button, pointerTrack);
			break;
		case move:
			move(key, pointer, button, pointerTrack);
			break;
		case end:
			end(key, pointer, button, pointerTrack);
			break;
		default:
			break;
		}
	}

	private void begin(int key, int pointer, int button, PointerTrack pointerTrack) {
		int screenX = pointerTrack.getScreenX(0);
		int screenY = pointerTrack.getScreenY(0);
		touchEvent.set(pointer, button, screenX, screenY);
		for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(touchDragged)) {
			scriptComponent.touchDragged(touchEvent);
		}

		SceneNode node = pointerTrack.getNode(0);
		if (node != null) {
			intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, 0);
			dragStartNodes.put(key, node);
			RenderableComponent renderableComponent = node.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragStartResolved)) {
				scriptComponent.onDragStart(renderableComponent, intersectionTouchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(node, onDragStart)) {
				scriptComponent.onDragStart(intersectionTouchEvent);
			}

			dragOverNodes.put(key, node);
			renderableComponent = node.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragOverStartResolved)) {
				scriptComponent.onDragOverStart(renderableComponent, intersectionTouchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(node, onDragOverEnd)) {
				scriptComponent.onDragOverStart(intersectionTouchEvent);
			}
		}
	}

	private void move(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		touchEvent.set(pointer, button, screenX, screenY);
		for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(touchDragged)) {
			scriptComponent.touchDragged(touchEvent);
		}

		SceneNode dragStartNode = dragStartNodes.get(key);
		if (dragStartNode != null) {
			RenderableComponent renderableComponent = dragStartNode.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragMoveResolved)) {
				scriptComponent.onDragMove(renderableComponent, touchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(dragStartNode, onDragMove)) {
				scriptComponent.onDragMove(touchEvent);
			}
		}

		SceneNode pointerNode = pointerTrack.getNode(last);
		SceneNode dragOverNode = dragOverNodes.get(key);
		intersectionTouchEvent.set(pointer, button, screenX, screenY, pointerTrack, last);
		if (dragOverNode != null) {
			RenderableComponent renderableComponent = dragOverNode.getComponent(RenderableComponent.class);
			if (pointerNode == dragOverNode) {
				for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragOverMoveResolved)) {
					scriptComponent.onDragOverMove(renderableComponent, intersectionTouchEvent);
				}

				for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(dragOverNode, onDragOverMove)) {
					scriptComponent.onDragOverMove(intersectionTouchEvent);
				}
			} else {
				for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragOverEndResolved)) {
					scriptComponent.onDragOverEnd(renderableComponent, touchEvent);
				}

				for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(dragOverNode, onDragOverEnd)) {
					scriptComponent.onDragOverEnd(touchEvent);
				}

				dragOverNodes.remove(key);
			}
		}

		if (pointerNode != null && pointerNode != dragOverNode) {
			dragOverNodes.put(key, pointerNode);
			RenderableComponent renderableComponent = pointerNode.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragOverStartResolved)) {
				scriptComponent.onDragOverStart(renderableComponent, intersectionTouchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(pointerNode, onDragOverEnd)) {
				scriptComponent.onDragOverStart(intersectionTouchEvent);
			}
		}
	}

	private void end(int key, int pointer, int button, PointerTrack pointerTrack) {
		int last = pointerTrack.getSize() - 1;
		int screenX = pointerTrack.getScreenX(last);
		int screenY = pointerTrack.getScreenY(last);
		touchEvent.set(pointer, button, screenX, screenY);

		SceneNode dragStartNode = dragStartNodes.remove(key);
		if (dragStartNode != null) {
			RenderableComponent renderableComponent = dragStartNode.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragEndResolved)) {
				scriptComponent.onDragEnd(renderableComponent, touchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(dragStartNode, onDragEnd)) {
				scriptComponent.onDragEnd(touchEvent);
			}
		}

		SceneNode dragOverNode = dragOverNodes.remove(key);
		if (dragOverNode != null) {
			RenderableComponent renderableComponent = dragOverNode.getComponent(RenderableComponent.class);
			for (ScriptComponent scriptComponent : inputSystem.getScriptsByMethod(onDragOverEndResolved)) {
				scriptComponent.onDragOverEnd(renderableComponent, touchEvent);
			}

			for (ScriptComponent scriptComponent : inputSystem.getNodeScriptsByMethod(dragOverNode, onDragOverEnd)) {
				scriptComponent.onDragOverEnd(touchEvent);
			}
		}
	}

	@Override
	public void reset() {
		dragStartNodes.clear();
		dragOverNodes.clear();
	}
}
