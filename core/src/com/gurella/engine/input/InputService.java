package com.gurella.engine.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.utils.priority.TypedPriorityComparator;

public class InputService {
	private static final ObjectMap<Application, ApplicationInput> instances = new ObjectMap<Application, ApplicationInput>();
	private static final TypedPriorityComparator comparator = new TypedPriorityComparator(InputProcessor.class);

	private static ApplicationInput lastSelected;
	private static Application lastApp;

	private InputService() {
	}

	private static ApplicationInput getInstance() {
		ApplicationInput input;
		boolean subscribe = false;

		synchronized (instances) {
			Application app = AsyncService.getCurrentApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			input = instances.get(app);
			if (input == null) {
				input = new ApplicationInput();
				instances.put(app, input);
				subscribe = true;
			}

			lastApp = app;
			lastSelected = input;

		}

		if (subscribe) {
			EventService.unsubscribe(new Cleaner());
		}

		return input;
	}

	public static void addInputProcessor(InputProcessor processor) {
		InputMultiplexer multiplexer = getInstance().multiplexer;
		multiplexer.addProcessor(processor);
		multiplexer.getProcessors().sort(comparator);
	}

	public static void removeInputProcessor(InputProcessor processor) {
		getInstance().multiplexer.removeProcessor(processor);
	}

	public static void addInputContext(InputContext inputContext) {
		getInstance().mapper.addInputContext(inputContext);
	}

	public static void removeInputContext(InputContext inputContext) {
		getInstance().mapper.removeInputContext(inputContext);
	}

	private static class ApplicationInput {
		private final InputMultiplexer multiplexer = new InputMultiplexer();
		private final InputMapper mapper = new InputMapper();

		public ApplicationInput() {
			multiplexer.addProcessor(mapper);
			Gdx.input.setInputProcessor(multiplexer);
		}
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);

			synchronized (instances) {
				if (instances.remove(AsyncService.getCurrentApplication()) == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}
		}
	}
}
