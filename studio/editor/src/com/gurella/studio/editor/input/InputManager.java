package com.gurella.studio.editor.input;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputService;
import com.gurella.engine.utils.priority.TypedPriorityComparator;
import com.gurella.studio.editor.subscription.EditorInputUpdateListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;
import com.gurella.studio.editor.subscription.InputProcessorActivationListener;

public class InputManager
		implements EditorInputUpdateListener, InputProcessorActivationListener, EditorPreCloseListener {
	private static final TypedPriorityComparator comparator = new TypedPriorityComparator(InputProcessor.class);

	private final int editorId;

	private final InputMultiplexer multiplexer = new InputMultiplexer();
	private final InputEventQueue inputQueue = new InputEventQueue(multiplexer);

	public InputManager(int editorId) {
		this.editorId = editorId;
		InputService.addInputProcessor(inputQueue);
		EventService.subscribe(editorId, this);
	}

	public void addInputProcessor(InputProcessor processor) {
		multiplexer.addProcessor(processor);
		multiplexer.getProcessors().sort(comparator);
	}

	public void removeInputProcessor(InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}

	@Override
	public void onEditorPreClose() {
		InputService.removeInputProcessor(inputQueue);
		EventService.unsubscribe(editorId, this);
	}

	@Override
	public void onInputUpdate() {
		inputQueue.drain();
	}

	@Override
	public void activate(InputProcessor processor) {
		addInputProcessor(processor);
	}

	@Override
	public void deactivate(InputProcessor processor) {
		removeInputProcessor(processor);
	}
}
