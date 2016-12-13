package com.gurella.studio.wizard.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {
	public interface CharCallback {
		public void character(char c);
	}

	/**
	 * Execute the Ant script file with the given parameters.
	 * 
	 * @return whether the Ant succeeded
	 */
	public static boolean execute(File workingDir, String windowsFile, String unixFile, String parameters,
			CharCallback callback) {
		String exec = workingDir.getAbsolutePath() + "/"
				+ (System.getProperty("os.name").contains("Windows") ? windowsFile : unixFile);
		String log = "Executing '" + exec + " " + parameters + "'";
		for (int i = 0; i < log.length(); i++) {
			callback.character(log.charAt(i));
		}
		callback.character('\n');

		String[] params = parameters.split(" ");
		String[] commands = new String[params.length + 1];
		commands[0] = exec;
		for (int i = 0; i < params.length; i++) {
			commands[i + 1] = params[i];
		}

		return startProcess(commands, workingDir, callback);
	}

	private static boolean startProcess(String[] commands, File directory, final CharCallback callback) {
		try {
			final Process process = new ProcessBuilder(commands).redirectErrorStream(true).directory(directory).start();

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
					try {
						int c = 0;
						while ((c = reader.read()) != -1) {
							callback.character((char) c);
						}
					} catch (IOException e) {
						// e.printStackTrace();
					}
				}
			});
			t.setDaemon(true);
			t.start();
			process.waitFor();
			t.interrupt();
			return process.exitValue() == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
