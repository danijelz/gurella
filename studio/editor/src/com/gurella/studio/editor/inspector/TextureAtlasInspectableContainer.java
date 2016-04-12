package com.gurella.studio.editor.inspector;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.gurella.engine.asset.properties.TextureAtlasProperties;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;

public class TextureAtlasInspectableContainer extends InspectableContainer<IFile> {
	private ModelEditorContainer<TextureAtlasProperties> textureProperties;
	private CTabFolder pages;

	public TextureAtlasInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		textureProperties = new ModelEditorContainer<>(getBody(), findProperties(target));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		textureProperties.setLayoutData(layoutData);

		pages = new CTabFolder(getBody(), SWT.TOP | SWT.FLAT | SWT.MULTI);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		pages.setLayoutData(layoutData);

		FileHandle fileHandle = new FileHandle(target.getLocation().toFile());
		FileHandle imagesDir = new FileHandle(target.getLocation().toFile().getParentFile());
		TextureAtlasData textureAtlasData = new TextureAtlasData(fileHandle, imagesDir, false);

		int i = 1;
		for (Page page : textureAtlasData.getPages()) {
			CTabItem item = new CTabItem(pages, SWT.NONE);
			item.setText("Page " + i++);
			TextureAtlasPage control = new TextureAtlasPage(pages, page, textureAtlasData);
			toolkit.adapt(control);
			item.setControl(control);
		}

		pages.setSelection(0);
		toolkit.adapt(pages);

		reflow(true);
	}

	private TextureAtlasProperties findProperties(IFile file) {
		// TODO Auto-generated method stub
		return new TextureAtlasProperties();
	}

	private static class TextureAtlasPage extends Composite {
		private Page page;
		private List<Region> regions;
		private Image image;

		public TextureAtlasPage(Composite parent, Page page, TextureAtlasData textureAtlasData) {
			super(parent, SWT.NONE);
			this.page = page;
			Region[] regionArr = textureAtlasData.getRegions().toArray(Region.class);
			regions = Arrays.<Region> stream(regionArr).filter(r -> r.page == page).collect(Collectors.toList());
			createImage();
			addListener(SWT.Paint, (e) -> paintImage(e.gc));
		}

		private void createImage() {
			try {
				InputStream contents = page.textureFile.read();
				image = new Image(getDisplay(), contents);
				addListener(SWT.Dispose, (e) -> image.dispose());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		private void paintImage(GC gc) {
			if (image == null) {
				int paneWidth = getSize().x;
				int paneHeight = getSize().y;
				String noImageStr = "No image";
				Point extent = gc.stringExtent(noImageStr);
				int left = (int) ((paneWidth - extent.x) * 0.5f);
				int top = (int) ((paneHeight - extent.y) * 0.5f);
				gc.drawString(noImageStr, left, top);
			} else {
				int imageWidth = image.getBounds().width;
				int imageHeight = image.getBounds().height;
				int paneWidth = getSize().x - 4;
				int paneHeight = getSize().y - 4;

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
				regions.forEach(r -> gc.drawRectangle(left + (int) (r.left * ratio), top + (int) (r.top * ratio),
						(int) ((r.rotate ? r.height : r.width) * ratio),
						(int) ((r.rotate ? r.width : r.height) * ratio)));
			}
		}
	}
}
