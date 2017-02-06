package com.gurella.studio.editor.inspector.polygonregion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.asset.loader.texture.TextureProperties;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.ui.bean.DefaultBeanEditor;

public class PolygonRegionInspectableContainer extends InspectableContainer<IFile> {
	private DefaultBeanEditor<TextureProperties> propertiesEditor;
	private Composite imageComposite;
	private Image image;
	private Transform transform;
	private int[] vertices;

	public PolygonRegionInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		propertiesEditor = new DefaultBeanEditor<>(getBody(), editorContext.editorId, findProperties(target));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		propertiesEditor.setLayoutData(layoutData);

		imageComposite = toolkit.createComposite(getBody(), SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		imageComposite.setLayoutData(layoutData);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(target.getContents(true)), 256);

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith("s")) {
					String[] polygonStrings = line.substring(1).trim().split(",");
					int length = polygonStrings.length;
					vertices = new int[length];
					IntStream.range(0, length).forEach(i -> vertices[i] = Float.valueOf(polygonStrings[i]).intValue());
				} else if (line.startsWith("i ")) {
					String subPath = line.substring("i ".length());
					IPath pah = target.getLocation().removeLastSegments(1).append(subPath);
					image = new Image(getDisplay(), new FileHandle(pah.toString()).read());
					transform = new Transform(getDisplay());
				}
			}

			if (image == null) {
				return;// TODO
			}

			if (vertices == null) {
				vertices = new int[0];// TODO
			}

			int height = image.getBounds().height;
			IntStream.range(0, vertices.length / 2).forEach(i -> updateVertice(i, height));
			addListener(SWT.Dispose, e -> doDispose());
		} catch (Exception e) {
			throw new GdxRuntimeException(e);
		}

		reflow(true);
		imageComposite.addListener(SWT.Paint, (e) -> paintImage(e.gc));
	}

	private void doDispose() {
		image.dispose();
		transform.dispose();
	}

	private void updateVertice(int i, int height) {
		int index = i * 2 + 1;
		vertices[index] = height - vertices[index];
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

			gc.setLineWidth(2);
			gc.setAntialias(SWT.ON);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			gc.drawPolygon(vertices);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
			IntStream.range(0, vertices.length / 2).forEach(i -> drawPoint(gc, i));
		}
	}

	private void drawPoint(GC gc, int i) {
		int index = i * 2;
		int x = vertices[index];
		int y = vertices[index + 1];
		gc.drawRectangle(x - 1, y - 1, 2, 2);
	}

	private TextureProperties findProperties(IFile file) {
		// TODO Auto-generated method stub
		return new TextureProperties();
	}
}
