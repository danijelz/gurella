package com.gurella.engine.scene.renderable.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.metatype.MetaTypeDescriptor;
import com.gurella.engine.scene.renderable.RenderableComponent3d;
import com.gurella.engine.scene.transform.TransformComponent;

@MetaTypeDescriptor(descriptiveName = "Terrain")
public class TerrainComponent extends RenderableComponent3d {
	public static final int DEFAULT_SIZE = 1600;
	public static final int DEFAULT_VERTEX_RESOLUTION = 180;

	private static final MeshPartBuilder.VertexInfo tempVertexInfo = new MeshPartBuilder.VertexInfo();
	private static final Vector3 c00 = new Vector3();
	private static final Vector3 c01 = new Vector3();
	private static final Vector3 c10 = new Vector3();
	private static final Vector3 c11 = new Vector3();

	public float[] heightData;
	public int terrainWidth = DEFAULT_SIZE;
	public int terrainDepth = DEFAULT_SIZE;
	public int vertexResolution = DEFAULT_VERTEX_RESOLUTION;

	private TerrainTexture terrainTexture;
	private final Material material;

	private Model model;
	private ModelInstance modelInstance;
	private Mesh mesh;

	private VertexAttributes attribs;
	private final Vector2 uvScale = new Vector2(60, 60);
	private float vertices[];
	private int stride;
	private int posPos;
	private int norPos;
	private int uvPos;

	public TerrainComponent() {
		this.attribs = MeshBuilder.createAttributes(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
				| VertexAttributes.Usage.TextureCoordinates);
		this.posPos = attribs.getOffset(VertexAttributes.Usage.Position, -1);
		this.norPos = attribs.getOffset(VertexAttributes.Usage.Normal, -1);
		this.uvPos = attribs.getOffset(VertexAttributes.Usage.TextureCoordinates, -1);
		this.stride = attribs.vertexSize / 4;

		this.heightData = new float[vertexResolution * vertexResolution];

		this.terrainTexture = new TerrainTexture();
		this.terrainTexture.setTerrain(this);
		material = new Material();
		material.set(new TerrainTextureAttribute(TerrainTextureAttribute.ATTRIBUTE_SPLAT0, terrainTexture));
	}

	@Override
	protected ModelInstance getModelInstance() {
		if (modelInstance == null) {
			initModelInstance();
		}
		return modelInstance;
	}

	private void initModelInstance() {
		final int numVertices = this.vertexResolution * vertexResolution;
		final int numIndices = (this.vertexResolution - 1) * (vertexResolution - 1) * 6;

		mesh = new Mesh(true, numVertices, numIndices, attribs);
		this.vertices = new float[numVertices * stride];
		mesh.setIndices(buildIndices());
		buildVertices();
		mesh.setVertices(vertices);

		MeshPart meshPart = new MeshPart(null, mesh, 0, numIndices, GL20.GL_TRIANGLES);
		meshPart.update();
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		mb.part(meshPart, material);
		model = mb.end();
		modelInstance = new ModelInstance(model);
	}

	public Vector3 getVertexPosition(Vector3 out, int x, int z) {
		final float dx = (float) x / (float) (vertexResolution - 1);
		final float dz = (float) z / (float) (vertexResolution - 1);
		final float height = heightData[z * vertexResolution + x];
		out.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
		return out;
	}

	public float getHeightAtWorldCoord(float worldX, float worldZ) {
		TransformComponent transformComponent = getTransformComponent();
		if (transformComponent != null) {
			transformComponent.getWorldTranslation(c00);
		}
		float terrainX = worldX - c00.x;
		float terrainZ = worldZ - c00.z;

		float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		c01.set(1, heightData[(gridZ + 1) * vertexResolution + gridX], 0);
		c10.set(0, heightData[gridZ * vertexResolution + gridX + 1], 1);

		// we are in upper left triangle of the square
		if (xCoord <= (1 - zCoord)) {
			c00.set(0, heightData[gridZ * vertexResolution + gridX], 0);
			return barryCentric(c00, c10, c01, new Vector2(zCoord, xCoord));
		}
		// bottom right triangle
		c11.set(1, heightData[(gridZ + 1) * vertexResolution + gridX + 1], 1);
		return barryCentric(c10, c11, c01, new Vector2(zCoord, xCoord));
	}

	public Vector3 getRayIntersection(Vector3 out, Ray ray) {
		// TODO improve performance. use binary search
		float curDistance = 2;
		int rounds = 0;

		ray.getEndPoint(out, curDistance);
		boolean isUnder = isUnderTerrain(out);

		while (true) {
			rounds++;
			ray.getEndPoint(out, curDistance);

			boolean u = isUnderTerrain(out);
			if (u != isUnder || rounds == 10000) {
				return out;
			}
			curDistance += u ? -0.1f : 0.1f;
		}

	}

