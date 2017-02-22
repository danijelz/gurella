package com.gurella.studio.wizard.project.setup;

import com.gurella.studio.editor.utils.Try;

public abstract class GeneratedFile extends ProjectFile {
	public GeneratedFile(String outputName) {
		super(outputName);
	}

	@Override
	protected final byte[] getContent() {
		return Try.ofFailable(() -> generate().getBytes("UTF-8")).getUnchecked();
	}

	protected abstract String generate();
}
