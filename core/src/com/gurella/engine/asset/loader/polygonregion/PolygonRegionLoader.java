package com.gurella.engine.asset.loader.polygonregion;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.gurella.engine.asset.loader.AssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PolygonRegionLoader implements AssetLoader<PolygonRegion, PolygonRegionProperties> {
	public static final String texturePrefix = "i ";
	public static final String verticesPrefix = "s ";

	private EarClippingTriangulator triangulator = new EarClippingTriangulator();

	private String imagePath;
	private float[] vertices;
	private PolygonRegion polygonRegion;

	@Override
	public Class<PolygonRegionProperties> getPropertiesType() {
		return PolygonRegionProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle file) {
		imagePath = null;
		vertices = null;
		polygonRegion = null;

		BufferedReader reader = null;
		try {
			reader = file.reader(1024);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.startsWith(texturePrefix)) {
					imagePath = file.sibling(line.substring(texturePrefix.length())).path();
				} else if (line.startsWith(verticesPrefix)) {
					String[] verticeStrings = line.substring(verticesPrefix.length()).trim().split(",");
					float[] vertices = new float[verticeStrings.length];
					for (int i = 0, n = vertices.length; i < n; i++) {
						vertices[i] = Float.parseFloat(verticeStrings[i]);
					}
				}

				if (imagePath != null && vertices != null) {
					break;
				}
			}
		} catch (IOException e) {
			throw new GdxRuntimeException("Error reading " + file.path(), e);
		} finally {
			StreamUtils.closeQuietly(reader);
		}

		if (imagePath == null) {
			throw new GdxRuntimeException("Error reading " + file.path() + ". No image file specified.");
		} else if (vertices == null) {
			throw new GdxRuntimeException("Error reading " + file.path() + ". No vertices specified.");
		}

		collector.addDependency(imagePath, file.type(), Texture.class);
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle file, PolygonRegionProperties properties) {
		Texture texture = provider.getDependency(imagePath, file.type(), Texture.class, null);
		polygonRegion = new PolygonRegion(new TextureRegion(texture), vertices,
				triangulator.computeTriangles(vertices).toArray());
	}

	@Override
	public PolygonRegion finish(DependencySupplier provider, FileHandle file, PolygonRegionProperties properties) {
		PolygonRegion result = polygonRegion;
		imagePath = null;
		vertices = null;
		polygonRegion = null;
		return result;
	}
}
