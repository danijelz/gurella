package com.gurella.engine.graphics.render.command;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.RenderContext;

public class RenderComandBuffer {
	private final Array<RenderCommand> commands = new Array<RenderCommand>();

	public void add(RenderCommand command) {
		commands.add(command);
	}

	public void insert(int index, RenderCommand command) {
		commands.insert(index, command);
	}

	public void remove(RenderCommand command) {
		commands.removeValue(command, true);
	}

	public void render(RenderContext context) {
		for (int i = 0, n = commands.size; i < n; i++) {
			commands.get(i).process(context);
		}
	}
}
