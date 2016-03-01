package com.gurella.engine.scene;

public class SceneSystem2 extends SceneElement2 {
	public final int baseSystemType;
	public final int systemType;

	public SceneSystem2() {
		Class<? extends SceneSystem2> type = getClass();
		baseSystemType = SceneSystemType.getBaseSystemType(type);
		systemType = SceneSystemType.getSystemType(type);
	}
}
