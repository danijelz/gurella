package com.gurella.engine.message;

import com.badlogic.gdx.utils.Array;

public class MessageQuery implements MessageExpression {
	Array<Array<MessageExpression>> oredExpressions = new Array<Array<MessageExpression>>();
	Array<MessageExpression> currentExpressionStack;

	public MessageQuery() {
		push();
	}

	public MessageQuery(MessageCondition condition) {
		push();
		currentExpressionStack.add(condition);
	}

	public MessageQuery and(MessageCondition... conditions) {
		currentExpressionStack.addAll(conditions);
		return this;
	}

	public MessageQuery or(MessageCondition... conditions) {
		push();
		currentExpressionStack.addAll(conditions);
		return this;
	}
	
	public MessageQuery and(MessageQuery... subQueries) {
		currentExpressionStack.addAll(subQueries);
		return this;
	}

	public MessageQuery or(MessageQuery... subQueries) {
		push();
		currentExpressionStack.addAll(subQueries);
		return this;
	}

	private void push() {
		currentExpressionStack = new Array<MessageExpression>();
		oredExpressions.add(currentExpressionStack);
	}
	
	/*public void ddd()
	{
		new Query(KeyboardMessages.isPressed(0))
		.and(KeyboardMessages.isPressed(1))
		.or(KeyboardMessages.isPressed(1))
		.or(PhysicsMessages.isDetectedCollisionBetween(a, b))
		.or(GameplayMessages.isPicupCollected());
	}*/
}
