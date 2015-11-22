package com.gurella.engine.message;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

public class MessageCenter {
	private static PooledMessagePool pooledMessagePool = new PooledMessagePool();
	private static Array<PooledMessage> pooledMessages = new Array<PooledMessage>();
	private static IntMap<ObjectSet<Message>> publishedMessages = new IntMap<ObjectSet<Message>>();
	private static IntMap<Array<MesageSubscriber>> subscribers = new IntMap<Array<MesageSubscriber>>();

	private static ObjectSet<Message> emptyMessages = new ObjectSet<Message>();

	private static boolean processing;

	private MessageCenter() {
	}

	public static void publish(Message message) {
		if (processing) {
			pooledMessages.add(pooledMessagePool.obtain().set(message, true));
		} else {
			processing = true;
			publishSafely(message);
			processing = false;
		}
	}

	private static void publishSafely(Message message) {
		getMessageByType(message.messageId.hash).add(message);
		notify(message);
		processPooledMessages();
	}

	private static ObjectSet<Message> getMessageByType(int messageId) {
		ObjectSet<Message> messages = publishedMessages.get(messageId);
		if (messages == null) {
			messages = new ObjectSet<Message>();
			publishedMessages.put(messageId, messages);
		}
		return messages;
	}

	private static void notify(Message message) {
		Array<MesageSubscriber> messageSubscribers = subscribers.get(message.messageId.hash);

		if (messageSubscribers != null) {
			for (MesageSubscriber subscriber : messageSubscribers) {
				if (evaluate(subscriber.query)) {
					subscriber.action.handle();
				}
			}
		}
	}

	private static void processPooledMessages() {
		if (pooledMessages.size > 0) {
			PooledMessage pooledEvent = pooledMessages.removeIndex(0);

			if (pooledEvent.publish) {
				publishSafely(pooledEvent.message);
			} else {
				supressSafely(pooledEvent.message);
			}
		}
	}

	public static void suppress(Message message) {
		if (processing) {
			pooledMessages.add(pooledMessagePool.obtain().set(message, false));
		} else {
			processing = true;
			supressSafely(message);
			processing = false;
		}
	}

	private static void supressSafely(Message message) {
		ObjectSet<Message> messages = publishedMessages.get(message.messageId.hash);
		if (messages != null && messages.remove(message)) {
			notify(message);
		}
		processPooledMessages();
	}

	public static boolean evaluate(MessageQuery query) {
		boolean valid = false;

		for (Array<MessageExpression> andExpressions : query.oredExpressions) {
			valid |= evaluateAndExpressions(andExpressions);
		}

		return valid;
	}

	private static boolean evaluateAndExpressions(Array<MessageExpression> andExpressions) {
		for (MessageExpression andExpression : andExpressions) {
			if (!evaluate(andExpression)) {
				return false;
			}
		}

		return true;
	}

	private static boolean evaluate(MessageExpression expression) {
		if (expression instanceof MessageQuery) {
			return evaluate((MessageQuery) expression);
		} else {
			MessageCondition condition = (MessageCondition) expression;
			return condition.predicate.evaluate(getEvaluationExpressions(condition));
		}
	}

	private static ObjectSet<Message> getEvaluationExpressions(MessageCondition condition) {
		ObjectSet<Message> messagesById = publishedMessages.get(condition.messageId.hash);
		return messagesById == null
				? emptyMessages
				: messagesById;
	}

	public static void subscribe(MesageSubscriber subscriber) {
		subscribe(subscriber, subscriber.query);
	}

	private static void subscribe(MesageSubscriber subscriber, MessageQuery query) {
		for (Array<MessageExpression> andExpressions : query.oredExpressions) {
			for (MessageExpression expression : andExpressions) {
				if (expression instanceof MessageQuery) {
					subscribe(subscriber, (MessageQuery) expression);
				} else {
					getSubscribers((MessageCondition) expression).add(subscriber);
				}
			}
		}
	}

	private static Array<MesageSubscriber> getSubscribers(MessageCondition condition) {
		int messageHash = condition.messageId.hash;
		Array<MesageSubscriber> messageSubscribers = subscribers.get(messageHash);

		if (messageSubscribers == null) {
			messageSubscribers = new Array<MesageSubscriber>();
			subscribers.put(messageHash, messageSubscribers);
		}

		return messageSubscribers;
	}

	public static void unsubscribe(MesageSubscriber subscriber) {
		unsubscribe(subscriber, subscriber.query);
	}

	private static void unsubscribe(MesageSubscriber subscriber, MessageQuery query) {
		for (Array<MessageExpression> andExpressions : query.oredExpressions) {
			for (MessageExpression expression : andExpressions) {
				if (expression instanceof MessageQuery) {
					unsubscribe(subscriber, (MessageQuery) expression);
				} else {
					unsubscribe(subscriber, (MessageCondition) expression);
				}
			}
		}
	}

	private static void unsubscribe(MesageSubscriber subscriber, MessageCondition condition) {
		Array<MesageSubscriber> eventSubscribers = subscribers.get(condition.messageId.hash);
		if (eventSubscribers != null) {
			eventSubscribers.removeValue(subscriber, true);
		}
	}

	private static class PooledMessage {
		Message message;
		boolean publish;

		PooledMessage set(Message message, boolean publish) {
			this.message = message;
			this.publish = publish;
			return this;
		}
	}

	private static class PooledMessagePool extends Pool<PooledMessage> {
		@Override
		protected PooledMessage newObject() {
			return new PooledMessage();
		}
	}
}
