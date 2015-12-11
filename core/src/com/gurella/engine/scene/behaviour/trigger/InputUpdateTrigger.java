package com.gurella.engine.scene.behaviour.trigger;

import static com.gurella.engine.scene.behaviour.BehaviourEvents.onInput;

import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.event.EventTrigger;

public class InputUpdateTrigger extends EventTrigger implements UpdateListener {
	@Override
	protected void activated() {
		EventService.addListener(UpdateEvent.class, this);
	}

	@Override
	protected void deactivated() {
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
