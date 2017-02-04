package com.gurella.studio.editor.inspector.bitmapfont;

import java.io.InputStream;
import java.util.stream.IntStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.asset.properties.BitmapFontProperties;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.ui.bean.DefaultBeanEditor;

public class BitmapFontInspectableContainer extends InspectableContainer<IFile> {
	private DefaultBeanEditor<BitmapFontProperties> textureProperties;
	private CTabFolder pages;

	public BitmapFontInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		textureProperties = new DefaultBeanEditor<>(getBody(), editorContext.editorId, findProperties(target));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		textureProperties.setLayoutData(layoutData);

		pages = new CTabFolder(getBody(), SWT.TOP | SWT.FLAT | SWT.MULTI);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		pages.setLayoutData(layoutData);

		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		BitmapFontData bitmapFontData = new BitmapFontData(fileHandle, false);

		IntStream.range(0, bitmapFontData.imagePaths.length).forEach(i -> addPage(bitmapFontData, i));

		pages.setSelection(0);
		toolkit.adapt(pages);

		reflow(true);
	}

	private void addPage(BitmapFontData bitmapFontData, int i) {
		CTabItem item = new CTabItem(pages, SWT.NONE);
		item.setText("Page " + i);
		BitmapFontPage control = new BitmapFontPage(pages, bitmapFontData, i);
		GurellaStudioPlugin.getToolkit().adapt(control);
		item.setControl(control);
	}

	private BitmapFontProperties findProperties(IFile file) {
		// TODO Auto-generated method stub
		return new BitmapFontProperties();
	}

	private static class BitmapFontPage extends Composite {
		private BitmapFontData data;
		private int index;

		private Composite imageComposite;
		private Image image;

		public BitmapFontPage(Composite parent, BitmapFontData data, int index) {
			super(parent, SWT.NONE);
			this.data = data;
			this.index = index;

			setLayout(new GridLayout());

			FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

			createImage();
			imageComposite = toolkit.createComposite(this, SWT.BORDER);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			layoutData.minimumWidth = 200;
			layoutData.minimumHeight = 300;
			imageComposite.setLayoutData(layoutData);
			imageComposite.addListener(SWT.Paint, (e) -> paintImage(e.gc));
		}

		private void createImage() {
			try {
				FileHandle file = new FileHandle(data.imagePaths[index]);
				InputStream contents = file.read();
				image = new Image(getDisplay(), contents);
				addListener(SWT.Dispose, (e) -> image.dispose());
			} catch (Exception e) {
				throw new GdxRuntimeException(e);
			}
		}

		private void paintImage(GC gc) {
			if (image == null) {
				int paneWidth = imageComposite.getSize().x;
				int paneHeight = imageComposite.getSize().y;
				String noImageStr = "No image";
				Point extent = gc.stringExtent(noImageStr);
				int left = (int) ((paneWidth - extent.x) * 0.5f);
				int top = (int) ((paneHeight - extent.y) * 0.5f);
				gc.drawString(noImageStr, left, top);
			} else {
				int imageWidth = image.getBounds().width;
				int imageHeight = image.getBounds().height;
				int paneWidth = imageComposite.getSize().x - 4;
				int paneHeight = imageComposite.getSize().y - 4;

				float widthRatio = (float) imageWidth / (float) paneWidth;
				float heightRatio = (float) imageHeight / (float) paneHeight;
				float ratio;

				if (widthRatio <= 1 && heightRatio <= 1) {
					ratio = 1;
				} else {
					ratio = 1 / Math.max(widthRatio, heightRatio);

				}

				int left = (int) ((paneWidth - imageWidth * ratio) / 2) + 1;
				int top = (int) ((paneHeight - imageHeight * ratio) / 2) + 1;

				int destWidth = (int) (imageWidth * ratio);
				int destHeight = (int) (imageHeight * ratio);
				gc.drawRectangle(left - 1, top - 1, destWidth + 1, destHeight + 1);
				gc.drawImage(image, 0, 0, imageWidth, imageHeight, left, top, destWidth, destHeight);
			}
		}
	}
}
