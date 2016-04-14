package com.gurella.studio.editor.inspector;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
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
import com.gurella.engine.asset.properties.TextureProperties;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;

public class PolygonRegionInspectableContainer extends InspectableContainer<IFile> {
	private ModelEditorContainer<TextureProperties> textureProperties;
	private Composite imageComposite;
	private Image image;
	private Transform transform;
	private float[] vertices;

	public PolygonRegionInspectableContainer(InspectorView parent, IFile target) {
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

		imageComposite = toolkit.createComposite(getBody(), SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		imageComposite.setLayoutData(layoutData);
		imageComposite.addListener(SWT.Paint, (e) -> paintImage(e.gc));

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(target.getContents(true)), 256);

			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith("s")) {
					String[] polygonStrings = line.substring(1).trim().split(",");
					vertices = new float[polygonStrings.length];
					for (int i = 0, n = vertices.length; i < n; i++) {
						vertices[i] = Float.parseFloat(polygonStrings[i]);
					}
				} else if (line.startsWith("i ")) {
					FileHandle fileHandle = new FileHandle(target.getLocation().removeLastSegments(1)
							.append(line.substring("i ".length())).toString());
					image = new Image(getDisplay(), fileHandle.read());
					transform = new Transform(getDisplay());
				}
			}

			addListener(SWT.Dispose, (e) -> image.dispose());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		reflow(true);
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
		}
	}

	private TextureProperties findProperties(IFile file) {
		// TODO Auto-generated method stub
		return new TextureProperties();
	}
}
