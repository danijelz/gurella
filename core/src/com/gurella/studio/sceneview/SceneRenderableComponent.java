package com.gurella.studio.sceneview;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.graphics.GenericBatch;
import com.gurella.engine.resource.AssetResourceReference;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SceneNodeComponent;
import com.gurella.engine.resource.factory.AssetRegistry;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.AssetId;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.scene.renderable.TextureComponent;

public class SceneRenderableComponent extends RenderableComponent {
	private static final AssetRegistry assetRegistry = new AssetRegistry();

	private TextureComponent textureComponent;

	private ObjectMap<ResourceReference<? extends SceneNodeComponent>, Renderable> renderablesByReference = new ObjectMap<ResourceReference<? extends SceneNodeComponent>, Renderable>();

	@Override
	protected void updateDefaultTransform() {
		for (Renderable renderable : renderablesByReference.values()) {
			renderable.worldTransform.idt();
		}
	}

	@Override
	protected void updateTransform() {
		for (Renderable renderable : renderablesByReference.values()) {
			getTransformComponent().getWorldTransform(renderable.worldTransform);
		}
	}

	@Override
	protected void render(GenericBatch batch) {
		if (textureComponent != null) {
			Sprite sprite = textureComponent.sprite;
			if (sprite.getTexture() != null) {
				batch.render(sprite);
			}
		}

		for (Renderable renderable : renderablesByReference.values()) {
			batch.render(renderable);
		}
	}

	public void add(ResourceReference<? extends SceneNodeComponent> componentReference) {
		if (renderablesByReference.containsKey(componentReference)) {
			return;
		}

		@SuppressWarnings("unchecked")
		ModelResourceFactory<TextureComponent> resourceFactory = null;//(ModelResourceFactory<TextureComponent>) componentReference.getResourceFactory();

		AssetId texture = resourceFactory.getPropertyValue("texture");
		@SuppressWarnings("unchecked")
		AssetDescriptor<Texture> descriptor = new AssetDescriptor<Texture>(texture.getFileName(),
				(Class<Texture>) texture.getAssetType());
		assetRegistry.load(descriptor);
		while (!assetRegistry.isLoaded(descriptor)) {
			assetRegistry.update();
		}

		Texture ttt = assetRegistry.get(descriptor);
		// TODO Auto-generated method stub

	}

	public void addTexture(ResourceReference<TextureComponent> componentReference) {
		if (getScene() == null) {
			return;
		}

		if (textureComponent == null) {
			ModelResourceFactory<TextureComponent> resourceFactory = (ModelResourceFactory<TextureComponent>) componentReference
					.getResourceFactory();
			AssetId texture = resourceFactory.getPropertyValue("texture");
			if (texture == null || texture.getFileName() == null) {
				return;
			}

			@SuppressWarnings("unchecked")
			AssetDescriptor<Texture> descriptor = new AssetDescriptor<Texture>(texture.getFileName(),
					(Class<Texture>) texture.getAssetType());
			assetRegistry.load(descriptor);
			while (!assetRegistry.isLoaded(descriptor)) {
				assetRegistry.update();
			}

			/*AssetResourceReference<?> textureReference = getScene().findOrCreateAssetReference(texture.getFileName(),
					texture.getAssetType());

			Texture ttt = assetRegistry.get(descriptor);
			IntArray dependencies = new IntArray();
			dependencies.add(textureReference.getId());
			DependencyMap dependencyMap = DependencyMap.obtain(getScene(), dependencies);
			dependencyMap.addResolvedResource(textureReference.getId(), ttt);

			textureComponent = new TextureComponent();
			textureComponent.setTransformComponent(getTransformComponent());
			resourceFactory.init(textureComponent, dependencyMap);*/
		}
	}

	@Override
	public void getBounds(BoundingBox bounds) {
		for (Renderable renderable : renderablesByReference.values()) {
			MeshPart meshPart = renderable.meshPart;
			meshPart.update();
			bounds.ext(meshPart.center, meshPart.radius);
		}
	}

	@Override
	public boolean getIntersection(Ray ray, Vector3 intersection) {
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
