package com.gurella.engine.input;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.event.Listener0;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.event.Listener2;
import com.gurella.engine.input.ButtonTrigger.ButtonType;
import com.gurella.engine.input.DragTrigger.DragDirection;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

public class InputContext implements Comparable<InputContext> {
	public int priority;

	private Listener1<Character> keyTypedListener;

	private Listener1<Integer> defaultKeyPressedActionListener;
	private Listener1<Integer> defaultKeyReleasedActionListener;
	private Listener2<ButtonState, Integer> defaultKeyStateListener;

	private Listener1<Integer> defaultButtonPressedActionListener;
	private Listener1<Integer> defaultButtonReleasedActionListener;
	private Listener2<ButtonState, Integer> defaultButtonStateListener;

	private Listener2<Integer, Float> defaultHorisontalTouchMoveListener;
	private Listener2<Integer, Float> defaultVerticalTouchMoveListener;

	private ObjectMap<ButtonTrigger, Listener0> actionBindings = new ObjectMap<ButtonTrigger, Listener0>();
	private ObjectMap<ButtonTrigger, Listener1<ButtonState>> stateBindings = new ObjectMap<ButtonTrigger, Listener1<ButtonState>>();
	private ObjectMap<InputTrigger, Listener1<Float>> rangeBindings = new ObjectMap<InputTrigger, Listener1<Float>>();

	public void activate() {
		InputService.addInputContext(this);
	}

	public void deactivate() {
		InputService.removeInputContext(this);
	}

	public void bindKeyTypedListener(Listener1<Character> newKeyTypedListener) {
		this.keyTypedListener = newKeyTypedListener;
	}

	boolean handleKeyTyped(char character) {
		if (keyTypedListener != null) {
			keyTypedListener.handle(Character.valueOf(character));
			return true;
		} else {
			return false;
		}
	}

	public InputContext bindDefaultKeyActionListener(ButtonState buttonState,
			Listener1<Integer> defaultKeyActionListener) {
		switch (buttonState) {
		case PRESSED:
			defaultKeyPressedActionListener = defaultKeyActionListener;
			break;
		case RELEASED:
			defaultKeyReleasedActionListener = defaultKeyActionListener;
			break;
		}
		return this;
	}

	public InputContext unbindDefaultKeyActionListener(ButtonState buttonState) {
		switch (buttonState) {
		case PRESSED:
			defaultKeyPressedActionListener = null;
			break;
		case RELEASED:
			defaultKeyReleasedActionListener = null;
			break;
		}
		return this;
	}

	public InputContext bindDefaultKeyStateListener(Listener2<ButtonState, Integer> newDefaultKeyStateListener) {
		this.defaultKeyStateListener = newDefaultKeyStateListener;
		return this;
	}

	public InputContext unbindDefaultKeyStateListener() {
		this.defaultKeyStateListener = null;
		return this;
	}

