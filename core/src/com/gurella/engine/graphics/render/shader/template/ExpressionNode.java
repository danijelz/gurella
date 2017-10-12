package com.gurella.engine.graphics.render.shader.template;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.shader.generator.ShaderGeneratorContext;

public class ExpressionNode extends ShaderTemplateNode {
	private ShaderTemplateExpression expression;

	public ExpressionNode(ShaderTemplateExpression expression) {
		this.expression = expression;
	}

	@Override
	protected void generate(ShaderGeneratorContext context) {
		if (expression.evaluate(context) != 0.0) {
			generateChildren(context);
		}
	}

	@Override
	protected String toStringValue() {
		StringBuilder builder = new StringBuilder();
		builder.append("if (");
		expression.toString(builder);
		return builder.append(')').toString();
	}

	public interface ShaderTemplateExpression {
		float evaluate(ShaderGeneratorContext context);

		void toString(StringBuilder builder);
	}

	public static class CompositeExpression implements ShaderTemplateExpression {
		private final Array<ShaderTemplateExpression> expressions;

		public CompositeExpression(Array<ShaderTemplateExpression> expressions) {
			this.expressions = expressions;
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			float result = 0;
			for (int i = 0, n = expressions.size; i < n; i++) {
				result = expressions.get(i).evaluate(context);
			}
			return result;
		}

		@Override
		public void toString(StringBuilder builder) {
			for (int i = 0; i < expressions.size; i++) {
				ShaderTemplateExpression expression = expressions.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append('(').append(expression).append(')');
			}
		}
	}

	public static class NumberLiteralExpression implements ShaderTemplateExpression {
		final float value;

		public NumberLiteralExpression(float value) {
			this.value = value;
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(value);
		}
	}

	public static class VarExpression implements ShaderTemplateExpression {
		final String name;

		public VarExpression(String name) {
			this.name = name;
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return context.getValue(name);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(name);
		}
	}

	public static abstract class UnaryOperation implements ShaderTemplateExpression {
		final ShaderTemplateExpression param;

		public UnaryOperation(ShaderTemplateExpression param) {
			this.param = param;
		}
	}

