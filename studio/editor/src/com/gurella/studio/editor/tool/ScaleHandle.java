package com.gurella.studio.editor.tool;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.render.GenericBatch;

public class ScaleHandle extends ToolHandle {
	private Model model;
	private ModelInstance modelInstance;

	public ScaleHandle(int id, Model model) {
		super(id);
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
	
	boolean getIntersection(Ray ray, Vector3 intersection) {
		Array<Node> nodes = modelInstance.nodes;
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (getIntersection(node, ray, intersection)) {
				return true;
			}
		}
		return false;
	}

	boolean getIntersection(Node node, Ray ray, Vector3 intersection) {
		if (Intersector.intersectRayBoundsFast(ray, temp)) {
			Array<NodePart> parts = node.parts;
			for (int i = 0, n = parts.size; i < n; i++) {
				NodePart nodePart = parts.get(i);
				if (nodePart.enabled) {
					MeshPart meshPart = nodePart.meshPart;
					Mesh mesh = meshPart.mesh;
					
					int offset = meshPart.offset;
					boolean indexed = mesh.getNumIndices() > 0;
					FloatBuffer verticesBuffer = mesh.getVerticesBuffer();
					ShortBuffer indicesBuffer = mesh.getIndicesBuffer();
				}
				// TODO meshPart.size;
				// Intersector.intersectRayTriangles(ray, vertices, indices, vertexSize, intersection);
			}
			Intersector.intersectRayBounds(ray, temp, intersection);
			return true;
		}

		return false;
	}
}
