package com.gurella.studio.editor.part;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.utils.UiUtils;

public class Dock extends Composite {
	private static final int CLOSED_DOCK_EXTENT = 38;

	SceneEditor editor;

	Composite center;
	Dockable east;
	Dockable south;
	Dockable west;

	private Image defaultDockImage;

	Cursor dragEast;
	Cursor dragSouth;
	Cursor dragWest;

	public Dock(SceneEditor editor, Composite parent, int style) {
		super(parent, style);
		this.editor = editor;

		defaultDockImage = GurellaStudioPlugin.createImage("icons/palette_view.gif");

		dragEast = createCursor("icons/right_source.bmp");
		dragSouth = createCursor("icons/bottom_source.bmp");
		dragWest = createCursor("icons/left_source.bmp");

		center = new Composite(this, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		center.setLayout(layout);
		center.addListener(SWT.MouseDoubleClick, e -> maximizeCenter());

		east = new Dockable(this, SWT.RIGHT);
		south = new Dockable(this, SWT.BOTTOM);
		west = new Dockable(this, SWT.LEFT);

		addListener(SWT.Resize, e -> layout(true));
		addListener(SWT.Dispose, e -> onDispose());
	}

	private void maximizeCenter() {
		east.setMinimized(true);
		west.setMinimized(true);
		south.setMinimized(true);
	}

	private void onDispose() {
		defaultDockImage.dispose();
		dragEast.dispose();
		dragSouth.dispose();
		dragWest.dispose();
	}

	public void setCenterControl(Control centerControl) {
		UiUtils.disposeChildren(center);
		centerControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		centerControl.setParent(center);
	}

	public Composite getCenter() {
		return center;
	}

	public void addItem(int position, String title, Image image, Control control) {
		getDockComponent(position).addItem(title, image == null ? defaultDockImage : image, control);
	}

	public void addItem(int position, String title, Image image, Control control, int index) {
		getDockComponent(position).addItem(title, image == null ? defaultDockImage : image, control, index);
	}

	private Dockable getDockComponent(int position) {
		if ((position & SWT.RIGHT) != 0) {
			return east;
		} else if ((position & SWT.LEFT) != 0) {
			return west;
		} else if ((position & SWT.BOTTOM) != 0) {
			return south;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Composite getDockItemParent(int position) {
		if ((position & SWT.RIGHT) != 0) {
			return east.tabFolder;
		} else if ((position & SWT.LEFT) != 0) {
			return west.tabFolder;
		} else if ((position & SWT.BOTTOM) != 0) {
			return south.tabFolder;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Cursor createCursor(String fileName) {
		ImageData imageData = GurellaStudioPlugin.getImageDescriptor(fileName).getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(new RGB(255, 255, 255));
		return new Cursor(getDisplay(), imageData, 16, 16);
	}

	@Override
	public void layout(boolean changed) {
		Rectangle clientArea = getClientArea();

		int westDockWidth = getDockWidth(west);
		int eastDockWidth = getDockWidth(east);
		int southDockHeight = getDockHeight(south);
		int maxWidth = getClientArea().width - 10;
		if (westDockWidth + eastDockWidth > maxWidth) {
			int sub = (westDockWidth + eastDockWidth - maxWidth) / 2;
			westDockWidth -= sub;
			eastDockWidth -= sub;
		}

		int westDockCenterWidth = west.isContentVisible() && west.isMinimized() ? CLOSED_DOCK_EXTENT : westDockWidth;
		int eastDockCenterWidth = east.isContentVisible() && east.isMinimized() ? CLOSED_DOCK_EXTENT : eastDockWidth;
		int southDockCenterHeight = south.isContentVisible() && south.isMinimized() ? CLOSED_DOCK_EXTENT
				: southDockHeight;

		center.setBounds(clientArea.x + westDockCenterWidth + 2, clientArea.y,
				clientArea.width - westDockCenterWidth - eastDockCenterWidth - 4,
				clientArea.height - southDockCenterHeight - 2);
		west.setBounds(clientArea.x, clientArea.y, westDockWidth, clientArea.height - southDockCenterHeight - 2);
		east.setBounds(clientArea.width - eastDockWidth, clientArea.y, eastDockWidth,
				clientArea.height - southDockCenterHeight - 2);
		south.setBounds(clientArea.x, clientArea.height - southDockHeight, clientArea.width, southDockHeight);

		if (west.isExpanded()) {
			center.moveBelow(west);
			east.moveBelow(west);
			south.moveBelow(west);
		}

		if (east.isExpanded()) {
			center.moveBelow(east);
			west.moveBelow(east);
			south.moveBelow(east);
		}

		if (south.isExpanded()) {
			center.moveBelow(south);
			east.moveBelow(south);
			west.moveBelow(south);
		}
	}

	int getDockHeight(Dockable dockable) {
		if (dockable.getItemCount() < 1) {
			return 0;
		} else if (dockable.isContentVisible()) {
			int extent = dockable.isMinimized() ? dockable.extentMinimized : dockable.extent;
			return Math.max(Math.min(extent, getClientArea().height - 50), 50);
		} else {
			return CLOSED_DOCK_EXTENT;
		}
	}

	private int getDockWidth(Dockable dockable) {
		if (dockable.getItemCount() < 1) {
			return 0;
		} else if (dockable.isContentVisible()) {
			int extent = dockable.isMinimized() ? dockable.extentMinimized : dockable.extent;
			return Math.max(Math.min(extent, getClientArea().width - 60), 50);
		} else {
			return CLOSED_DOCK_EXTENT;
		}
	}

	public void setSelection(Control control) {
		east.setSelection(control);
		west.setSelection(control);
		south.setSelection(control);
	}
}