	public static class NotOperation extends UnaryOperation {
		public NotOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return param.evaluate(context) == 0.0f ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('!').append('(').append(param).append(')');
		}
	}

	public static abstract class BaseAssignOperation extends UnaryOperation {
		String variableName;

		public BaseAssignOperation(ShaderTemplateExpression param, String assignment) {
			super(param);
			this.variableName = assignment;
		}

		@Override
		public final float evaluate(ShaderGeneratorContext context) {
			float assignee = context.getValue(variableName);
			float result = evaluate(assignee, param.evaluate(context));
			context.setValue(variableName, result);
			return result;
		}

		abstract float evaluate(float assignee, float value);
	}

	public static class AssignOperation extends BaseAssignOperation {
		public AssignOperation(ShaderTemplateExpression param, String variableName) {
			super(param, variableName);
		}

		@Override
		float evaluate(float assignee, float value) {
			return value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" = ");
		}
	}

	public static class AssignAddOperation extends BaseAssignOperation {
		public AssignAddOperation(ShaderTemplateExpression param, String variableName) {
			super(param, variableName);
		}

		@Override
		float evaluate(float assignee, float value) {
			return assignee + value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" += ");
		}
	}

	public static class AssignSubOperation extends BaseAssignOperation {
		public AssignSubOperation(ShaderTemplateExpression param, String variableName) {
			super(param, variableName);
		}

		@Override
		float evaluate(float assignee, float value) {
			return assignee - value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" -= ");
		}
	}

	public static class AssignMulOperation extends BaseAssignOperation {
		public AssignMulOperation(ShaderTemplateExpression param, String variableName) {
			super(param, variableName);
		}

		@Override
		float evaluate(float assignee, float value) {
			return assignee * value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" *= ");
		}
	}

	public static class AssignDivOperation extends BaseAssignOperation {
		public AssignDivOperation(ShaderTemplateExpression param, String variableName) {
			super(param, variableName);
		}

		@Override
		float evaluate(float assignee, float value) {
			if (value == 0.0) {
				throw new RuntimeException("Divide by zero");
			}
			return assignee / value;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" /= ");
		}
	}

	public static class UnaryMinusOperation extends UnaryOperation {
		public UnaryMinusOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return -param.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append(" --");
		}
	}

	public static class AbsOperation extends UnaryOperation {
		public AbsOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return Math.abs(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("abs(").append(param).append(')');
		}
	}

	public static class AcosOperation extends UnaryOperation {
		public AcosOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.acos(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("acos(").append(param).append(')');
		}
	}

	public static class AsinOperation extends UnaryOperation {
		public AsinOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.asin(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("asin(").append(param).append(')');
		}
	}

	public static class AtanOperation extends UnaryOperation {
		public AtanOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.atan(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("atan(").append(param).append(')');
		}
	}

	public static class CeilOperation extends UnaryOperation {
		public CeilOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.ceil(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("ceil(").append(param).append(')');
		}
	}

	public static class CosOperation extends UnaryOperation {
		public CosOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.cos(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("cos(").append(param).append(')');
		}
	}

	public static class FloorOperation extends UnaryOperation {
		public FloorOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.floor(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("floor(").append(param).append(')');
		}
	}

	public static class LogeOperation extends UnaryOperation {
		public LogeOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.log(MathUtils.E, param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("loge(").append(param).append(')');
		}
	}

	public static class Log10Operation extends UnaryOperation {
		public Log10Operation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.log(10f, param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("log10(").append(param).append(')');
		}
	}

	public static class SinOperation extends UnaryOperation {
		public SinOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.sin(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("sin(").append(param).append(')');
		}
	}

	public static class SqrtOperation extends UnaryOperation {
		public SqrtOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.sqrt(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("sqrt(").append(param).append(')');
		}
	}

	public static class ExpOperation extends UnaryOperation {
		public ExpOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.exp(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("exp(").append(param).append(')');
		}
	}

	public static class TanOperation extends UnaryOperation {
		public TanOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.tan(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("tan(").append(param).append(')');
		}
	}

	public static class IntOperation extends UnaryOperation {
		public IntOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (int) param.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("int(").append(param).append(')');
		}
	}

	public static class RandOperation extends UnaryOperation {
		public RandOperation(ShaderTemplateExpression param) {
			super(param);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.random(param.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("rand(").append(param).append(')');
		}
	}

	public static abstract class BinaryOperation implements ShaderTemplateExpression {
		final ShaderTemplateExpression left;
		final ShaderTemplateExpression right;

		public BinaryOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			this.left = left;
			this.right = right;
		}
	}

	public static class AndOperation extends BinaryOperation {
		public AndOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (left.evaluate(context) != 0.0f) && (right.evaluate(context) != 0.0f) ? 1f : 0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" && ").append(right).append(')');
		}
	}

	public static class OrOperation extends BinaryOperation {
		public OrOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (left.evaluate(context) != 0.0f) || (right.evaluate(context) != 0.0f) ? 1f : 0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" || ").append(right).append(')');
		}
	}

	public static class PlusOperation extends BinaryOperation {
		public PlusOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) + right.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" + ").append(right).append(')');
		}
	}

	public static class MinusOperation extends BinaryOperation {
		public MinusOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) + right.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" - ").append(right).append(')');
		}
	}

	public static class MultiplyOperation extends BinaryOperation {
		public MultiplyOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) * right.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" * ").append(right).append(')');
		}
	}

	public static class DivideOperation extends BinaryOperation {
		public DivideOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			float divider = right.evaluate(context);
			if (divider == 0.0) {
				throw new RuntimeException("Divide by zero");
			}
			return left.evaluate(context) / divider;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" / ").append(right).append(')');
		}
	}

	public static class LtOperation extends BinaryOperation {
		public LtOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) < right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" < ").append(right).append(')');
		}
	}

	public static class GtOperation extends BinaryOperation {
		public GtOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) < right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" > ").append(right).append(')');
		}
	}

	public static class LeOperation extends BinaryOperation {
		public LeOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) <= right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" <= ").append(right).append(')');
		}
	}

	public static class GeOperation extends BinaryOperation {
		public GeOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) <= right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" >= ").append(right).append(')');
		}
	}

	public static class EqOperation extends BinaryOperation {
		public EqOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) == right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" == ").append(right).append(')');
		}
	}

	public static class NeOperation extends BinaryOperation {
		public NeOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return left.evaluate(context) != right.evaluate(context) ? 1.0f : 0.0f;
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append('(').append(left).append(" != ").append(right).append(')');
		}
	}

	public static class LogOperation extends BinaryOperation {
		public LogOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return MathUtils.log(left.evaluate(context), right.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("log(").append(left).append(", ").append(right).append(')');
		}
	}

	public static class MinOperation extends BinaryOperation {
		public MinOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return Math.min(left.evaluate(context), right.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("min(").append(left).append(", ").append(right).append(')');
		}
	}

	public static class MaxOperation extends BinaryOperation {
		public MaxOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return Math.max(left.evaluate(context), right.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("max(").append(left).append(", ").append(right).append(')');
		}
	}

	public static class ModOperation extends BinaryOperation {
		public ModOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.IEEEremainder(left.evaluate(context), right.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("mod(").append(left).append(", ").append(right).append(')');
		}
	}

	public static class PowOperation extends BinaryOperation {
		public PowOperation(ShaderTemplateExpression left, ShaderTemplateExpression right) {
			super(left, right);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return (float) Math.pow(left.evaluate(context), right.evaluate(context));
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("pow(").append(left).append(", ").append(right).append(')');
		}
	}

	public static abstract class TernaryOperation implements ShaderTemplateExpression {
		final ShaderTemplateExpression first;
		final ShaderTemplateExpression second;
		final ShaderTemplateExpression third;

		public TernaryOperation(ShaderTemplateExpression first, ShaderTemplateExpression second,
				ShaderTemplateExpression third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}
	}

	public static class IfOperation extends TernaryOperation {
		public IfOperation(ShaderTemplateExpression first, ShaderTemplateExpression second,
				ShaderTemplateExpression third) {
			super(first, second, third);
		}

		@Override
		public float evaluate(ShaderGeneratorContext context) {
			return first.evaluate(context) != 0 ? second.evaluate(context) : third.evaluate(context);
		}

		@Override
		public void toString(StringBuilder builder) {
			builder.append("if (").append(first).append(", ").append(second).append(", ").append(third).append(')');
		}
	}

	// TODO
	// static int LoadOneArgumentFunctions ()
	// {
	// OneArgumentFunctions ["abs"] = fabs;
	// STD_FUNCTION (acos);
	// STD_FUNCTION (asin);
	// STD_FUNCTION (atan);
	// #ifndef WIN32 // doesn't seem to exist under Visual C++ 6
	// STD_FUNCTION (atanh);
	// #endif
	// STD_FUNCTION (ceil);
	// STD_FUNCTION (cos);
	// STD_FUNCTION (cosh);
	// STD_FUNCTION (exp);
	// STD_FUNCTION (exp);
	// STD_FUNCTION (floor);
	// STD_FUNCTION (log);
	// STD_FUNCTION (log10);
	// STD_FUNCTION (sin);
	// STD_FUNCTION (sinh);
	// STD_FUNCTION (sqrt);
	// STD_FUNCTION (tan);
	// STD_FUNCTION (tanh);
	//
	// OneArgumentFunctions ["int"] = DoInt;
	// OneArgumentFunctions ["rand"] = DoRandom;
	// OneArgumentFunctions ["rand"] = DoRandom;
	// OneArgumentFunctions ["percent"] = DoPercent;
	// return 0;
	// } // end of LoadOneArgumentFunctions
	//
	// static int LoadTwoArgumentFunctions ()
	// {
	// TwoArgumentFunctions ["log"] = log;
	// TwoArgumentFunctions ["min"] = DoMin;
	// TwoArgumentFunctions ["max"] = DoMax;
	// TwoArgumentFunctions ["mod"] = DoFmod;
	// TwoArgumentFunctions ["pow"] = DoPow; // x to the power y
	// TwoArgumentFunctions ["roll"] = DoRoll; // dice roll
	// return 0;
	// } // end of LoadTwoArgumentFunctions
	//
	// static int LoadThreeArgumentFunctions ()
	// {
	// ThreeArgumentFunctions ["if"] = DoIf;
	// return 0;
	// }
}
