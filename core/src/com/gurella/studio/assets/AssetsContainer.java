package com.gurella.studio.assets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.resource.AssetResourceDescriptor;
import com.gurella.engine.resource.AssetResourceReference;
import com.gurella.engine.resource.AssetResourceType;
import com.gurella.engine.scene.Scene;
import com.gurella.studio.EditorScreen.PresentProjectEvent;
import com.gurella.studio.Project;
import com.gurella.studio.inspector.InspectorContainer.InspectableValue;
import com.gurella.studio.inspector.InspectorContainer.PresentInspectableValueEvent;
import com.gurella.studio.inspector.InspectorPropertiesContainer;
import com.gurella.studio.project.ProjectHeaderContainer.SceneSelectionChangedEvent;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.InputDialogListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileUtils;

public class AssetsContainer extends VisTable {
	private Scene scene;
	private FileHandle applicationFile;
	private FileHandle sceneDirectory;
	private FilesTree filesTree = new FilesTree();

	public AssetsContainer() {
		setBackground("border");
		add(new VisScrollPane(filesTree)).top().left().fill().expand();
		EventService.addListener(PresentProjectEvent.class, new PresentProjectListener());
		EventService.addListener(SceneSelectionChangedEvent.class, new SceneSelectionChangedListener());
		filesTree.addListener(new SelectionChangeListener());
	}

	public void presentProject(Project selectedProject) {
		applicationFile = selectedProject.getApplicationFileHandle().parent();
	}

	private void presentScene(Scene selectedScene) {
		scene = selectedScene;
		String sceneId = selectedScene.getId();
		String sceneDirectoryName = applicationFile.file().getAbsolutePath() + "/" + sceneId;
		sceneDirectory = new FileHandle(sceneDirectoryName);
		if (!sceneDirectory.exists()) {
			sceneDirectory.mkdirs();
		}
		filesTree.setDirectory(sceneDirectory);
	}

	private void clearScene() {
		filesTree.setDirectory(null);
	}

	private boolean isValidFile(FileHandle f) {
		String path = f.path();
		int index = path.indexOf(sceneDirectory.name());
		String subPath = path.substring(index, path.length());
		return AssetsContainer.this.scene.containsAsset(subPath);
	}

	private class PresentProjectListener implements Listener1<Project> {
		@Override
		public void handle(Project selectedProject) {
			presentProject(selectedProject);
		}
	}

	private class FilesTree extends VisTree {
		private void setDirectory(FileHandle directory) {
			clearChildren();

			if (directory != null) {
				add(new DirectoryNode(directory));
			}

			addListener(new ShowNodePopupInputListener());
		}

		private class ShowNodePopupInputListener extends InputListener {
			private PopupMenu menu = new PopupMenu();
			private boolean menuVisible;

			public ShowNodePopupInputListener() {
				menu.addItem(new MenuItem("Add asset", new AddAssetMenuListener()));
				menu.addItem(new MenuItem("Add directory", new AddDirectoryMenuListener()));
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.RIGHT && !menuVisible) {
					menuVisible = true;
					Node node = FilesTree.this.getNodeAt(y);
					if (node instanceof DirectoryNode) {
						FilesTree.this.getSelection().set(node);
						menu.showMenu(getStage(), x, y);
					}
				}
			}

