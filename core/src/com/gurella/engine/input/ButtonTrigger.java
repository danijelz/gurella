package com.gurella.engine.input;

class ButtonTrigger implements InputTrigger {
	int button;
	ButtonState buttonState;
	ButtonType buttonType;

	int getButton() {
		return button;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + button;
		result = prime * result + ((buttonState == null)
				? 0
				: buttonState.hashCode());
		result = prime * result + ((buttonType == null)
				? 0
				: buttonType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ButtonTrigger other = (ButtonTrigger) obj;
		if (button != other.button)
			return false;
		if (buttonState != other.buttonState)
			return false;
		if (buttonType != other.buttonType)
			return false;
		return true;
	}

	enum ButtonType {
		KEYBOARD, MOUSE;
	}
}
