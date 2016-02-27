package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventSubscription;

public interface ObjectMouseListener extends EventSubscription {
	void onMouseOverStart(int screenX, int screenY, Vector3 intersection);

	public void onMouseOverMove(int screenX, int screenY, Vector3 intersection);

	public void onMouseOverEnd(int screenX, int screenY);
}
