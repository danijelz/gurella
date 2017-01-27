package com.gurella.studio.editor.inspector.pixmap;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.asset2.properties.PixmapProperties;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;

public class PixmapInspectableContainer extends InspectableContainer<IFile> {
	private Composite imageComposite;
	private Image image;

	public PixmapInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		imageComposite = toolkit.createComposite(getBody(), SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		imageComposite.setLayoutData(layoutData);
		imageComposite.addListener(SWT.Paint, e -> paintImage(e.gc));

		try {
			InputStream contents = target.getContents(true);
			image = new Image(getDisplay(), contents);
			addListener(SWT.Dispose, e -> image.dispose());
		} catch (CoreException e) {
			GurellaStudioPlugin.showError(e, "Error loading image");
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

			int destWidth = (int) (imageWidth * ratio);
			int destHeight = (int) (imageHeight * ratio);
			gc.drawRectangle(left - 1, top - 1, destWidth + 1, destHeight + 1);
			gc.drawImage(image, 0, 0, imageWidth, imageHeight, left, top, destWidth, destHeight);
		}
	}

	private PixmapProperties findProperties(IFile file) {
		// TODO Auto-generated method stub
		return new PixmapProperties();
	}
}
