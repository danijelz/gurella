package com.gurella.engine.application;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.gurella.engine.utils.DisposableManager;

//TODO unused
public interface ApplicationConstants {
	public static final ApplicationStateManager APPLICATION_STATE_MANAGER = new ApplicationStateManager();
	public static final DisposableManager DISPOSABLE_MANAGER = new DisposableManager();

	public static final AsyncExecutor ASYNC_EXECUTOR = DISPOSABLE_MANAGER.add(new AsyncExecutor(2));
	public static final InputMultiplexer INPUT_PROCESSORS = new InputMultiplexer();
	
	public static final Actor DUMMY_ACTOR = new Actor();
}
