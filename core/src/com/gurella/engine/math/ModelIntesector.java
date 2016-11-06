package com.gurella.engine.math;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ModelIntesector {
	private final BoundingBox temp = new BoundingBox();
	private final Matrix4 transform = new Matrix4();
	private final Ray invRay = new Ray();
	private final Vector3 intersection = new Vector3();

	private ModelInstance closestModelInstance; //TODO unused
	private final Vector3 closestIntersection = new Vector3();
	private float closestDistance = Float.MAX_VALUE;

	private final Vector3 closestNodeIntersection = new Vector3();
	private float closestNodeDistance = Float.MAX_VALUE;

	private final Vector3 closestPartIntersection = new Vector3();
	private float closestPartDistance = Float.MAX_VALUE;

	private final Vector3 t1 = new Vector3();
	private final Vector3 t2 = new Vector3();
	private final Vector3 t3 = new Vector3();

	private final Vector3 cameraPosition = new Vector3();
	private final Ray ray = new Ray();

	private ModelInstance modelInstance;

	public boolean getIntersection(Vector3 cameraPosition, Ray ray, ModelInstance modelInstance,
			Intersection intersection) {
		init(cameraPosition, ray);
		process(modelInstance);
		intersection.distance = closestDistance;
		return extractResult(intersection.location);
	}

	public boolean getIntersection(Vector3 cameraPosition, Ray ray, ModelInstance modelInstance, Vector3 intersection) {
		init(cameraPosition, ray);
		process(modelInstance);
		return extractResult(intersection);
	}

	void init(Vector3 cameraPosition, Ray ray) {
		this.cameraPosition.set(cameraPosition);
		this.ray.set(ray);
		closestIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		closestDistance = Float.MAX_VALUE;
	}

	boolean process(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;

		Array<Node> nodes = modelInstance.nodes;
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (getIntersection(node)) {
				float distance = closestNodeIntersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					closestIntersection.set(closestNodeIntersection);
					closestModelInstance = modelInstance;
				}
			}
		}

		return closestModelInstance == modelInstance;
	}

	boolean extractResult(Vector3 intersection) {
		modelInstance = null;
		if (closestModelInstance != null) {
			closestModelInstance = null;
			intersection.set(closestIntersection);
			return true;
		} else {
			return false;
		}
	}

	private boolean getIntersection(Node node) {
		closestNodeIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		closestNodeDistance = Float.MAX_VALUE;

		node.calculateWorldTransform();
		node.extendBoundingBox(temp.inf(), true);
		temp.mul(modelInstance.transform);

		if (Intersector.intersectRayBoundsFast(ray, temp)) {
			transform.set(modelInstance.transform).mul(node.globalTransform);
			if (Matrix4.inv(transform.val)) {
				invRay.set(ray);
				invRay.mul(transform);
			}

			Array<NodePart> parts = node.parts;
			for (int i = 0, n = parts.size; i < n; i++) {
				NodePart nodePart = parts.get(i);
				if (nodePart.enabled) {
					MeshPart meshPart = nodePart.meshPart;
					int primitiveType = meshPart.primitiveType;
					Mesh mesh = meshPart.mesh;
					int offset = meshPart.offset;
					int count = meshPart.size;

					if (getIntersection(mesh, primitiveType, offset, count)) {
						float distance = closestPartIntersection.dst2(cameraPosition);
						if (closestNodeDistance > distance) {
							closestNodeDistance = distance;
							closestNodeIntersection.set(closestPartIntersection);
						}
					}
				}
			}
		}

		return closestNodeDistance != Float.MAX_VALUE;
	}

	private boolean getIntersection(Mesh mesh, int primitiveType, int offset, int count) {
		closestPartIntersection.set(Float.NaN, Float.NaN, Float.NaN);
		closestPartDistance = Float.MAX_VALUE;

		t1.setZero();
		t2.setZero();
		t3.setZero();

		final int numIndices = mesh.getNumIndices();
		final int numVertices = mesh.getNumVertices();
		final int max = numIndices == 0 ? numVertices : numIndices;
		if (offset < 0 || count < 1 || offset + count > max) {
			return false;
		}

		final FloatBuffer verts = mesh.getVerticesBuffer();
		final ShortBuffer index = mesh.getIndicesBuffer();
		final VertexAttribute posAttrib = mesh.getVertexAttribute(Usage.Position);
		final int posoff = posAttrib.offset / 4;
		final int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
		final int end = offset + count;

		switch (posAttrib.numComponents) {
		case 1:
			if (numIndices > 0) {
				for (int i = offset; i < end;) {
					int idx = index.get(i++) * vertexSize + posoff;
					t1.set(verts.get(idx), 0, 0);

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), 0, 0);

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), 0, 0);

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), 0, 0);

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), 0, 0);

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), 0, 0);

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			}
			break;
		case 2:
			if (numIndices > 0) {
				for (int i = offset; i < end;) {
					int idx = index.get(i++) * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), 0);

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), 0);

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), 0);

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), 0);

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), 0);

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), 0);

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			}
			break;
		case 3:
			if (numIndices > 0) {
				for (int i = offset; i < end;) {
					int idx = index.get(i++) * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

					if (Intersector.intersectRayTriangle(invRay, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestPartDistance > distance) {
							closestPartDistance = distance;
							closestPartIntersection.set(intersection);
						}
					}
				}
			}
			break;
		default:
			throw new GdxRuntimeException("Unsupported posAttrib.numComponents");
		}

		return closestPartDistance != Float.MAX_VALUE;
	}
}
