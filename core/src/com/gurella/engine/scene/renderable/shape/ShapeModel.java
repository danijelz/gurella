package com.gurella.engine.scene.renderable.shape;

import static com.badlogic.gdx.graphics.VertexAttribute.Normal;
import static com.badlogic.gdx.graphics.VertexAttribute.Position;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.pool.PoolService;

public abstract class ShapeModel implements Disposable {
	private static final ModelBuilder modelBuilder = new ModelBuilder();

	public static final Material defaulMaterial = new Material(ColorAttribute.createDiffuse(1, 1, 1, 1));
	public static final VertexAttributes defaultAttributes = new VertexAttributes(Position(), Normal());

	@PropertyDescriptor(descriptiveName = "material")
	public MaterialDescriptor materialDescriptor;
	private transient Material material;

	private transient ModelInstance instance;
	private transient Model model;

	@PropertyDescriptor(nullable = false)
	private PrimitiveType primitiveType = PrimitiveType.triangles;

	protected transient boolean dirty = true;

	public MaterialDescriptor getMaterialDescriptor() {
		return materialDescriptor;
	}

	public void setMaterialDescriptor(MaterialDescriptor materialDescriptor) {
		this.materialDescriptor = materialDescriptor;
		material = materialDescriptor == null ? new Material(defaulMaterial) : materialDescriptor.createMaterial();
		if (instance != null) {
			instance.materials.set(0, material);
		}
		dirty = true;
	}

	public Material getMaterial() {
		if (material == null) {
			material = materialDescriptor == null ? new Material(defaulMaterial) : materialDescriptor.createMaterial();
		}
		return material;
	}

	public VertexAttributes getVertexAttributes() {
		return materialDescriptor == null ? defaultAttributes : materialDescriptor.createVertexAttributes(true, true);
	}

	public ModelInstance getModelInstance() {
		if (dirty || instance == null) {
			dirty = false;
			if (model != null) {
				model.dispose();
			}

			synchronized (modelBuilder) {
				model = createModel(modelBuilder);
			}

			if (model != null) {
				ModelInstance newInstance = new ModelInstance(model);
				if (instance != null) {
					// TODO update transform from TransformComponent
					newInstance.transform.set(instance.transform);
				}
				instance = newInstance;
			} else {
				instance = null;
			}
		}
		return instance;
	}

	private Model createModel(ModelBuilder builder) {
		builder.begin();
		Matrix4 transform = PoolService.obtain(Matrix4.class);
		transform.idt();
		buildParts(builder, transform);
		PoolService.free(transform);
		return builder.end();
	}

	protected abstract void buildParts(ModelBuilder builder, Matrix4 parentTransform);

	public int getGlPrimitiveType() {
		return primitiveType.glValue;
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	public void setPrimitiveType(PrimitiveType primitiveType) {
		if (this.primitiveType != primitiveType) {
			this.primitiveType = primitiveType == null ? PrimitiveType.triangles : primitiveType;
			dirty = true;
		}
	}

	@Override
	public void dispose() {
		if (model != null) {
			model.dispose();
			model = null;
		}
	}

	public enum PrimitiveType {
		points(GL20.GL_POINTS), lines(GL20.GL_LINES), triangles(GL20.GL_TRIANGLES);

		public final int glValue;

		private PrimitiveType(int glValue) {
			this.glValue = glValue;
		}
	}
}
