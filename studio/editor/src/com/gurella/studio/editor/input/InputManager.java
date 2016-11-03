package com.gurella.studio.editor.input;

import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.input.InputService;
import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.plugin.PluginListener;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.utils.priority.TypedPriorityComparator;
import com.gurella.studio.editor.subscription.EditorInputUpdateListener;
import com.gurella.studio.editor.subscription.EditorPreCloseListener;

public class InputManager implements EditorInputUpdateListener, PluginListener, EditorPreCloseListener {
	private static final TypedPriorityComparator comparator = new TypedPriorityComparator(InputProcessor.class);

	private final int editorId;

	private final InputMultiplexer multiplexer = new InputMultiplexer();
	private final InputEventQueue inputQueue = new InputEventQueue(multiplexer);

	public InputManager(int editorId) {
		this.editorId = editorId;
		InputService.addInputProcessor(inputQueue);
		EventService.subscribe(editorId, this);
		Workbench.addListener(this);
	}

	public void addInputProcessor(InputProcessor processor) {
		Array<InputProcessor> processors = multiplexer.getProcessors();
		if (!processors.contains(processor, true)) {
			multiplexer.addProcessor(processor);
			processors.sort(comparator);
		}
	}

	public void removeInputProcessor(InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}

	@Override
	public void onEditorPreClose() {
		Workbench.removeListener(this);
		EventService.unsubscribe(editorId, this);
		InputService.removeInputProcessor(inputQueue);
	}

	@Override
	public void onInputUpdate() {
		inputQueue.drain();
	}

	@Override
	public void activated(Plugin plugin) {
		if (plugin instanceof InputProcessor) {
			addInputProcessor((InputProcessor) plugin);
		}
	}

	@Override
	public void deactivated(Plugin plugin) {
		if (plugin instanceof InputProcessor) {
			removeInputProcessor((InputProcessor) plugin);
		}
	}
}
