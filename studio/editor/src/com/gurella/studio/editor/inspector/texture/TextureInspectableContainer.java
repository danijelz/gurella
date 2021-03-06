package com.gurella.studio.editor.inspector.texture;

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

import com.gurella.engine.asset.loader.texture.TextureProperties;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.ui.bean.DefaultBeanEditor;
import com.gurella.studio.editor.utils.Try;

public class TextureInspectableContainer extends InspectableContainer<IFile> {
	private DefaultBeanEditor<TextureProperties> propertiesEditor;
	private Composite imageComposite;
	private Image image;

	private TextureProperties properties;

	public TextureInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));
		getBody().addListener(SWT.Resize, (e) -> getBody().layout(true, true));

		addDisposeListener(e -> editorContext.unload(properties));
		properties = editorContext.loadAssetProperties(target);
		properties = properties == null ? new TextureProperties() : properties;
		propertiesEditor = new DefaultBeanEditor<>(getBody(), editorContext.editorId, properties);
		propertiesEditor.addPropertiesListener(e -> editorContext.saveProperties(target, properties));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		propertiesEditor.setLayoutData(layoutData);

		imageComposite = toolkit.createComposite(getBody(), SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		imageComposite.setLayoutData(layoutData);
		imageComposite.addListener(SWT.Paint, e -> paintImage(e.gc));

		image = Try.ignored(() -> createImage(), null);

		reflow(true);
	}

	private Image createImage() throws CoreException {
		InputStream contents = target.getContents(true);
		Image image = new Image(getDisplay(), contents);
		addListener(SWT.Dispose, e -> image.dispose());
		return image;
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
