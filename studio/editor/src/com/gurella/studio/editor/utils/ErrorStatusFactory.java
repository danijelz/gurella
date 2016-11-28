package com.gurella.studio.editor.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.studio.GurellaStudioPlugin;

public interface ErrorStatusFactory {
	default IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, GurellaStudioPlugin.PLUGIN_ID, 2, message, exception);
	}
}
