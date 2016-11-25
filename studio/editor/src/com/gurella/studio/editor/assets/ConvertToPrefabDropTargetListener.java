package com.gurella.studio.editor.assets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.base.serialization.json.JsonOutput;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.graph.NodeSelection;
import com.gurella.studio.editor.operation.ConvertToPrefabOperation;

class ConvertToPrefabDropTargetListener extends DropTargetAdapter {
	private final SceneEditorContext context;

	ConvertToPrefabDropTargetListener(SceneEditorContext context) {
		this.context = context;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_MOVE) == 0 || getTransferedNode() == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		event.detail = DND.DROP_MOVE;
	}

	private static SceneNode2 getTransferedNode() {
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
		SceneNode2 node = getTransferedNode();
		Shell shell = event.display.getActiveShell();
		String nodeName = Optional.ofNullable(node.getName()).filter(n -> Values.isNotBlank(n)).orElse("Prefab");
		String prefabName = getPrefabName(shell, folder, nodeName);
		if (prefabName == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		try {
			convertToPrefab(folder, node, prefabName);
		} catch (Exception e) {
			GurellaStudioPlugin.showError(e, "Error while converting to prefab.");
		}
	}

	private static String getPrefabName(Shell shell, IFolder folder, String nodeName) {
		String msg = "Enter node name";
		String initialValue = nodeName + ".pref";
		InputDialog dlg = new InputDialog(shell, "Add prefab", msg, initialValue,
				i -> i.length() < 3 ? "Too short" : null);
		if (dlg.open() != Window.OK) {
			return null;
		}

		String prefabName = dlg.getValue();
		IFile file = folder.getFile(prefabName);
		if (file.exists()) {
			String message = prefabName + " already exists. Do you want to replace it?";
			return MessageDialog.openQuestion(shell, "Confirm", message) ? prefabName : null;
		} else {
			return prefabName;
		}
	}

	private void convertToPrefab(IFolder folder, SceneNode2 node, String prefabName)
			throws UnsupportedEncodingException, CoreException {
		IProject project = folder.getProject();
		IPath projectPath = project.getLocation();
		IPath assetsRootPath = projectPath.append("assets");
		IPath projectAssetPath = folder.getFile(prefabName).getLocation().makeRelativeTo(projectPath);

		SceneNode2 prefab = CopyContext.copyObject(node);
		JsonOutput output = new JsonOutput();
		SceneNode2 template = Optional.ofNullable(prefab.getPrefab()).map(p -> (SceneNode2) p.get()).orElse(null);
		String source = output.serialize(projectAssetPath.toString(), ManagedObject.class, template, prefab);
		String pretty = new JsonReader().parse(source).prettyPrint(OutputType.minimal, 120);
		InputStream is = new ByteArrayInputStream(pretty.getBytes("UTF-8"));
		IFile file = project.getFile(projectAssetPath);
		if (file.exists()) {
			file.setContents(is, true, true, new NullProgressMonitor());
		} else {
			file.create(is, true, new NullProgressMonitor());
		}

		IPath gdxAssetPath = file.getLocation().makeRelativeTo(assetsRootPath);
		AssetService.put(prefab, gdxAssetPath.toString());
		String errMsg = "Error while converting to prefab";
		context.executeOperation(new ConvertToPrefabOperation(context.editorId, node, prefab), errMsg);
	}
}