package com.gurella.studio.editor.assets;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

class CopyAssetHandler extends AbstractHandler {
	private final AssetsView view;

	public CopyAssetHandler(AssetsView view) {
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		view.copy();
		return null;
	}
}