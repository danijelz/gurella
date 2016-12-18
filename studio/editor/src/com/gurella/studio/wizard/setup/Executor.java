package com.gurella.studio.wizard.setup;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Executor {
	/**
	 * Execute the Ant script file with the given parameters.
	 * 
	 * @return whether the Ant succeeded
	 */
	public static boolean execute(File workingDir, List<String> commands, LogCallback callback) {
		String file = System.getProperty("os.name").contains("Windows") ? "gradlew.bat" : "gradlew";
		String exec = workingDir.getAbsolutePath() + "/" + file;
		commands.add(0, exec);
		callback.log("Executing '" + commands.stream().collect(joining(" ")) + "'\n");
		return startProcess(commands, workingDir, callback);
	}

	private static boolean startProcess(List<String> commands, File directory, final LogCallback callback) {
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
		public void log(String text);
	}
}
