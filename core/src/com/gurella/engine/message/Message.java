package com.gurella.engine.message;

import com.badlogic.gdx.utils.Pool.Poolable;

public class Message implements Poolable {
	public MessageId messageId;

	public static Message fromValue(Object value) {
		Message message = new Message();
		message.messageId = MessageId.fromValue(value);
		return message;
	}

	public void publish() {
		MessageCenter.publish(this);
	}

	public void suppress() {
		MessageCenter.suppress(this);
	}

	@Override
	public void reset() {
		messageId = null;
	}
}
