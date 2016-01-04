package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RegistryAwareJson extends Json {
	@Override
	public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData) {

	}
}
