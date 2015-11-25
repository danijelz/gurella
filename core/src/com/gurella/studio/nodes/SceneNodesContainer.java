package com.gurella.studio.nodes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventBus;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.resource.ResourceReference;
import com.gurella.engine.resource.SharedResourceReference;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceId;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.signal.Listener1;
import com.gurella.studio.inspector.InspectorContainer.InspectableValue;
import com.gurella.studio.inspector.InspectorContainer.PresentInspectableValueEvent;
import com.gurella.studio.project.ProjectHeaderContainer.SceneSelectionChangedEvent;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTree;

public class SceneNodesContainer extends VisTable {
	private VisTable header = new VisTable();
	private VisLabel headerLabel = new VisLabel("Nodes");
	private VisTextButton menuButton = new VisTextButton("...");
	private VisTree nodesTree = new VisTree();
	private Actor spacer = new Actor();
	private Scene scene;

	public SceneNodesContainer() {
		setBackground("border");

		menuButton.addListener(new MenuClickListener());
		menuButton.setDisabled(true);

		header.setBackground("button-down");
		header.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		header.add(headerLabel).top().left().fillX().expandX();
		header.add(menuButton).top().left();

		nodesTree.addListener(new SelectionChangeListener());

		add(header).top().left().fillX().expandX();
		row();
		add(nodesTree).top().left().fill().expand();
		row();
		add(spacer).top().left().fill().expand();
		EventBus.GLOBAL.addListener(SceneSelectionChangedEvent.class, new SceneSelectionChangedListener());
	}

	private void clearScene() {
		this.scene = null;
		nodesTree.clearChildren();
		menuButton.setDisabled(true);
	}

	public void presentScene(Scene selectedScene) {
		this.scene = selectedScene;
		refresh();
		menuButton.setDisabled(false);
	}

	private void refresh() {
		nodesTree.clearChildren();
		for (int i = 0; i < scene.initialNodes.size; i++) {
			ResourceReference<SceneNode> sceneNode = scene.getReference(scene.initialNodes.get(i));
			nodesTree.add(new SceneNodeTreeNode(sceneNode));
		}
	}

	private void addNode(String nodeName) {
		ModelResourceFactory<? extends SceneNode> parentFactory = null;
		Selection<Node> selection = nodesTree.getSelection();
		if (selection != null && selection.first() != null) {
			parentFactory = (ModelResourceFactory<? extends SceneNode>) ((SceneNodeTreeNode) selection.first())
					.getReference().getResourceFactory();
		}

		ModelResourceFactory<SceneNode> nodeFactory = new ModelResourceFactory<SceneNode>(SceneNode.class);
		nodeFactory.setPropertyValue("activateOnInit", Boolean.FALSE);
		int nodeId = scene.getNextId();
		SharedResourceReference<SceneNode> nodeReference = new SharedResourceReference<SceneNode>(nodeId, nodeName,
				false, false, nodeFactory);
		scene.add(nodeReference);

		if (parentFactory == null) {
			scene.addInitialNode(nodeId);
		} else {
			Array<Object> children = parentFactory.getPropertyValue("children");
			if (children == null) {
				children = new Array<Object>();
				parentFactory.setPropertyValue("children", children);
			}
			children.add(new ResourceId(nodeId));
		}

		refresh();
	}

	private class SelectionChangeListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			Selection<Node> selected = nodesTree.getSelection();
			if (selected.first() instanceof SceneNodeTreeNode) {
				SceneNodeTreeNode sceneNodeTreeNode = (SceneNodeTreeNode) selected.first();
				ResourceReference<? extends SceneNode> reference = sceneNodeTreeNode.getReference();
				EventBus.GLOBAL.notify(new PresentInspectableValueEvent(new InspectableValue(
						new SceneNodePropertiesContainer(reference))));
			} else {
				EventBus.GLOBAL.notify(new PresentInspectableValueEvent(null));
			}
		}
	}

	private class SceneSelectionChangedListener implements Listener1<Scene> {
		@Override
		public void handle(Scene selectedScene) {
			if (selectedScene == null) {
				clearScene();
			} else {
				presentScene(selectedScene);
			}
		}
	}

	private class MenuClickListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			PopupMenu menu = new PopupMenu();
			menu.addItem(new AddNodeMenuItem());
			menu.showMenu(getStage(), Gdx.input.getX(), getStage().getHeight() - Gdx.input.getY());
		}
	}

	private class AddNodeMenuItem extends MenuItem {
		public AddNodeMenuItem() {
			super("Add");
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
}
