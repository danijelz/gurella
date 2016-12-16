package com.gurella.studio.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;

public class NewSceneWizard extends BasicNewResourceWizard {
	private NewScenePage mainPage;

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new NewScenePage("newScenePage", getSelection());//$NON-NLS-1$
		mainPage.setTitle("Scene");
		mainPage.setDescription("Create a new scene.");
		mainPage.setFileExtension("gscn");
		mainPage.setFileName("New_Scene");
		addPage(mainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle("New Scene");
		setNeedsProgressMonitor(true);
	}

	@Override
	protected void initializeDefaultPageImageDescriptor() {
		ImageDescriptor desc = GurellaStudioPlugin.getImageDescriptor("icons/Umbrella-16.png");//$NON-NLS-1$
		setDefaultPageImageDescriptor(desc);
	}

	@Override
	public boolean performFinish() {
		IFile file = mainPage.createNewFile();
		if (file == null) {
			return false;
		}

		selectAndReveal(file);
		Try.successful(file).peek(f -> openEditor(f))
				.onFailure(e -> GurellaStudioPlugin.showError(e, "Problems Opening Editor"));

		return true;
	}

	private void openEditor(IFile file) throws PartInitException {
		IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
		if (dw != null) {
			IWorkbenchPage page = dw.getActivePage();
			if (page != null) {
				IDE.openEditor(page, file, true);
			}
		}
	}
}
