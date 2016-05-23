package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
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
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.scene.debug.DebugRenderable;
import com.gurella.engine.scene.renderable.debug.WireframeShader;

public abstract class RenderableComponent3d extends RenderableComponent implements DebugRenderable {
	private transient final BoundingBox temp = new BoundingBox();

	protected abstract ModelInstance getModelInstance();

	@Override
	protected void updateGeometry() {
		ModelInstance instance = getModelInstance();
		if (instance == null) {
			return;
		}

		if (transformComponent == null) {
			instance.transform.idt();
		} else {
			transformComponent.getWorldTransform(instance.transform);
		}
	}

	@Override
	protected void doRender(GenericBatch batch) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			batch.render(instance);
		}
	}

	@Override
	public void doGetBounds(BoundingBox bounds) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			instance.extendBoundingBox(bounds);
			if (transformComponent != null) {
				bounds.mul(instance.transform);
			}
		}
	}

	@Override
	public boolean doGetIntersection(Ray ray, Vector3 intersection) {
		ModelInstance instance = getModelInstance();
		if (instance == null) {
			return false;
		}

		Array<Node> nodes = instance.nodes;
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (getIntersection(ray, intersection, node)) {
				return true;
			}
		}

		return false;
	}

	private boolean getIntersection(Ray ray, Vector3 intersection, Node node) {
		int childCount = node.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				Node childNode = node.getChild(i);
				if (getIntersection(ray, intersection, childNode)) {
					return true;
				}
			}
		} else {
			node.extendBoundingBox(temp.inf(), true);
			if (Intersector.intersectRayBoundsFast(ray, temp)) {
				Array<NodePart> parts = node.parts;
				for (int i = 0, n = parts.size; i < n; i++) {
					NodePart nodePart = parts.get(i);
					if (!nodePart.enabled) {
						continue;
					}

					MeshPart meshPart = nodePart.meshPart;
					Mesh mesh = meshPart.mesh;

					// TODO meshPart.size;
					// Intersector.intersectRayTriangles(ray, vertices, indices, vertexSize, intersection);
				}
				Intersector.intersectRayBounds(ray, temp, intersection);
				return true;
			}
		}

		return false;
	}

	private boolean intersect(Ray ray, Model model, Matrix4 transform, Vector3 intersection) {
		final Matrix4 reverse = new Matrix4(transform).inv();
		for (Mesh mesh : model.meshes) {
			mesh.transform(transform);
			float[] vertices = new float[mesh.getNumVertices() * 6];
			short[] indices = new short[mesh.getNumIndices()];
			mesh.getVertices(vertices);
			mesh.getIndices(indices);
			try {
				if (Intersector.intersectRayTriangles(ray, vertices, indices, 4, intersection)) {
					intersection.mul(transform);
					return true;
				}
			} finally {
				mesh.transform(reverse);
			}
		}
		return false;
	}

	// public boolean intersect(Ray ray, Vector3 intersection) {
	// final BoundingBox bb = new BoundingBox();
	// for (int i = 0; i < modelInstances.size; i++) {
	// ModelInstance instance = modelInstances.get(i);
	// instance.calculateBoundingBox(bb).mul(instance.transform);
	// if (Intersector.intersectRayBoundsFast(ray, bb)) {
	// return intersect(ray, models.get(i), instance.transform, intersection);
	// }
	// }
	// return false;
	// }

	WireframeShader wfSh;
	@Override
	public void debugRender(GenericBatch batch) {
		ModelInstance instance = getModelInstance();
		if (instance != null) {
			Array<MeshPart> meshParts = instance.model.meshParts;
			int[] old = new int[meshParts.size];
			for (int i = 0, n = meshParts.size; i < n; i++) {
				meshParts.get(i).primitiveType = GL20.GL_LINE_LOOP;
			}

			if(wfSh == null) {
				wfSh = new WireframeShader();
				wfSh.init();
			}
			batch.render(instance, wfSh);

			for (int i = 0, n = meshParts.size; i < n; i++) {
				meshParts.get(i).primitiveType = old[i];
			}
		}
	}
}
