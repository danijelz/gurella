package com.gurella.engine.scene.box2d;

import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.gurella.engine.application.GurellaApplication;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.scene.BuiltinSceneSystem;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.subscriptions.application.ApplicationActivityListener;
import com.gurella.engine.subscriptions.scene.ComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.PhysicsUpdateListener;

public class Box2dPhysicsSystem extends BuiltinSceneSystem
		implements ComponentActivityListener, PhysicsUpdateListener, ApplicationActivityListener {
	static {
		Box2D.init();
	}

	private static final float step = 1.0f / 60.0f;
	private static final int velocityIterations = 6;
	private static final int positionIterations = 2;

	private World world;
	private double accumulator;
	private double currentTime;

	public boolean stopSimulationOnPause;
	private boolean paused;

	public Box2dPhysicsSystem(Scene scene) {
		super(scene);
	}

	@Override
	protected void serviceActivated() {
		paused = ((GurellaApplication) AsyncService.getCurrentApplication().getApplicationListener()).isPaused();
	}

	@Override
	protected void serviceDeactivated() {

	}

	@Override
	public void onPhysicsUpdate() {
		if (paused && stopSimulationOnPause) {
			return;
		}

		double newTime = TimeUtils.millis() / 1000.0;
		double frameTime = Math.min(newTime - currentTime, 0.25);
		float deltaTime = (float) frameTime;

		currentTime = newTime;

		while (accumulator <= step) {
			world.step(step, velocityIterations, positionIterations);
			accumulator -= step;
		}

		// EventService.post(scene.getInstanceId(), physicsSimulationStartEvent);
		// dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		// EventService.post(scene.getInstanceId(), physicsSimulationEndEvent);
	}

	@Override
	public void onComponentActivated(SceneNodeComponent component) {
	}

	@Override
	public void onComponentDeactivated(SceneNodeComponent component) {
	}

	@Override
	public void onPause() {
		paused = true;
	}

	@Override
	public void onResume() {
		paused = false;
	}
}
