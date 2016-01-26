package com.gurella.studio.nodes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventCallbackIdentifier;
import com.gurella.engine.scene.event.EventSubscriptionCallback;
import com.gurella.engine.scene.input.DragSource;
import com.gurella.engine.scene.input.DragStartCondition;
import com.gurella.engine.scene.input.DropTarget;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.utils.ImmutableArray;

public class TestInputComponnent extends BehaviourComponent implements TestEventSubscription {
	private static final EventCallbackIdentifier<TestInputComponnent> testCallback = EventCallbackIdentifier
			.get(TestInputComponnent.class, "testCallback");

	//////////// INPUT EVENTS
	@Override
	public void onTouchDown(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onTouchDown");
	}

	@Override
	public void onTouchUp(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onTouchUp");
	}

	@Override
	public void onTap(IntersectionTouchEvent pointerEvent, int count) {
		System.out.println("onTap");
		ImmutableArray<TestInputComponnent> listeners = getScene().eventManager.getListeners(getNode(), testCallback);
		for (int i = 0; i < listeners.size(); i++) {
			TestInputComponnent testInputComponnent = listeners.get(i);
			testInputComponnent.testCallback();
		}

		ImmutableArray<TestEventSubscription> subListeners = getScene().eventManager.getListeners(getNode(),
				TestEventSubscription.class);
		for (int i = 0; i < listeners.size(); i++) {
			TestEventSubscription listener = subListeners.get(i);
			listener.testEventSubscription();
		}
	}

	@EventSubscriptionCallback()
	public void testCallback() {
		System.out.println("testCallback");
	}

	@Override
	public void testEventSubscription() {
		System.out.println("testEventSubscription");
	}

	@Override
	public void onDragOverStart(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragOverStart");
	}

	@Override
	public void onDragOverMove(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragOverMove");
	}

	@Override
	public void onDragOverEnd(TouchEvent touchEvent) {
		// System.out.println("onDragOverEnd");
	}

	@Override
	public void onDragStart(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragStart");
	}

	@Override
	public void onDragMove(TouchEvent touchEvent) {
		// System.out.println("onDragMove");
	}

	@Override
	public void onDragEnd(TouchEvent touchEvent) {
		// System.out.println("onDragEnd");
	}

	@Override
	public void onLongPress(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onLongPress");
	}

	@Override
	public void onDoubleTouch(IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDoubleTouch");
	}

	@Override
	public void onScrolled(int screenX, int screenY, int amount, Vector3 intersection) {
		TextureComponent component = getComponent(TextureComponent.class);
		Color tint = component.getTint();
		tint.r += amount * 0.1f;
		component.setTint(tint);
		// System.out.println("onScrolled");
	}

	@Override
	public void onMouseOverStart(int screenX, int screenY, Vector3 intersection) {
		// System.out.println("onMouseOverStart");
	}

	@Override
	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection) {
		// System.out.println("onMouseOverMove");
	}

	@Override
	public void onMouseOverEnd(int screenX, int screenY) {
		// System.out.println("onMouseOverEnd");
	}

	// //RESOLVED GLOBAL INPUT
	@Override
	public void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onTouchDown");
	}

	@Override
	public void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onTouchUp");
	}

	@Override
	public void onTap(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent, int count) {
		// System.out.println("onTap");
	}

	@Override
	public void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragOverStart");
	}

	@Override
	public void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragOverMove");
	}

	@Override
	public void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
		// System.out.println("onDragOverEnd");
	}

	@Override
	public void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDragStart");
	}

	@Override
	public void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent) {
		// System.out.println("onDragMove");
	}

	@Override
	public void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent) {
		// System.out.println("onDragEnd");
	}

	@Override
	public void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onLongPress");
	}

	@Override
	public void onDoubleTouch(RenderableComponent renderableComponent, IntersectionTouchEvent pointerEvent) {
		// System.out.println("onDoubleTouch");
	}

	@Override
	public void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection) {
		// System.out.println("onScrolled");
	}

	@Override
	public void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
		// System.out.println("onMouseOverStart");
	}

	@Override
	public void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY,
			Vector3 intersection) {
		// System.out.println("onMouseOverMove");
	}

	@Override
	public void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY) {
		// System.out.println("onMouseOverEnd");
	}

	// //GLOBAL INPUT
	@Override
	public void keyDown(int keycode) {
		// System.out.println("keyDown");
	}

	@Override
	public void keyUp(int keycode) {
		// System.out.println("keyUp");
	}

	@Override
	public void keyTyped(char character) {
		// System.out.println("keyTyped");
	}

	@Override
	public void touchDown(TouchEvent touchEvent) {
		// System.out.println("touchDown");
	}

	@Override
	public void doubleTouchDown(TouchEvent touchEvent) {
		// System.out.println("doubleTouchDown");
	}

	@Override
	public void touchUp(TouchEvent touchEvent) {
		// System.out.println("touchUp");
	}

	@Override
	public void touchDragged(TouchEvent touchEvent) {
		// System.out.println("touchDragged");
	}

	@Override
	public void mouseMoved(int screenX, int screenY) {
		// System.out.println("mouseMoved");
	}

	@Override
	public void scrolled(int screenX, int screenY, int amount) {
		// System.out.println("scrolled");
	}

	@Override
	public void tap(TouchEvent touchEvent, int count) {
		// System.out.println("tap");
	}

	@Override
	public void longPress(TouchEvent touchEvent) {
		// System.out.println("longPress");
	}

	@Override
	public DragSource getDragSource(DragStartCondition dragStartCondition) {
		if (dragStartCondition != DragStartCondition.doubleTouch) {
			return null;
		}
		System.out.println("getDragSource: " + dragStartCondition.name());

		return new DragSource() {
			@Override
			public void dragStarted(float screenX, float screenY) {
				System.out.println("dragStarted");
			}

			@Override
			public void dragMove(float screenX, float screenY) {
				System.out.println("dragMove1");
			}

			@Override
			public void dragEnd(float screenX, float screenY) {
				System.out.println("dragEnd");
			}
		};
	}

	@Override
	public DropTarget getDropTarget(Array<DragSource> dragSources) {
		return new DropTarget() {
			@Override
			public void drop(float screenX, float screenY, Array<DragSource> dragSources) {
				System.out.println("drop");
			}

			@Override
			public void dragOut(float screenX, float screenY, Array<DragSource> dragSources) {
				System.out.println("dragOut");
			}

			@Override
			public void dragMove(float screenX, float screenY, Array<DragSource> dragSources) {
				System.out.println("dragMove2");
			}

			@Override
			public void dragIn(float screenX, float screenY, Array<DragSource> dragSources) {
				System.out.println("dragIn");
			}
		};
	}
}
