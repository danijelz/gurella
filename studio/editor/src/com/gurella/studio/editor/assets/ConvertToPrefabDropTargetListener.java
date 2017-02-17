package com.gurella.studio.editor.assets;

import static com.gurella.engine.asset.descriptor.DefaultAssetDescriptors.prefab;
import static com.gurella.studio.GurellaStudioPlugin.showError;
import static com.gurella.studio.editor.utils.FileDialogUtils.enterNewFileName;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.badlogic.gdx.Files.FileType;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.graph.NodeSelection;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.operation.ConvertToPrefabOperation;
import com.gurella.studio.editor.utils.PrettyPrintSerializer;
import com.gurella.studio.editor.utils.Try;

class ConvertToPrefabDropTargetListener extends DropTargetAdapter implements HistoryContributor {
	private final SceneEditorContext context;

	private HistoryService historyService;

	ConvertToPrefabDropTargetListener(SceneEditorContext context) {
		this.context = context;
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferedNode() == null) {
			event.detail = DND.DROP_NONE;
		} else {
			event.detail = DND.DROP_MOVE;
		}
	}

	private static SceneNode getTransferedNode() {
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof NodeSelection) {
			return ((NodeSelection) selection).getNode();
		} else {
			return null;
		}
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
		event.feedback |= DND.FEEDBACK_SELECT;
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		event.detail = DND.DROP_MOVE;
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (event.item == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		TreeItem item = (TreeItem) event.item;
		Object data = item.getData();
		if (!(data instanceof IFolder)) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IFolder folder = (IFolder) data;
		SceneNode node = getTransferedNode();
		Optional<String> prefabName = enterNewFileName(folder, node.getName(), true, prefab.getSingleExtension());
		if (!prefabName.isPresent()) {
			event.detail = DND.DROP_NONE;
			return;
		}

		Try.run(() -> convertToPrefab(folder, node, prefabName.get()),
				e -> showError(e, "Error converting to prefab."));
	}

	private void convertToPrefab(IFolder folder, SceneNode node, String prefabName)
			throws UnsupportedEncodingException, CoreException {
		IProject project = folder.getProject();
		IPath projectPath = project.getLocation();
		IPath projectAssetPath = folder.getFile(prefabName).getLocation().makeRelativeTo(projectPath);
		IFile file = project.getFile(projectAssetPath);

		SceneNode prefab = CopyContext.copyObject(node);
		String serialized = PrettyPrintSerializer.serialize(ManagedObject.class, prefab);
		InputStream is = new ByteArrayInputStream(serialized.getBytes("UTF-8"));
		file.create(is, true, context.getProgressMonitor());
		IPath gdxAssetPath = AssetsFolderLocator.getAssetsRelativePath(file);
		AssetService.save(prefab, gdxAssetPath.toString(), FileType.Internal);
		ConvertToPrefabOperation operation = new ConvertToPrefabOperation(context.editorId, node, prefab);
		historyService.executeOperation(operation, "Error converting to prefab");
	}
}