	private short[] buildIndices() {
		final int w = vertexResolution - 1;
		final int h = vertexResolution - 1;
		short indices[] = new short[w * h * 6];
		int i = -1;
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				final int c00 = y * vertexResolution + x;
				final int c10 = c00 + 1;
				final int c01 = c00 + vertexResolution;
				final int c11 = c10 + vertexResolution;
				indices[++i] = (short) c11;
				indices[++i] = (short) c10;
				indices[++i] = (short) c00;
				indices[++i] = (short) c00;
				indices[++i] = (short) c01;
				indices[++i] = (short) c11;
			}
		}
		return indices;
	}

	private void buildVertices() {
		for (int x = 0; x < vertexResolution; x++) {
			for (int z = 0; z < vertexResolution; z++) {
				calculateVertexAt(tempVertexInfo, x, z);
				calculateNormalAt(tempVertexInfo, x, z);
				setVertex(z * vertexResolution + x, tempVertexInfo);
			}
		}
	}

	private void setVertex(int index, MeshPartBuilder.VertexInfo info) {
		index *= stride;
		if (posPos >= 0) {
			vertices[index + posPos] = info.position.x;
			vertices[index + posPos + 1] = info.position.y;
			vertices[index + posPos + 2] = info.position.z;
		}
		if (uvPos >= 0) {
			vertices[index + uvPos] = info.uv.x;
			vertices[index + uvPos + 1] = info.uv.y;
		}
		if (norPos >= 0) {
			vertices[index + norPos] = info.normal.x;
			vertices[index + norPos + 1] = info.normal.y;
			vertices[index + norPos + 2] = info.normal.z;
		}
	}

	private MeshPartBuilder.VertexInfo calculateVertexAt(MeshPartBuilder.VertexInfo out, int x, int z) {
		final float dx = (float) x / (float) (vertexResolution - 1);
		final float dz = (float) z / (float) (vertexResolution - 1);
		final float height = heightData[z * vertexResolution + x];

		out.position.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
		out.uv.set(dx, dz).scl(uvScale);

		return out;
	}

	private MeshPartBuilder.VertexInfo calculateNormalAt(MeshPartBuilder.VertexInfo out, int x, int y) {
		out.normal.set(getNormalAt(x, y));
		return out;
	}

	public Vector3 getNormalAtWordCoordinate(float worldX, float worldZ) {
		TransformComponent transformComponent = getTransformComponent();
		if (transformComponent != null) {
			transformComponent.getWorldTranslation(c00);
		}
		float terrainX = worldX - c00.x;
		float terrainZ = worldZ - c00.z;

		float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
			return Vector3.Y.cpy();
		}

		return getNormalAt(gridX, gridZ);
	}

	public Vector3 getNormalAt(int x, int y) {
		Vector3 out = new Vector3();
		// handle edges of terrain
		int xP1 = (x + 1 >= vertexResolution) ? vertexResolution - 1 : x + 1;
		int yP1 = (y + 1 >= vertexResolution) ? vertexResolution - 1 : y + 1;
		int xM1 = (x - 1 < 0) ? 0 : x - 1;
		int yM1 = (y - 1 < 0) ? 0 : y - 1;

		float hL = heightData[y * vertexResolution + xM1];
		float hR = heightData[y * vertexResolution + xP1];
		float hD = heightData[yM1 * vertexResolution + x];
		float hU = heightData[yP1 * vertexResolution + x];
		out.x = hL - hR;
		out.y = 2;
		out.z = hD - hU;
		out.nor();
		return out;
	}

	public boolean isUnderTerrain(Vector3 worldCoords) {
		float terrainHeight = getHeightAtWorldCoord(worldCoords.x, worldCoords.z);
		return terrainHeight > worldCoords.y;
	}

	public boolean isOnTerrain(float worldX, float worldZ) {
		TransformComponent transformComponent = getTransformComponent();
		if (transformComponent != null) {
			transformComponent.getWorldTranslation(c00);
		}
		return worldX >= c00.x && worldX <= c00.x + terrainWidth && worldZ >= c00.z && worldZ <= c00.z + terrainDepth;
	}

	public Vector3 getPosition(Vector3 out) {
		TransformComponent transformComponent = getTransformComponent();
		if (transformComponent != null) {
			transformComponent.getWorldTranslation(out);
		} else {
			out.setZero();
		}
		return out;
	}

	public TerrainTexture getTerrainTexture() {
		return terrainTexture;
	}

	public void setTerrainTexture(TerrainTexture terrainTexture) {
		if (terrainTexture == null) {
			return;
		}

		terrainTexture.setTerrain(this);
		this.terrainTexture = terrainTexture;

		material.set(new TerrainTextureAttribute(TerrainTextureAttribute.ATTRIBUTE_SPLAT0, terrainTexture));
	}

	public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	private void updateVertices() {
		buildVertices();
		mesh.setVertices(vertices);
	}

	@Override
	public void reset() {
		super.reset();
		if (mesh != null) {
			mesh.dispose();
			mesh = null;
			model.dispose();
			model = null;
		}
	}
}