	public InputContext bindKeyActionListener(int keycode, ButtonState buttonState, Listener0 listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = buttonState;
		actionBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindKeyActionListener(int keycode, ButtonState buttonState) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = buttonState;
		actionBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	public InputContext bindKeyStateListener(int keycode, Listener1<ButtonState> listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = null;
		stateBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindKeyStateListener(int keycode) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = null;
		stateBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	public InputContext bindKeyRangeListener(int keycode, Listener1<Float> listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = null;
		rangeBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindKeyRangeListener(int keycode) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.KEYBOARD;
		buttonTrigger.button = keycode;
		buttonTrigger.buttonState = null;
		rangeBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	public InputContext bindDefaultButtonActionListener(ButtonState buttonState,
			Listener1<Integer> defaultButtonActionListener) {
		switch (buttonState) {
		case PRESSED:
			defaultButtonPressedActionListener = defaultButtonActionListener;
			break;
		case RELEASED:
			defaultButtonReleasedActionListener = defaultButtonActionListener;
			break;
		}
		return this;
	}

	public InputContext unbindDefaultButtonActionListener(ButtonState buttonState) {
		switch (buttonState) {
		case PRESSED:
			defaultButtonPressedActionListener = null;
			break;
		case RELEASED:
			defaultButtonReleasedActionListener = null;
			break;
		}
		return this;
	}

	public InputContext bindDefaultButtonStateListener(Listener2<ButtonState, Integer> newDefaultButtonStateListener) {
		this.defaultButtonStateListener = newDefaultButtonStateListener;
		return this;
	}

	public InputContext unbindDefaultButtonStateListener() {
		this.defaultButtonStateListener = null;
		return this;
	}

	public InputContext bindTouchButtonActionListener(int button, ButtonState buttonState, Listener0 listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = buttonState;
		actionBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindTouchButtonActionListener(int button, ButtonState buttonState) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = buttonState;
		actionBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	public InputContext bindTouchButtonStateListener(int button, Listener1<ButtonState> listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = null;
		stateBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindTouchButtonStateListener(int button) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = null;
		stateBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	public InputContext bindTouchButtonRangeListener(int button, Listener1<Float> listener) {
		ButtonTrigger buttonTrigger = new ButtonTrigger();
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = null;
		rangeBindings.put(buttonTrigger, listener);
		return this;
	}

	public InputContext unbindTouchButtonRangeListener(int button) {
		ButtonTrigger buttonTrigger = PoolService.obtain(ButtonTrigger.class);
		buttonTrigger.buttonType = ButtonType.MOUSE;
		buttonTrigger.button = button;
		buttonTrigger.buttonState = null;
		rangeBindings.remove(buttonTrigger);
		PoolService.free(buttonTrigger);
		return this;
	}

	boolean handleButton(ButtonTrigger buttonTrigger) {
		boolean handled = false;

		Listener0 actionListener = actionBindings.get(buttonTrigger);
		if (actionListener != null) {
			actionListener.handle();
			handled = true;
		} else {
			Listener1<Integer> defaultActionListener = getDefaultActionListener(buttonTrigger);
			if (defaultActionListener != null) {
				defaultActionListener.handle(Integer.valueOf(buttonTrigger.getButton()));
				handled = true;
			}
		}

		ButtonState buttonState = buttonTrigger.buttonState;
		buttonTrigger.buttonState = null;

		Listener1<ButtonState> stateListener = stateBindings.get(buttonTrigger);
		if (stateListener != null) {
			stateListener.handle(buttonState);
			handled = true;
		} else {
			Listener2<ButtonState, Integer> defaultStateListener = getDefaultStateListener(buttonTrigger);
			if (defaultStateListener != null) {
				defaultStateListener.handle(buttonState, Integer.valueOf(buttonTrigger.getButton()));
				handled = true;
			}
		}

		Listener1<Float> rangeListener = rangeBindings.get(buttonTrigger);
		if (rangeListener != null) {
			rangeListener.handle(Float.valueOf(ButtonState.RELEASED == buttonState ? 0f : 1f));
			handled = true;
		}

		buttonTrigger.buttonState = buttonState;

		return handled;
	}

	private Listener1<Integer> getDefaultActionListener(ButtonTrigger buttonTrigger) {
		switch (buttonTrigger.buttonType) {
		case KEYBOARD:
			switch (buttonTrigger.buttonState) {
			case PRESSED:
				return defaultKeyPressedActionListener;
			case RELEASED:
				return defaultKeyReleasedActionListener;
			default:
				throw new GdxRuntimeException("Invalid button state: " + buttonTrigger.buttonState);
			}
		case MOUSE:
			switch (buttonTrigger.buttonState) {
			case PRESSED:
				return defaultButtonPressedActionListener;
			case RELEASED:
				return defaultButtonReleasedActionListener;
			default:
				throw new GdxRuntimeException("Invalid button state: " + buttonTrigger.buttonState);
			}
		default:
			throw new GdxRuntimeException("Invalid button type: " + buttonTrigger.buttonType);
		}
	}

	private Listener2<ButtonState, Integer> getDefaultStateListener(ButtonTrigger buttonTrigger) {
		switch (buttonTrigger.buttonType) {
		case KEYBOARD:
			return defaultKeyStateListener;
		case MOUSE:
			return defaultButtonStateListener;
		default:
			throw new IllegalArgumentException();
		}
	}

	public InputContext bindScrollRangeTrigger(Listener1<Float> listener) {
		rangeBindings.put(ScrollTrigger.INSTANCE, listener);
		return this;
	}

	boolean handleScroll(int amount) {
		Listener1<Float> listener = rangeBindings.get(ScrollTrigger.INSTANCE);
		if (listener == null) {
			return false;
		} else {
			listener.handle(Float.valueOf(amount));
			return true;
		}
	}

	public InputContext bindDefaultTouchMoveListener(DragDirection moveDirection,
			Listener2<Integer, Float> defaultTouchMoveListener) {
		switch (moveDirection) {
		case HORISONTAL:
			this.defaultHorisontalTouchMoveListener = defaultTouchMoveListener;
			break;
		case VERTICAL:
			this.defaultVerticalTouchMoveListener = defaultTouchMoveListener;
			break;
		}
		return this;
	}

	public InputContext unbindDefaultTouchMoveListener(DragDirection moveDirection) {
		switch (moveDirection) {
		case HORISONTAL:
			this.defaultHorisontalTouchMoveListener = null;
			break;
		case VERTICAL:
			this.defaultVerticalTouchMoveListener = null;
			break;
		}
		return this;
	}

	public InputContext bindTouchMovedRangeListener(DragDirection direction, int pointer, Listener1<Float> listener) {
		DragTrigger dragTrigger = new DragTrigger();
		dragTrigger.direction = direction;
		dragTrigger.pointer = pointer;
		rangeBindings.put(dragTrigger, listener);
		return this;
	}

	public InputContext unbindTouchMovedRangeListener(DragDirection direction, int pointer) {
		DragTrigger dragTrigger = PoolService.obtain(DragTrigger.class);
		dragTrigger.direction = direction;
		dragTrigger.pointer = pointer;
		rangeBindings.remove(dragTrigger);
		PoolService.free(dragTrigger);
		return this;
	}

	boolean handleTouchMoved(DragTrigger dragTrigger, int delta) {
		Listener1<Float> listener = rangeBindings.get(dragTrigger);
		if (listener == null) {
			Listener2<Integer, Float> defaultTouchMoveListener = getDefaultTouchMoveListener(dragTrigger.direction);
			if (defaultTouchMoveListener == null) {
				return false;
			} else {
				defaultTouchMoveListener.handle(Integer.valueOf(dragTrigger.pointer), Float.valueOf(delta));
				return true;
			}
		} else {
			listener.handle(Float.valueOf(delta));
			return true;
		}
	}

	private Listener2<Integer, Float> getDefaultTouchMoveListener(DragDirection moveDirection) {
		switch (moveDirection) {
		case HORISONTAL:
			return defaultHorisontalTouchMoveListener;
		case VERTICAL:
			return defaultVerticalTouchMoveListener;
		default:
			return null;
		}
	}

	@Override
	public int compareTo(InputContext other) {
		return Values.compare(priority, other.priority);
	}
}
