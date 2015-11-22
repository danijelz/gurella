package com.gurella.engine.message;

import com.badlogic.gdx.utils.ObjectSet;

public interface MessagePredicate {
	boolean evaluate(ObjectSet<Message> messagesById);
	
	public class IsAbsentPredicate implements MessagePredicate {
		public static MessagePredicate instance = new IsAbsentPredicate();

		@Override
		public boolean evaluate(ObjectSet<Message> messagesByType) {
			return messagesByType.size == 0;
		}
	}
	
	public class IsPresentPredicate implements MessagePredicate {
		public static MessagePredicate instance = new IsPresentPredicate();

		@Override
		public boolean evaluate(ObjectSet<Message> messagesByType) {
			return messagesByType.size > 0;
		}
	}
	
	public class TruePredicate implements MessagePredicate {
		public static MessagePredicate instance = new TruePredicate();

		@Override
		public boolean evaluate(ObjectSet<Message> messagesByType) {
			return true;
		}
	}
	
	public class FalsePredicate implements MessagePredicate {
		public static MessagePredicate instance = new FalsePredicate();

		@Override
		public boolean evaluate(ObjectSet<Message> messagesByType) {
			return false;
		}
	}
}
