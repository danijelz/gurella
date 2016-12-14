package com.gurella.studio.wizard.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.IntStream;

public class Executor {
	/**
	 * Execute the Ant script file with the given parameters.
	 * 
	 * @return whether the Ant succeeded
	 */
	public static boolean execute(File workingDir, String parameters, LogCallback callback) {
		String file = System.getProperty("os.name").contains("Windows") ? "gradlew.bat" : "gradlew";
		String exec = workingDir.getAbsolutePath() + "/" + file;
		callback.log("Executing '" + exec + " " + parameters + "'\n");

		String[] params = parameters.split(" ");
		String[] commands = new String[params.length + 1];
		commands[0] = exec;
		IntStream.range(0, params.length).forEach(i -> commands[i + 1] = params[i]);
		return startProcess(commands, workingDir, callback);
	}

	private static boolean startProcess(String[] commands, File directory, final LogCallback callback) {
		try {
			final Process process = new ProcessBuilder(commands).redirectErrorStream(true).directory(directory).start();
			Thread t = new Thread(new StartProcessRunnable(process, callback));
			t.setDaemon(true);
			t.start();
			process.waitFor();
			t.interrupt();
			return process.exitValue() == 0;
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			callback.log(errors.toString());
			return false;
		}
	}

	private static final class StartProcessRunnable implements Runnable {
		private final Process process;
		private final LogCallback callback;

		private StartProcessRunnable(Process process, LogCallback callback) {
			this.process = process;
			this.callback = callback;
		}

		@Override
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
			reader.lines().forEachOrdered(l -> callback.log(l + '\n'));
		}
	}

	public interface LogCallback {
		public void log(String log);
	}

}
