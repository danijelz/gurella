package com.gurella.studio.editor.operation;

import static com.gurella.engine.utils.Reflection.newInstanceSilently;
import static com.gurella.studio.common.RequiredComponetsRegistry.getRequired;
import static com.gurella.studio.gdx.GdxContext.post;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;
import com.gurella.studio.gdx.GdxContext;

public class AddComponentOperation extends AbstractOperation {
	final int editorId;
	final SceneNode node;
	final List<SceneNodeComponent> components = new ArrayList<>();

	public AddComponentOperation(int editorId, SceneNode node, SceneNodeComponent newValue) {
		super("Add component");
		this.editorId = editorId;
		this.node = node;
		getRequired(newValue.getClass()).forEach(t -> ofNullable(newInstanceSilently(t)).ifPresent(components::add));
		components.add(newValue);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		components.forEach(this::addComponent);
		return Status.OK_STATUS;
	}

	private void addComponent(SceneNodeComponent component) {
		if (node.hasComponent(ComponentType.findBaseType(component), true)) {
			return;
		}

		node.addComponent(component);
		GdxContext.addToBundle(editorId, node, component, component.ensureUuid());
		GdxContext.clean(editorId);
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentAdded(node, component));
		post(editorId, editorId, SceneChangedEvent.instance);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		int count = components.size() - 1;
		IntStream.rangeClosed(0, count).mapToObj(i -> components.get(count - i)).forEachOrdered(this::removeComponent);
		return Status.OK_STATUS;
	}

	private void removeComponent(SceneNodeComponent component) {
		node.removeComponent(component, false);
		GdxContext.removeFromBundle(editorId, node, component);
		GdxContext.clean(editorId);
		post(editorId, editorId, EditorSceneActivityListener.class, l -> l.componentRemoved(node, component));
		post(editorId, editorId, SceneChangedEvent.instance);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
		return execute(monitor, adaptable);
	}
}
