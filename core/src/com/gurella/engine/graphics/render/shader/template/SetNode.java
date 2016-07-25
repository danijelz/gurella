package com.gurella.engine.graphics.render.shader.template;

import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class SetNode extends PreprocessedShaderTemplateNode {
	private String varName;
	private int varValue;

	public SetNode(boolean preprocessed, String value) {
		super(preprocessed);
		String[] params = value.split(",");
		varName = params[0].trim();
		try {
			varValue = Integer.parseInt(params[1].trim());
		} catch (Exception e) {
			varValue = 0;
		}
	}

	@Override
	protected String toStringValue() {
		return varName + ", " + varValue;
	}

	@Override
	protected void preprocess(ShaderGeneratorContext context) {
		if (preprocessed) {
			context.setValue(varName, varValue);
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (!preprocessed) {
			context.setValue(varName, varValue);
		}
	}

}
