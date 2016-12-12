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

public class NewSceneWizard extends BasicNewResourceWizard {
	private NewSceneCreationPage mainPage;

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new NewSceneCreationPage("newFilePage1", getSelection());//$NON-NLS-1$
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

		IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				IWorkbenchPage page = dw.getActivePage();
				if (page != null) {
					IDE.openEditor(page, file, true);
				}
			}
		} catch (PartInitException e) {
			GurellaStudioPlugin.showError(e, "Problems Opening Editor");
		}

		return true;
	}
}