			private final class AddAssetMenuListener extends ChangeListener {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					menuVisible = false;
					menu.remove();
					final DirectoryNode directoryNode = (DirectoryNode) FilesTree.this.getSelection().getLastSelected();
					FileChooser fileChooser = new FileChooser(Mode.OPEN);
					fileChooser.setSelectionMode(SelectionMode.FILES);
					fileChooser.setListener(new FileChooserAdapter() {
						@Override
						public void selected(FileHandle file) {
							FileHandle sourceHandle = new FileHandle(file.file().getAbsolutePath());
							File source = sourceHandle.file();

							FileHandle destHandle = new FileHandle(
									directoryNode.directory.path() + "/" + source.getName());
							File dest = destHandle.file();

							try {
								Files.copy(source.toPath(), dest.toPath());
								addAssetToScene(destHandle);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
					getStage().addActor(fileChooser.fadeIn());
				}

				private void addAssetToScene(FileHandle f) {
					String path = f.path();
					int index = path.indexOf(sceneDirectory.name());
					String subPath = path.substring(index, path.length());
					if (!scene.containsAsset(subPath) && subPath.endsWith(".png")) {
						AssetDescriptor<Texture> assetDescriptor = new AssetDescriptor<Texture>(subPath, Texture.class);
						AssetResourceReference<Texture> asset = new AssetResourceReference<Texture>(scene.getNextId(),
								false, true, assetDescriptor);
						scene.add(asset);

						final DirectoryNode directoryNode = (DirectoryNode) FilesTree.this.getSelection()
								.getLastSelected();
						FileHandle[] files = directoryNode.directory.list();
						Array<FileHandle> fileList = FileUtils.sortFiles(files);
						Array<FileHandle> validFileList = new Array<FileHandle>();
						for (FileHandle fileHandle : fileList) {
							if (isValidFile(fileHandle)) {
								validFileList.add(fileHandle);
							}
						}

						directoryNode.insert(validFileList.indexOf(f, false), new FileNode(f));

						if (!directoryNode.isExpanded()) {
							directoryNode.setExpanded(true);
						}
					}
				}
			}

			private final class AddDirectoryMenuListener extends ChangeListener {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					menuVisible = false;
					menu.remove();
					DialogUtils.showInputDialog(getStage(), "Add directory", "Name", new InputDialogListener() {
						@Override
						public void finished(String input) {
							final DirectoryNode directoryNode = (DirectoryNode) FilesTree.this.getSelection()
									.getLastSelected();
							FileHandle h = new FileHandle(directoryNode.directory.path() + "/" + input);
							h.mkdirs();

							FileHandle[] files = directoryNode.directory.list();
							Array<FileHandle> fileList = FileUtils.sortFiles(files);
							Array<FileHandle> validFileList = new Array<FileHandle>();
							for (FileHandle fileHandle : fileList) {
								if (isValidFile(fileHandle)) {
									validFileList.add(fileHandle);
								}
							}

							directoryNode.insert(validFileList.indexOf(h, false), new DirectoryNode(h));
						}

						@Override
						public void canceled() {
						}
					});
				}
			}
		}
	}

	private class DirectoryNode extends Node {
		private VisImage image = new VisImage(VisUI.getSkin().getDrawable("icon-folder"));
		private VisLabel label = new VisLabel();
		private FileHandle directory;

		public DirectoryNode(FileHandle directory) {
			super(new VisTable());
			this.directory = directory;
			label.setText(directory.name());
			VisTable actor = (VisTable) getActor();
			actor.add(image);
			actor.add(label).fillX().expandX();

			FileHandle[] files = directory.list();

			if (files.length == 0)
				return;

			Array<FileHandle> fileList = FileUtils.sortFiles(files);

			for (FileHandle f : fileList) {
				if (f.file() != null && !f.file().isHidden()) {
					if (f.isDirectory()) {
						add(new DirectoryNode(f));
					} else {
						add(new FileNode(f));
					}
				}
			}
		}
	}

	private class FileNode extends Node {
		private VisImage image = new VisImage(VisUI.getSkin().getDrawable("icon-drive"));
		private VisLabel label = new VisLabel();
		private FileHandle file;

		public FileNode(FileHandle file) {
			super(new VisTable());
			this.file = file;
			label.setText(file.name());
			VisTable actor = (VisTable) getActor();
			actor.add(image);
			actor.add(label).fillX().expandX();
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

	private class SelectionChangeListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			Selection<Node> selected = filesTree.getSelection();
			if (selected.first() instanceof FileNode) {
				FileNode fileNode = (FileNode) selected.first();
				String fileName = fileNode.file.path();
				AssetResourceDescriptor<?> assetDescriptor = scene.findAssetDescriptor(fileName);
				if (assetDescriptor == null) {
					assetDescriptor = createDefaultAssetDescriptor(fileNode.file);
				}
				if (assetDescriptor == null) {
					EventService.notify(new PresentInspectableValueEvent(null));
				} else {
					EventService.notify(
							new PresentInspectableValueEvent(new InspectableValue(new TexturePropertiesContainer(scene,
									(AssetResourceDescriptor<Texture>) assetDescriptor))));
				}
			} else {
				EventService.notify(new PresentInspectableValueEvent(null));
			}
		}

		private AssetResourceDescriptor<?> createDefaultAssetDescriptor(FileHandle file) {
			if (file.extension().equalsIgnoreCase("png")) {
				return new AssetResourceDescriptor<Texture>(AssetResourceType.texture, file.path());
			}
			return null;
		}
	}

	private static class TexturePropertiesContainer extends VisTable implements InspectorPropertiesContainer {
		private Scene scene;
		private AssetResourceDescriptor<Texture> descriptor;

		public TexturePropertiesContainer(Scene scene, AssetResourceDescriptor<Texture> descriptor) {
			this.scene = scene;
			this.descriptor = descriptor;
			add("Asset");
		}

		@Override
		public void save() {
			// TODO Auto-generated method stub
		}
	}
}
