package com.gurella.studio.wizard.project.setup;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.badlogic.gdx.utils.StreamUtils;

public class Executor {
	/**
	 * Execute the Gradle script file with the given parameters.
	 * @return whether execution succeeded
	 */
	public static boolean execute(File workDir, List<String> commands, LogCallback callback) {
		String file = System.getProperty("os.name").contains("Windows") ? "gradlew.bat" : "gradlew";
		String exec = workDir.getAbsolutePath() + "/" + file;
		commands.add(0, exec);
		callback.log("Executing '" + commands.stream().collect(joining(" ")) + "', this might take awhile!\n");
		ProcessListener processListener = null;

		try {
			ProcessBuilder builder = new ProcessBuilder(commands).redirectErrorStream(true).directory(workDir);
			final Process process = builder.start();
			processListener = new ProcessListener(process, callback);
			Thread thread = new Thread(processListener);
			thread.setDaemon(true);
			thread.start();
			process.waitFor();
			thread.interrupt();
			return process.exitValue() == 0;
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			callback.log(errors.toString());
			return false;
		} finally {
			StreamUtils.closeQuietly(processListener);
		}
	}

	private static final class ProcessListener implements Runnable, Closeable {
		private final Process process;
		private final LogCallback callback;
		private BufferedReader reader;

		private ProcessListener(Process process, LogCallback callback) {
			this.process = process;
			this.callback = callback;
		}

		@Override
		public void run() {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
			reader.lines().forEachOrdered(l -> callback.log(l + '\n'));
		}

		@Override
		public void close() {
			StreamUtils.closeQuietly(reader);
		}
	}

	public interface LogCallback {
		public void log(String text);
	}
}
