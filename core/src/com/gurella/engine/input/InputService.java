package com.gurella.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputEventQueue;

public class InputService {
	private static final InputMultiplexer multiplexer = new InputMultiplexer();
	private static final InputMapper mapper = new InputMapper();

	static {
		multiplexer.addProcessor(mapper);
		Gdx.input.setInputProcessor(multiplexer);
	}

	private InputService() {
	}

	public static void addInputProcessor(InputProcessor processor) {
		multiplexer.addProcessor(processor);
	}

	public static void removeInputProcessor(InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}

	public static void addInputContext(InputContext inputContext) {
		mapper.addInputContext(inputContext);
	}

	public static void removeInputContext(InputContext inputContext) {
		mapper.removeInputContext(inputContext);
	}
}
