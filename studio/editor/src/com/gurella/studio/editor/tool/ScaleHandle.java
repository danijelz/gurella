package com.gurella.studio.editor.tool;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.GenericBatch;

public class ScaleHandle extends ToolHandle {
	Model model;
	ModelInstance modelInstance;

	public ScaleHandle(int id, Color color, Model model) {
		super(id, color);
		this.model = model;
		this.modelInstance = new ModelInstance(model);
	}

	@Override
	void changeColor(Color color) {
		ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.first().get(ColorAttribute.Diffuse);
		diffuse.color.set(color);
	}

	@Override
	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	@Override
	void applyTransform() {
		modelInstance.transform.set(position, rotation, scale);
	}

	@Override
	public void dispose() {
		model.dispose();
	}

	///////////////////////intersection

	private final BoundingBox temp = new BoundingBox();
	private final Matrix4 transform = new Matrix4();

	boolean getIntersection(Vector3 cameraPosition, Ray ray, Vector3 intersection) {
		Vector3 closestIntersection = new Vector3(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		Array<Node> nodes = modelInstance.nodes;
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (getIntersection(cameraPosition, node, ray, intersection)) {
				float distance = intersection.dst2(cameraPosition);
				if (closestDistance > distance) {
					closestDistance = distance;
					closestIntersection.set(intersection);
				}
			}
		}

		return closestDistance != Float.MAX_VALUE;
	}

	private boolean getIntersection(Vector3 cameraPosition, Node node, Ray ray, Vector3 intersection) {
		Vector3 closestIntersection = new Vector3(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		node.calculateWorldTransform();
		node.extendBoundingBox(temp.inf(), true);
		temp.mul(modelInstance.transform);
		
		if (Intersector.intersectRayBoundsFast(ray, temp)) {
			transform.set(modelInstance.transform).mul(node.globalTransform);
			
			Array<NodePart> parts = node.parts;
			for (int i = 0, n = parts.size; i < n; i++) {
				NodePart nodePart = parts.get(i);
				if (nodePart.enabled) {
					MeshPart meshPart = nodePart.meshPart;
					int primitiveType = meshPart.primitiveType;
					Mesh mesh = meshPart.mesh;
					int offset = meshPart.offset;
					int count = meshPart.size;

					if (getIntersection(cameraPosition, mesh, primitiveType, offset, count, transform, ray,
							intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
						}
					}
				}
			}
		}

		return closestDistance != Float.MAX_VALUE;
	}

	private static boolean getIntersection(Vector3 cameraPosition, Mesh mesh, int primitiveType, int offset, int count,
			final Matrix4 transform, Ray ray, Vector3 intersection) {
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
		Vector3 t1 = new Vector3();
		Vector3 t2 = new Vector3();
		Vector3 t3 = new Vector3();
		Vector3 closestIntersection = new Vector3(Float.NaN, Float.NaN, Float.NaN);
		float closestDistance = Float.MAX_VALUE;

		switch (posAttrib.numComponents) {
		case 1:
			if (numIndices > 0) {
				for (int i = offset; i < end;) {
					int idx = index.get(i++) * vertexSize + posoff;
					t1.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t1.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t2.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t1.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t2.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), 0, 0);
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
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
					if (transform != null) {
						t1.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), 0);
					if (transform != null) {
						t2.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), 0);
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), 0);
					if (transform != null) {
						t1.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), 0);
					if (transform != null) {
						t2.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), 0);
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
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
					if (transform != null) {
						t1.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
					if (transform != null) {
						t2.mul(transform);
					}

					idx = index.get(i++) * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
						}
					}
				}
			} else {
				for (int i = offset; i < end;) {
					int idx = i++ * vertexSize + posoff;
					t1.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
					if (transform != null) {
						t1.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t2.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
					if (transform != null) {
						t2.mul(transform);
					}

					idx = i++ * vertexSize + posoff;
					t3.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
					if (transform != null) {
						t3.mul(transform);
					}

					if (Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)) {
						float distance = intersection.dst2(cameraPosition);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestIntersection.set(intersection);
						}
					}
				}
			}
			break;
		}

		return closestDistance != Float.MAX_VALUE;
	}
}
