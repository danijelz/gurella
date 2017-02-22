package com.gurella.studio.wizard.project.setup;

import static com.gurella.studio.GurellaStudioPlugin.getFileInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceFile extends ProjectFile {
	public final String resourceName;

	public ResourceFile(String resourceName, String outputName) {
		super(outputName);
		this.resourceName = resourceName;
	}

	@Override
	byte[] getContent() {
		String path = SetupConstants.setupFolderLocation + resourceName;
		try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); InputStream in = getFileInputStream(path)) {
			return readBytes(bytes, in);
		} catch (Throwable e) {
			throw new RuntimeException("Couldn't read resource '" + path + "'", e);
		}
	}

	private static byte[] readBytes(ByteArrayOutputStream bytes, InputStream in) throws IOException {
		int read = 0;
		byte[] buffer = new byte[1024 * 10];
		while ((read = in.read(buffer)) > 0) {
			bytes.write(buffer, 0, read);
		}
		return bytes.toByteArray();
	}
}
