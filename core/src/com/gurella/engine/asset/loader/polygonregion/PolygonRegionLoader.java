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
import com.gurella.engine.asset.loader.BaseAssetLoader;
import com.gurella.engine.asset.loader.DependencyCollector;
import com.gurella.engine.asset.loader.DependencySupplier;

public class PolygonRegionLoader extends BaseAssetLoader<PolygonRegion, PolygonRegionProperties> {
	public static final String texturePrefix = "i ";
	public static final String verticesPrefix = "s ";

	private EarClippingTriangulator triangulator = new EarClippingTriangulator();

	@Override
	public Class<PolygonRegionProperties> getPropertiesType() {
		return PolygonRegionProperties.class;
	}

	@Override
	public void initDependencies(DependencyCollector collector, FileHandle assetFile) {
		String imagePath = null;
		float[] vertices = null;
		BufferedReader reader = null;

		try {
			reader = assetFile.reader(1024);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.startsWith(texturePrefix)) {
					imagePath = assetFile.sibling(line.substring(texturePrefix.length())).path();
				} else if (line.startsWith(verticesPrefix)) {
					String[] verticeStrings = line.substring(verticesPrefix.length()).trim().split(",");
					vertices = new float[verticeStrings.length];
					for (int i = 0, n = vertices.length; i < n; i++) {
						vertices[i] = Float.parseFloat(verticeStrings[i]);
					}
				}

				if (imagePath != null && vertices != null) {
					break;
				}
			}
		} catch (IOException e) {
			throw new GdxRuntimeException("Error reading " + assetFile.path(), e);
		} finally {
			StreamUtils.closeQuietly(reader);
		}

		if (imagePath == null) {
			throw new GdxRuntimeException("Error reading " + assetFile.path() + ". No image file specified.");
		} else if (vertices == null) {
			throw new GdxRuntimeException("Error reading " + assetFile.path() + ". No vertices specified.");
		}

		collector.collectDependency(imagePath, assetFile.type(), Texture.class);
		put(assetFile, new PolygonRegionData(imagePath, vertices));
	}

	@Override
	public void processAsync(DependencySupplier provider, FileHandle assetFile, PolygonRegionProperties properties) {
		PolygonRegionData polygonRegionData = get(assetFile);
		String imagePath = polygonRegionData.imagePath;
		float[] vertices = polygonRegionData.vertices;
		Texture texture = provider.getDependency(imagePath, assetFile.type(), Texture.class, null);
		short[] triangles = triangulator.computeTriangles(vertices).toArray();
		polygonRegionData.polygonRegion = new PolygonRegion(new TextureRegion(texture), vertices, triangles);
	}

	@Override
	public PolygonRegion finish(DependencySupplier provider, FileHandle assetFile, PolygonRegionProperties properties) {
		PolygonRegionData polygonRegionData = remove(assetFile);
		return polygonRegionData.polygonRegion;
	}

	//TODO get from pool?
	private static class PolygonRegionData {
		String imagePath;
		float[] vertices;
		PolygonRegion polygonRegion;

		public PolygonRegionData(String imagePath, float[] vertices) {
			this.imagePath = imagePath;
			this.vertices = vertices;
		}
	}
}
