package com.gurella.engine.asset.persister.object;

import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.asset.persister.AbstractAssetPersister;
import com.gurella.engine.serialization.json.JsonOutput;

public class JsonObjectPersister<T> extends AbstractAssetPersister<T> {
	private final Class<T> expectedType;

	public JsonObjectPersister(FileHandleResolver resolver, Class<T> expectedType) {
		super(resolver);
		this.expectedType = expectedType;
	}

	@Override
	public void persist(FileHandle file, T asset) {
		JsonOutput output = new JsonOutput();
		String string = output.serialize(file, expectedType, asset);
		OutputStream outputStream = file.write(false);

		try {
			outputStream.write(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120).getBytes());
			outputStream.close();
		} catch (Exception e) {
			String message = "Error while saving asset '" + file.path() + "'.";
			//TODO LogService
			Gdx.app.log(JsonObjectPersister.class.getName(), message, e);
			throw new GdxRuntimeException(message, e);
		}
	}
}
