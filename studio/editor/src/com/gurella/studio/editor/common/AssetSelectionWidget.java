package com.gurella.studio.editor.common;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.AssetType;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public class AssetSelectionWidget<T> extends Composite {
	private Text text;
	private Button selectAssetButton;

	private Class<T> assetType;
	private T asset;

	private BiConsumer<T, T> selectionListener;

	public AssetSelectionWidget(Composite parent, Class<T> assetType) {
		super(parent, SWT.NONE);
		this.assetType = assetType;

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.verticalSpacing = 0;
		setLayout(layout);

		text = UiUtils.createText(this);
		text.setEditable(false);
		GridDataFactory.defaultsFor(text).align(SWT.FILL, SWT.CENTER).grab(true, false).hint(100, 14).applyTo(text);

		selectAssetButton = toolkit.createButton(this, "", SWT.PUSH);
		selectAssetButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		GridDataFactory.defaultsFor(selectAssetButton).align(SWT.BEGINNING, SWT.BEGINNING).grab(false, false)
				.hint(32, 22).applyTo(selectAssetButton);
		selectAssetButton.addListener(SWT.Selection, e -> showFileDialg());

		DropTarget target = new DropTarget(text, DND.DROP_DEFAULT | DND.DROP_COPY);
		target.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		target.addDropListener(new AssetDropTarget());

		toolkit.adapt(this);
		UiUtils.paintBordersFor(this);
	}

	private boolean isValidResource(IResource item) {
		return (item instanceof IFile) && AssetType.isValidExtension(assetType, item.getFileExtension());
	}

	private void showFileDialg() {
		FileDialog dialog = new FileDialog(getShell());
		AssetType value = AssetType.value(assetType);
		dialog.setFilterExtensions(
				new String[] { Arrays.stream(value.extensions).map(e -> "*." + e).collect(Collectors.joining(";")) });

		IFile file = getEditorFile();
		IPath location = file.getLocation();
		dialog.setFilterPath(location.removeLastSegments(1).toString());
		Optional.ofNullable(dialog.open()).ifPresent(path -> assetSelected(path));
	}

	// TODO should find safer way to assets folder
	private static IFile getEditorFile() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		return input.getFile();
	}

	private void assetSelected(final String path) {
		IFile file = getEditorFile();
		IPath assetPath = new Path(path).makeRelativeTo(file.getProject().getLocation().append("assets"));

		T oldAsset = asset;
		asset = AssetService.load(assetPath.toString(), assetType);
		text.setText(assetPath.lastSegment());
		text.setMessage("");
		Optional.ofNullable(selectionListener).ifPresent(l -> l.accept(oldAsset, asset));
	}

	public void setSelectionListener(BiConsumer<T, T> selectionListener) {
		this.selectionListener = selectionListener;
	}

	public T getAsset() {
		return asset;
	}

	public void setAsset(final T asset) {
		this.asset = asset;
		if (asset == null) {
			text.setText("");
			text.setMessage("null");
		} else {
			String path = AssetService.getFileName(asset);
			text.setText(extractFileName(path));
			text.setMessage("");
		}
	}

	private static String extractFileName(String path) {
		int index = path.lastIndexOf('/');
		return index < 0 ? path : path.substring(index + 1);
	}

	private final class AssetDropTarget extends DropTargetAdapter {
		@Override
		public void dragEnter(DropTargetEvent event) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}

		@Override
		public void drop(DropTargetEvent event) {
			event.detail = DND.DROP_NONE;
			Optional.ofNullable((IResource[]) event.data).filter(d -> d != null && d.length == 1).map(d -> d[0])
					.filter(r -> isValidResource(r)).ifPresent(r -> assetSelected(r.getLocation().toString()));
		}
	}
}
