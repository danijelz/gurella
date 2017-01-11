package com.gurella.studio.wizard.project.setup;

import java.io.File;

//TODO unused
public class SetupUtils {
	private SetupUtils() {
	}

	private static boolean isEmptyDirectory(String destination) {
		File file = new File(destination);
		return file.exists() ? file.list().length == 0 : true;
	}

	//////////////////////////////////TODO

	/*private void buildProjects(Setup builder) throws InvocationTargetException, InterruptedException {
		if (!GdxSetup.isEmptyDirectory(destination)) {
			int value = JOptionPane.showConfirmDialog(this, "The destination is not empty, do you want to overwrite?",
					"Warning!", JOptionPane.YES_NO_OPTION);
			if (value != 0) {
				return;
			}
		}
	}*/
}
