package com.gurella.engine.message.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.events.UpdateEvent;
import com.gurella.engine.application.events.UpdateListener;
import com.gurella.engine.event.EventService;
import com.gurella.engine.message.Message;
import com.gurella.engine.message.MessageCenter;
import com.gurella.engine.message.MessageId;
import com.gurella.engine.message.input.KeyboardMessages.KeyboardAction;
import com.gurella.engine.message.input.KeyboardMessages.KeyboardMessage;
import com.gurella.engine.utils.SynchronizedPools;

public class KeyboardMessenger extends InputAdapter implements UpdateListener {
	private IntMap<Message> temporaryMessages = new IntMap<Message>();

	@Override
	public boolean keyDown(int keycode) {
		publishMessage(keycode, KeyboardAction.JUST_PRESSED);
		publishMessage(keycode, KeyboardAction.PRESSED);
		return false;
	}

	private void publishMessage(int keycode, KeyboardAction action) {
		Message message = SynchronizedPools.obtain(Message.class);
		MessageId messageId = MessageId.fromValue(KeyboardMessage.getInstance(keycode, action));
		message.messageId = messageId;

		if (action != KeyboardAction.PRESSED) {
			if (temporaryMessages.size == 0) {
				EventService.addListener(UpdateEvent.class, this);
			}

			temporaryMessages.put(messageId.hash, message);
		}

		MessageCenter.publish(message);
	}

	@Override
	public boolean keyUp(int keycode) {
		suppressMesage(keycode, KeyboardAction.JUST_PRESSED);
		suppressMesage(keycode, KeyboardAction.PRESSED);
		publishMessage(keycode, KeyboardAction.RELEASED);
		return false;
	}

	private void suppressMesage(int keycode, KeyboardAction action) {
		MessageId messageId = MessageId.fromValue(KeyboardMessage.getInstance(keycode, action));
		Message message = temporaryMessages.remove(messageId.hash);

		if (message != null) {
			suppressMesage(message);
		}
	}

	private static void suppressMesage(Message message) {
		MessageCenter.suppress(message);
		SynchronizedPools.free(message);
	}

	@Override
	public void update() {
		if (temporaryMessages.size > 0) {
			for (Message message : temporaryMessages.values()) {
				suppressMesage(message);
			}
			temporaryMessages.clear();
			EventService.removeListener(UpdateEvent.class, this);
		}
	}

	@Override
	public int getPriority() {
		return CommonUpdateOrder.INPUT;
	}
}
