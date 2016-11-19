package com.gurella.studio.editor.utils;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionInfo;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISources;

public class ControlExpression extends Expression {
	private Control control;

	public ControlExpression(Control control) {
		this.control = control;
	}

	@Override
	public void collectExpressionInfo(ExpressionInfo info) {
		info.addVariableNameAccess(ISources.ACTIVE_FOCUS_CONTROL_NAME);
	}

	@Override
	public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
		return EvaluationResult.valueOf(context.getVariable(ISources.ACTIVE_FOCUS_CONTROL_NAME) == control);
	}
}