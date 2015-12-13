package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onInput;

import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.events.UpdateEvent;
import com.gurella.engine.application.events.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class InputUpdateTrigger extends EventTrigger implements UpdateListener {
	@Override
	protected void start() {
		EventService.addListener(UpdateEvent.class, this);
	}

	@Override
	protected void stop() {
		EventService.removeListener(UpdateEvent.class, this);
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.INPUT;
	}

	@Override
	public void update() {
		for (BehaviourComponent behaviourComponent : eventManager.getListeners(onInput)) {
			behaviourComponent.onInput();
		}
	}
}
