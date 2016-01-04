package com.gurella.engine.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputProcessorQueue;

public class InputService implements InputProcessor {
	private final InputMultiplexer multiplexer = new InputMultiplexer();
	private InputProcessorQueue inputProcessorQueue = new InputProcessorQueue(multiplexer);

	public void addInputProcessor(InputProcessor processor) {
		multiplexer.addProcessor(processor);
	}

	public void removeInputProcessor(InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}

	@Override
	public boolean keyDown(int keycode) {
		return inputProcessorQueue.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return inputProcessorQueue.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return inputProcessorQueue.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return inputProcessorQueue.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return inputProcessorQueue.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return inputProcessorQueue.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return inputProcessorQueue.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return inputProcessorQueue.scrolled(amount);
	}
}
