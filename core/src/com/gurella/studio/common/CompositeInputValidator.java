package com.gurella.studio.common;

import com.kotcrab.vis.ui.InputValidator;

public class CompositeInputValidator implements InputValidator {
	private InputValidator[] delegates;

	public CompositeInputValidator(InputValidator... delegates) {
		this.delegates = delegates;
	}

	@Override
	public boolean validateInput(String input) {
		for (InputValidator delegate : delegates) {
			if(!delegate.validateInput(input)) {
				return false;
			}
		}
		return true;
	}
}
