package com.gurella.engine.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.utils.priority.TypedPriorityComparator;

public class InputService {
	private static final TypedPriorityComparator comparator = new TypedPriorityComparator(InputProcessor.class);
	private static final ObjectMap<Application, ApplicationInput> applicationInputs = new ObjectMap<Application, ApplicationInput>();

	private InputService() {
	}

	private static ApplicationInput getApplicationInput() {
		ApplicationInput input = applicationInputs.get(Gdx.app);
		if (input == null) {
			input = new ApplicationInput();
			applicationInputs.put(Gdx.app, input);
		}
		return input;
	}

	public static void addInputProcessor(InputProcessor processor) {
		InputMultiplexer multiplexer = getApplicationInput().multiplexer;
		multiplexer.addProcessor(processor);
		multiplexer.getProcessors().sort(comparator);
	}

	public static void removeInputProcessor(InputProcessor processor) {
		ApplicationInput input = applicationInputs.get(Gdx.app);
		if (input != null) {
			input.multiplexer.removeProcessor(processor);
		}
	}

	public static void addInputContext(InputContext inputContext) {
		getApplicationInput().mapper.addInputContext(inputContext);
	}

	public static void removeInputContext(InputContext inputContext) {
		ApplicationInput input = applicationInputs.get(Gdx.app);
		if (input != null) {
			input.mapper.removeInputContext(inputContext);
		}
	}

	private static class ApplicationInput {
		private final InputMultiplexer multiplexer = new InputMultiplexer();
		private final InputMapper mapper = new InputMapper();

		public ApplicationInput() {
			multiplexer.addProcessor(mapper);
			Gdx.input.setInputProcessor(multiplexer);
		}
	}
}
