package com.gurella.engine.test;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.input.TapInfo;
import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.input.NodeTouchListener;
import com.gurella.engine.subscriptions.scene.input.SceneTapListener;

public class TestInputComponent extends SceneNodeComponent2 implements SceneTapListener, NodeTouchListener {
	@Override
	public void onTap(TapInfo tapInfo) {
		System.out.println("onTap");
	}

	@Override
	public void onTouchDown(PointerInfo pointerInfo) {
		System.out.println("onTouchDown");
	}

	@Override
	public void onTouchUp(PointerInfo pointerInfo) {
		System.out.println("onTouchUp");
	}
}
