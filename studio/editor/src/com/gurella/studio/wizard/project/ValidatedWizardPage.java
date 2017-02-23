package com.gurella.studio.wizard.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;

abstract class ValidatedWizardPage extends WizardPage {
	private List<Validator> validators = new ArrayList<>();

	protected ValidatedWizardPage(String pageName) {
		super(pageName);
	}

	protected void addValidator(Validator validator) {
		validators.add(validator);
	}

	protected void removeValidator(Validator validator) {
		validators.remove(validator);
	}

	protected void validate() {
		IStatus status = validators.stream().flatMap(v -> v.validate().stream())
				.sorted((s1, s2) -> Integer.compare(s2.getSeverity(), s1.getSeverity())).findFirst().orElse(null);

		if (status == null) {
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			return;
		}

		switch (status.getSeverity()) {
		case IStatus.OK:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			break;
		case IStatus.INFO:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(status.getMessage(), IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			setPageComplete(false);
			setErrorMessage(null);
			setMessage(status.getMessage(), IMessageProvider.WARNING);
			break;
		case IStatus.ERROR:
		case IStatus.CANCEL:
			setPageComplete(false);
			setMessage(null);
			setErrorMessage(status.getMessage());
			break;
		default:
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			break;
		}
	}
}
