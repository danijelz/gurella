package com.gurella.studio.editor.inspector.textureatlas;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.gurella.engine.asset.properties.TextureAtlasProperties;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.ui.bean.DefaultBeanEditor;

public class TextureAtlasInspectableContainer extends InspectableContainer<IFile> {
	private DefaultBeanEditor<TextureAtlasProperties> textureProperties;
	private CTabFolder pages;

	public TextureAtlasInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		int editorId = editorContext.editorId;
		IJavaProject javaProject = editorContext.javaProject;
		textureProperties = new DefaultBeanEditor<>(getBody(), editorId, javaProject, findProperties(target));
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

		private TableViewer tableViewer;
		private Composite imageComposite;
		private Image image;
		private Transform transform;

		public TextureAtlasPage(Composite parent, Page page, TextureAtlasData textureAtlasData) {
			super(parent, SWT.NONE);
			this.page = page;
			Region[] regionArr = textureAtlasData.getRegions().toArray(Region.class);
			regions = Arrays.<Region> stream(regionArr).filter(r -> r.page == page).collect(Collectors.toList());

			setLayout(new GridLayout());

			FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
			tableViewer = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			Table table = tableViewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			layoutData.heightHint = 100;
			layoutData.minimumHeight = 100;
			table.setLayoutData(layoutData);
			table.addListener(SWT.Selection, e -> imageComposite.redraw());
			createTableColumns();
			toolkit.adapt(table);
			tableViewer.setContentProvider(ArrayContentProvider.getInstance());
			tableViewer.setInput(regions.toArray());

			transform = new Transform(getDisplay());
			addListener(SWT.Dispose, e -> transform.dispose());

			createImage();
			imageComposite = toolkit.createComposite(this, SWT.BORDER);
			layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			layoutData.minimumWidth = 200;
			layoutData.minimumHeight = 300;
			imageComposite.setLayoutData(layoutData);
			imageComposite.addListener(SWT.Paint, (e) -> paintImage(e.gc));
		}

		private void createTableColumns() {
			TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			nameColumn.getColumn().setWidth(200);
			nameColumn.getColumn().setText("Name");
			nameColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Region) element).name;
				}
			});
		}

		private void createImage() {
			try {
				InputStream contents = page.textureFile.read();
				image = new Image(getDisplay(), contents);
				addListener(SWT.Dispose, e -> image.dispose());
			} catch (Exception e) {
				GurellaStudioPlugin.showError(e, "Error loading image");
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

				transform.identity();
				transform.translate(left, top);
				transform.scale(ratio, ratio);
				gc.setTransform(transform);

				gc.drawRectangle(1, 1, imageWidth + 1, imageHeight + 1);
				gc.drawImage(image, 0, 0);

				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				gc.setLineStyle(SWT.LINE_DOT);
				regions.forEach(r -> drawRegionBorder(gc, r));

				IStructuredSelection selection = tableViewer.getStructuredSelection();
				Object element = selection.getFirstElement();
				if (element instanceof Region) {
					gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
					gc.setLineStyle(SWT.LINE_SOLID);
					drawRegionBorder(gc, (Region) element);
				}
			}
		}

		private static void drawRegionBorder(GC gc, Region r) {
			gc.drawRectangle(r.left, r.top, (r.rotate ? r.height : r.width), (r.rotate ? r.width : r.height));
		}
	}
}
