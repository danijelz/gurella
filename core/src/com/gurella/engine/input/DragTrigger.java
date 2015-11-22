package com.gurella.engine.input;

class DragTrigger implements InputTrigger {
	int pointer;
	DragDirection direction;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null)
				? 0
				: direction.hashCode());
		result = prime * result + pointer;
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
		DragTrigger other = (DragTrigger) obj;
		if (direction != other.direction)
			return false;
		if (pointer != other.pointer)
			return false;
		return true;
	}

	public enum DragDirection {
		HORISONTAL, VERTICAL;
	}
}
