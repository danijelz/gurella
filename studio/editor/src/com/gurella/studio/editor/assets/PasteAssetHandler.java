package com.gurella.studio.editor.assets;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

class PasteAssetHandler extends AbstractHandler {
	private final AssetsView view;

	public PasteAssetHandler(AssetsView view) {
		this.view = view;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		view.paste();
		return null;
	}
}