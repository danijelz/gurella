package com.gurella.studio.nodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.event.Listener1Event;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SharedResourceReference;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceId;
import com.gurella.engine.scene2.Scene;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.utils.ValueUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;

public class SceneNodeTreeNode extends Node {
	private ResourceReference<? extends SceneNode> reference;
	private ModelResourceFactory<? extends SceneNode> nodeFactory;

	public SceneNodeTreeNode(ResourceReference<? extends SceneNode> reference) {
		super(new VisLabel(ValueUtils.isEmpty(reference.getName())
				? "Node"
				: reference.getName()));
		this.reference = reference;
		this.nodeFactory = (ModelResourceFactory<? extends SceneNode>) reference.getResourceFactory();
		// TODO must also be removed
		EventBus.GLOBAL.addListener(NodeNameChangedEvent.class, new NodeNameChangedListener(nodeFactory));

		Array<ResourceReference<SceneNode>> children = nodeFactory.getPropertyValue("children");
		if (children != null) {
			for (Object child : children) {
				if (child instanceof ResourceId) {
					add(new SceneNodeTreeNode(reference.getOwningContext().<SceneNode> getReference(
							((ResourceId) child).getId())));
				}
			}
		}

		getActor().addListener(new MenuClickListener());
	}

	@Override
	public VisLabel getActor() {
		return (VisLabel) super.getActor();
	}

	private void addNode(String nodeName) {
		ModelResourceFactory<SceneNode> childFactory = new ModelResourceFactory<SceneNode>(SceneNode.class);
		childFactory.setPropertyValue("activateOnInit", Boolean.FALSE);

		Scene scene = (Scene) reference.getOwningContext();

		int nodeId = scene.getNextId();
		SharedResourceReference<SceneNode> nodeReference = new SharedResourceReference<SceneNode>(nodeId, nodeName,
				false, false, childFactory);
		scene.add(nodeReference);

		Array<Object> children = nodeFactory.getPropertyValue("children");
		if (children == null) {
			children = new Array<Object>();
			nodeFactory.setPropertyValue("children", children);
		}

		children.add(new ResourceId(nodeId));
		add(new SceneNodeTreeNode(nodeReference));
	}

	private void removeNode() {
		SceneNodeTreeNode parent = (SceneNodeTreeNode) getParent();
		if (parent == null) {
			Scene scene = (Scene) reference.getOwningContext();
			scene.remove(reference.getId());
			getTree().remove(this);
		} else {
			parent.removeChild(this);
		}
	}

	private void removeChild(SceneNodeTreeNode child) {
		if (getChildren().contains(child, true)) {
			for (Node child2 : child.getChildren()) {
				child.removeChild((SceneNodeTreeNode) child2);
			}

			Scene scene = (Scene) reference.getOwningContext();
			scene.remove(reference.getId());
			remove(child);
		}
	}

	public ResourceReference<? extends SceneNode> getReference() {
		return reference;
	}

	public static class NodeNameChangedEvent extends Listener1Event<ModelResourceFactory<? extends SceneNode>> {
		public NodeNameChangedEvent(ModelResourceFactory<? extends SceneNode> value) {
			super(value);
		}
	}

	private class NodeNameChangedListener implements Listener1<ModelResourceFactory<? extends SceneNode>> {
		private ModelResourceFactory<? extends SceneNode> factory;

		public NodeNameChangedListener(ModelResourceFactory<? extends SceneNode> factory) {
			this.factory = factory;
		}

		@Override
		public void handle(ModelResourceFactory<? extends SceneNode> value) {
			if (factory == value) {
				getActor().setText(factory.getPropertyValue("name", "Node"));
			}
		}
	}

	private class MenuClickListener extends InputListener {
		private PopupMenu menu = new PopupMenu();

		public MenuClickListener() {
			menu.addItem(new AddNodeMenuItem());
			menu.addItem(new RemoveNodeMenuItem());
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			if (button == Buttons.RIGHT) {
				Stage stage = getActor().getStage();
				menu.showMenu(stage, Gdx.input.getX(), stage.getHeight() - Gdx.input.getY());
			}
		}
	}

	private class AddNodeMenuItem extends MenuItem {
		public AddNodeMenuItem() {
			super("Add Child");
			addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					DialogUtils.showInputDialog(getStage(), "New Scene node", "name", new InputDialogAdapter() {
						@Override
						public void finished(String input) {
							addNode(input);
						}
					});
				}
			});
		}
	}

	private class RemoveNodeMenuItem extends MenuItem {
		public RemoveNodeMenuItem() {
			super("Remove");
			addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					removeNode();
				}
			});
		}
	}
}
