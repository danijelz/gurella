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
	
		List<String> incompatList = builder.buildProject(modules, dependencies);
		if (incompatList.size() == 0) {
			try {
				builder.build();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			for (String subIncompat : incompatList) {
				JLabel label = new JLabel(subIncompat);
				label.setAlignmentX(Component.CENTER_ALIGNMENT);
				panel.add(label);
			}
	
			JLabel infoLabel = new JLabel(
					"<html><br><br>The project can be generated, but you wont be able to use these extensions in the respective sub modules<br>Please see the link to learn about extensions</html>");
			infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(infoLabel);
			JEditorPane pane = new JEditorPane("text/html",
					"<a href=\"https://github.com/libgdx/libgdx/wiki/Dependency-management-with-Gradle\">Dependency Management</a>");
			pane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							Desktop.getDesktop().browse(new URI(e.getURL().toString()));
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
				}
			});
			pane.setEditable(false);
			pane.setOpaque(false);
			pane.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(pane);
	
			Object[] options = { "Yes, build it!", "No, I'll change my extensions" };
			int value = JOptionPane.showOptionDialog(null, panel, "Extension Incompatibilities",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
			if (value != 0) {
				return;
			} else {
				try {
					builder.build();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
}
