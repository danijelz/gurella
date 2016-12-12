package com.gurella.studio.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.badlogic.gdx.setup.Dependency;
import com.badlogic.gdx.setup.DependencyBank;
import com.badlogic.gdx.setup.DependencyBank.ProjectDependency;
import com.badlogic.gdx.setup.DependencyBank.ProjectType;
import com.badlogic.gdx.setup.ProjectBuilder;

public class NewProjectWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		DependencyBank bank = new DependencyBank();
		ProjectBuilder builder = new ProjectBuilder(bank);
		List<ProjectType> modules = new ArrayList<ProjectType>();
		modules.add(ProjectType.CORE);
		modules.add(ProjectType.DESKTOP);
		modules.add(ProjectType.ANDROID);
		modules.add(ProjectType.IOS);
		// Gwt has no friends
		//modules.add(ProjectType.GWT);

		List<Dependency> dependencies = new ArrayList<Dependency>();
		dependencies.add(bank.getDependency(ProjectDependency.GDX));
		dependencies.add(bank.getDependency(ProjectDependency.BULLET));
		dependencies.add(bank.getDependency(ProjectDependency.FREETYPE));

		List<String> incompatList = builder.buildProject(modules, dependencies);
		
		// TODO Auto-generated method stub
		return false;
	}
}
