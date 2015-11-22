package com.gurella.engine.graph.inputold;

import com.badlogic.gdx.math.Vector2;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.signal.Signal2;

public class InputListenerComponent extends SceneNodeComponent {
	public Signal2<TouchEventListener, SceneNode, Vector2> touchDownSignal = new InputSignal();
	public Signal2<TouchEventListener, SceneNode, Vector2> touchUpSignal = new InputSignal();
	public Signal2<TouchEventListener, SceneNode, Vector2> tapSignal = new InputSignal();

	public Signal2<TouchEventListener, SceneNode, Vector2> dragInSignal = new InputSignal();
	public Signal2<TouchEventListener, SceneNode, Vector2> dragOutSignal = new InputSignal();

	public Signal2<TouchEventListener, SceneNode, Vector2> dragStartSignal = new InputSignal();
	public Signal2<TouchEventListener, SceneNode, Vector2> dragSignal = new InputSignal();
	public Signal2<TouchEventListener, SceneNode, Vector2> dragStopSignal = new InputSignal();

	public InputListenerComponent() {
	}

	public InputListenerComponent(TouchEventListener tapListener) {
		tapSignal.addListener(tapListener);
	}

	public interface TouchEventListener {
		void handleInput(SceneNode node, float x, float y);
	}

	private static class InputSignal extends Signal2<TouchEventListener, SceneNode, Vector2> {
		@Override
		protected void dispatch(TouchEventListener listener, SceneNode node, Vector2 inputCoordinates) {
			listener.handleInput(node, inputCoordinates.x, inputCoordinates.y);
		}
	}
}
