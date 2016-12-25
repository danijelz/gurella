package com.gurella.studio.wizard.project;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

interface Validator {
	List<IStatus> validate();
}
