package com.gurella.engine.resource;

public class InvalidResourceIdException extends RuntimeException {
	private static final long serialVersionUID = -5251221868237370310L;

	public InvalidResourceIdException(String message) {
		super(message);
	}
}
