package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedSet;

//TODO unused
public abstract class SceneElementsContainer implements Serializable {
	private static final String TEMPLATES_TAG = "templates";
	private static final String OBJECTS_TAG = "templates";

	private final ObjectManager manager = new ObjectManager();

	private final OrderedSet<ManagedObject> templates = new OrderedSet<ManagedObject>();
	private final OrderedSet<ManagedObject> objects = new OrderedSet<ManagedObject>();
	
	protected abstract void clear();
	
	protected abstract void reset();

	@Override
	public void write(Json json) {
		if (templates.size > 0) {
			json.writeArrayStart(TEMPLATES_TAG);
			Array<ManagedObject> templatesItems = templates.orderedItems();
			for (int i = 0; i < templatesItems.size; i++) {
				ManagedObject template = templatesItems.get(i);
				json.writeValue(template);
			}
			json.writeArrayEnd();
		}

		if (objects.size > 0) {
			json.writeArrayStart(OBJECTS_TAG);
			Array<ManagedObject> objectsItems = objects.orderedItems();
			for (int i = 0; i < objectsItems.size; i++) {
				ManagedObject object = objectsItems.get(i);
				json.writeValue(object);
			}
			json.writeArrayEnd();
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue values = jsonData.get(TEMPLATES_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject template = json.readValue(null, value);
				templates.add(template);
				manager.manage(template);
			}
			
			//TODO readProperties
		}
		
		values = jsonData.get(OBJECTS_TAG);
		if (values != null) {
			for (JsonValue value : values) {
				ManagedObject object = json.readValue(null, value);
				objects.add(object);
				manager.manage(object);
			}
		}
	}
}
