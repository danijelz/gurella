package com.gurella.engine.base.object;

import java.io.IOException;
import java.io.OutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.resource.FileService;
import com.gurella.engine.base.serialization.json.JsonOutput;

public final class Prefabs {
	private Prefabs() {
	}

	public static <T extends ManagedObject> void saveAsPrefab(T object, Class<? super T> expectedType,
			String fileName) {
		String fileUuid = FileService.getUuid(fileName);
		System.out.println("fileUuid: " + fileUuid);
		T prefab = new CopyContext().copy(object);
		save(prefab, expectedType, fileName);
		object.prefab = new PrefabReference(fileUuid, prefab.getUuid());
	}

	public static <T extends ManagedObject> void save(T object, Class<? super T> expectedType, String fileName) {
		FileHandle handle = Gdx.files.absolute(fileName);
		if (handle.exists()) {
			// TODO exception
		}
		JsonOutput output = new JsonOutput();
		String string = output.serialize(expectedType, object);
		OutputStream outputStream = handle.write(false);

		try {
			outputStream.write(new JsonReader().parse(string).prettyPrint(OutputType.minimal, 120).getBytes());
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("uuid: " + object.getUuid());
	}
}
