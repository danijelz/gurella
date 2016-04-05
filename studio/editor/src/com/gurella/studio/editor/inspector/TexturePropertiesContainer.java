package com.gurella.studio.editor.inspector;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.asset.properties.TextureProperties;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class TexturePropertiesContainer extends PropertiesContainer<TexturePropertiesContainer.TextureResource> {
	private ModelPropertiesContainer<TextureProperties> loaderProperties;
	private Composite imageComposite;
	private Image image;

	public TexturePropertiesContainer(InspectorView parent, TextureResource target) {
		super(parent, target);
		IFile file = target.file;
		setText(file.getName());
		FormToolkit toolkit = getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		getBody().setLayout(new GridLayout(1, false));

		loaderProperties = new ModelPropertiesContainer<TextureProperties>(getGurellaEditor(), getBody(),
				findTextureProperties(target.file));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		loaderProperties.setLayoutData(layoutData);

		imageComposite = toolkit.createComposite(getBody(), SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 300;
		imageComposite.setLayoutData(layoutData);
		getBody().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				getBody().layout(true, true);
			}
		});

		imageComposite.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				if (image == null) {
					gc.drawString("No image", 0, 0);
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
		});

		try {
			InputStream contents = file.getContents(true);
			image = new Image(getDisplay(), contents);
			addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					if (image != null) {
						image.dispose();
					}
				}
			});
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		reflow(true);
	}

	private TextureProperties findTextureProperties(IFile file) {
		// TODO Auto-generated method stub
		return new TextureProperties();
	}

	public static class TextureResource {
		IFile file;

		public TextureResource(IFile file) {
			super();
			this.file = file;
		}
	}
}
