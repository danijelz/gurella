package com.gurella.studio.editor.tool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gurella.engine.graphics.render.GenericBatch;

public class RotateHandle extends ToolHandle {
	private Model model;
	private ModelInstance modelInstance;

	public RotateHandle(int id, Color color) {
		super(id, color);
		model = torus(new Material(ColorAttribute.createDiffuse(color)), 20, 1f, 50, 50);
		modelInstance = new ModelInstance(model);
		switch (id) {
		case TransformTool.X_HANDLE_ID:
			this.rotationEuler.y = 90;
			this.scale.x = 0.9f;
			this.scale.y = 0.9f;
			this.scale.z = 0.9f;
			break;
		case TransformTool.Y_HANDLE_ID:
			this.rotationEuler.x = 90;
			break;
		case TransformTool.Z_HANDLE_ID:
			this.rotationEuler.z = 90;
			this.scale.x = 1.1f;
			this.scale.y = 1.1f;
			this.scale.z = 1.1f;
			break;
		}
	}

	@Override
	void render(GenericBatch batch) {
		batch.render(modelInstance);
	}

	@Override
	void applyTransform() {
		rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
		modelInstance.transform.set(position, rotation, scale);
	}

	@Override
	public void dispose() {
		model.dispose();
	}

	@Override
	void changeColor(Color color) {
	}

	private static Model torus(Material mat, float width, float height, int divisionsU, int divisionsV) {
		MeshPartBuilder.VertexInfo v0 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("torus", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, mat);
		// builder.setColor(Color.LIGHT_GRAY);

		MeshPartBuilder.VertexInfo curr1 = v0.set(null, null, null, null);
		curr1.hasUV = curr1.hasNormal = false;
		curr1.hasPosition = true;

		MeshPartBuilder.VertexInfo curr2 = v1.set(null, null, null, null);
		curr2.hasUV = curr2.hasNormal = false;
		curr2.hasPosition = true;
		short i1, i2, i3 = 0, i4 = 0;

		int i, j, k;
		double s, t, twopi;
		twopi = 2 * Math.PI;

		for (i = 0; i < divisionsV; i++) {
			for (j = 0; j <= divisionsU; j++) {
				for (k = 1; k >= 0; k--) {
					s = (i + k) % divisionsV + 0.5;
					t = j % divisionsU;

					curr1.position.set(
							(float) ((width + height * Math.cos(s * twopi / divisionsV))
									* Math.cos(t * twopi / divisionsU)),
							(float) ((width + height * Math.cos(s * twopi / divisionsV))
									* Math.sin(t * twopi / divisionsU)),
							(float) (height * Math.sin(s * twopi / divisionsV)));
					k--;
					s = (i + k) % divisionsV + 0.5;
					curr2.position.set(
							(float) ((width + height * Math.cos(s * twopi / divisionsV))
									* Math.cos(t * twopi / divisionsU)),
							(float) ((width + height * Math.cos(s * twopi / divisionsV))
									* Math.sin(t * twopi / divisionsU)),
							(float) (height * Math.sin(s * twopi / divisionsV)));
					// curr2.uv.set((float) s, 0);
					i1 = builder.vertex(curr1);
					i2 = builder.vertex(curr2);
					builder.rect(i4, i2, i1, i3);
					i4 = i2;
					i3 = i1;
				}
			}
		}

		return modelBuilder.end();
	}

}
