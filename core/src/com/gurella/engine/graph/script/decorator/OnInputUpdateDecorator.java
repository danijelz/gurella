package com.gurella.engine.graph.script.decorator;

import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateEvent;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.graph.script.DefaultScriptMethod;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.graph.script.ScriptMethodDecorator;

public class OnInputUpdateDecorator implements UpdateListener, ScriptMethodDecorator {
	public OnInputUpdateDecorator() {
		EventService.addListener(UpdateEvent.class, this);
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.INPUT;
	}

	@Override
	public void update() {
		for (ScriptComponent scriptComponent : getScriptComponents(DefaultScriptMethod.onInput)) {
			scriptComponent.onInput();
		}
	}

	@Override
	public void componentActivated(ScriptComponent component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentDeactivated(ScriptComponent component) {
		// TODO Auto-generated method stub
		
	}
}
