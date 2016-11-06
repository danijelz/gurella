package com.gurella.engine.test;

import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.input.TapInfo;
import com.gurella.engine.subscriptions.scene.input.SceneTapListener;

public class TestInputComponent extends SceneNodeComponent2 implements SceneTapListener {
	@Override
	public void onTap(TapInfo tapInfo) {
		System.out.println("onTap");
	}
}
