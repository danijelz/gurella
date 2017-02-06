package com.gurella.engine.asset.persister.json;

import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.asset.persister.AssetIdResolver;
import com.gurella.engine.asset.persister.AssetPersister;
import com.gurella.engine.serialization.json.JsonOutput;

public class SerializedJsonPersister<T> implements AssetPersister<T>{
	private final Class<T> expectedType;
	private final JsonOutput output = new JsonOutput();

	public SerializedJsonPersister(Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	@Override
	public void persist(AssetIdResolver assetIdResolver, FileHandle file, T asset) {
		//TODO use AssetIdResolver
		String string = output.serialize(file, expectedType, asset);
		OutputStream outputStream = file.write(false);

		try {
			outputStream.write(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120).getBytes());
			outputStream.close();
		} catch (Exception e) {
			String message = "Error while saving asset '" + file.path() + "'.";
			// TODO LogService
			Gdx.app.log(SerializedJsonPersister.class.getName(), message, e);
			throw new GdxRuntimeException(message, e);
		}
	}
}
