package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public abstract class EvaluateNode extends PreprocessedShaderTemplateNode {
	private String firstProperty;
	private String secondProperty;
	private float constant;
	private boolean useCnstant;

	public EvaluateNode(boolean preprocessed, String expression) {
		super(preprocessed);
		String[] params = expression.split(",");
		if (params.length != 2) {
			throw new GdxRuntimeException("Invalid expression: " + expression
					+ " Correct form: '@expType (variableName, variableNameOrIntLiteral)'.");
		}

		firstProperty = params[0].trim();
		secondProperty = params[1].trim();
		try {
			constant = Float.parseFloat(secondProperty);
			useCnstant = true;
		} catch (Exception e) {
		}
	}

	@Override
	protected void preprocess(ShaderGeneratorContext context) {
		if (preprocessed) {
			float first = context.getValue(firstProperty);
			float second = useCnstant ? context.getValue(secondProperty) : constant;
			context.setValue(firstProperty, evaluate(first, second));
		}
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (!preprocessed) {
			float first = context.getValue(firstProperty);
			float second = useCnstant ? context.getValue(secondProperty) : constant;
			context.setValue(firstProperty, evaluate(first, second));
		}
	}

	protected abstract float evaluate(float first, float second);

	@Override
	protected String toStringValue() {
		return firstProperty + getOperatorString() + secondProperty;
	}

	protected abstract String getOperatorString();
}
