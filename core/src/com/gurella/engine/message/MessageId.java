package com.gurella.engine.message;

import com.badlogic.gdx.utils.ObjectMap;

public class MessageId {
	private static ObjectMap<Object, MessageId> messageIds = new ObjectMap<Object, MessageId>();

	private static int hashIndex = 0;

	public final int hash;

	public MessageId() {
		hash = hashIndex++;
	}

	public static MessageId fromValue(Object value) {
		MessageId messageId = messageIds.get(value);

		if (messageId == null) {
			messageId = new MessageId();
			messageIds.put(value, messageId);
		}

		return messageId;
	}
}
