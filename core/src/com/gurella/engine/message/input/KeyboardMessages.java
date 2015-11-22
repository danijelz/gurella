package com.gurella.engine.message.input;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.message.MessageCondition;
import com.gurella.engine.message.MessageId;
import com.gurella.engine.message.MessagePredicate;
import com.gurella.engine.message.MessagePredicate.IsAbsentPredicate;
import com.gurella.engine.message.MessagePredicate.IsPresentPredicate;

public class KeyboardMessages {
	public static MessageCondition isPressed(int key) {
		return createExpression(key, KeyboardAction.PRESSED, IsPresentPredicate.instance);
	}

	private static MessageCondition createExpression(int key, KeyboardAction action, MessagePredicate predicate) {
		MessageCondition expression = new MessageCondition();
		expression.messageId = MessageId.fromValue(KeyboardMessage.getInstance(key, action));
		expression.predicate = predicate;
		return expression;
	}

	public static MessageCondition[] isNotPressed(int key) {
		return new MessageCondition[] { createExpression(key, KeyboardAction.JUST_PRESSED, IsAbsentPredicate.instance),
				createExpression(key, KeyboardAction.PRESSED, IsAbsentPredicate.instance) };
	}

	public static MessageCondition isJustPressed(int key) {
		return createExpression(key, KeyboardAction.JUST_PRESSED, IsPresentPredicate.instance);
	}

	public static MessageCondition isReleased(int key) {
		return createExpression(key, KeyboardAction.RELEASED, IsPresentPredicate.instance);
	}

	enum KeyboardAction {
		JUST_PRESSED, PRESSED, RELEASED
	}

	static class KeyboardMessage {
		private static IntMap<KeyboardMessage> cachedMessages = new IntMap<KeyboardMessage>();

		public static KeyboardMessage getInstance(int key, KeyboardAction action) {
			int hash = getHash(key, action);
			KeyboardMessage keyboardMessage = cachedMessages.get(hash);

			if (keyboardMessage == null) {
				keyboardMessage = new KeyboardMessage(key, action);
				cachedMessages.put(hash, keyboardMessage);
			}

			return keyboardMessage;
		}

		int key;
		KeyboardAction action;

		public KeyboardMessage(int key, KeyboardAction action) {
			this.key = key;
			this.action = action;
		}

		private static int getHash(int key, KeyboardAction action) {
			return action.ordinal() * 1000 + key;
		}

		@Override
		public int hashCode() {
			return getHash(key, action);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyboardMessage other = (KeyboardMessage) obj;
			return key == other.key && action == other.action;
		}
	}
}
