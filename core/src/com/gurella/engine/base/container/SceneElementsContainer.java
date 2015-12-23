package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedSet;

//TODO unused
public class SceneElementsContainer implements Serializable {
	private Container container = new Container();
	
	private OrderedSet<ManagedObject> objects = new OrderedSet<ManagedObject>();
	private OrderedSet<ManagedObject> templates = new OrderedSet<ManagedObject>();
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}
