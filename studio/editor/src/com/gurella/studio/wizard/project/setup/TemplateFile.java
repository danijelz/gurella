package com.gurella.studio.wizard.project.setup;

import java.util.Map;
import java.util.Map.Entry;

import com.gurella.studio.editor.utils.Try;

public class TemplateFile extends ResourceFile {
	private final Map<String, String> replacements;

	public TemplateFile(String resourceName, String outputName, Map<String, String> replacements) {
		super(resourceName, outputName);
		this.replacements = replacements;
	}

	@Override
	byte[] getContent() {
		String text = Try.ofFailable(() -> new String(super.getContent(), "UTF-8")).getUnchecked();
		for (Entry<String, String> entry : replacements.entrySet()) {
			text = text.replace(entry.getKey(), entry.getValue());
		}
		String result = text;
		return Try.ofFailable(() -> result.getBytes("UTF-8")).getUnchecked();
	}
}
