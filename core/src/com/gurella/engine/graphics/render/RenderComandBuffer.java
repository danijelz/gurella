package com.gurella.engine.graphics.render;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.command.RenderCommand;
import com.gurella.engine.graphics.render.gl.GlContext;

public class RenderComandBuffer {
	private final Array<RenderCommand> commands = new Array<RenderCommand>();

	public void add(RenderCommand command) {
		commands.add(command);
	}
	
	public void addAll(Array<RenderCommand> commands) {
		this.commands.addAll(commands);
	}

	public void insert(int index, RenderCommand command) {
		commands.insert(index, command);
	}

	public void remove(RenderCommand command) {
		commands.removeValue(command, true);
	}

	public void process(GlContext glContext) {
		for (int i = 0, n = commands.size; i < n; i++) {
			commands.get(i).process(glContext);
		}
	}
}
