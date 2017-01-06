package com.gurella.studio.editor.control;

import static com.gurella.studio.GurellaStudioPlugin.getImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

public class Dock extends Composite {
	private static final int CLOSED_DOCK_EXTENT = 38;

	final int editorId;

	final Composite center;
	final Dockable east;
	final Dockable south;
	final Dockable west;

	final Cursor dragEast;
	final Cursor dragSouth;
	final Cursor dragWest;

	public Dock(Composite parent, int editorId) {
		super(parent, SWT.NONE);
		this.editorId = editorId;

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
		dragEast.dispose();
		dragSouth.dispose();
		dragWest.dispose();
	}

	public Composite getCenter() {
		return center;
	}

	void addDockItem(DockableView view, String title, Image image, int position) {
		String resolvedTitle = Values.isBlank(title) ? view.getClass().getSimpleName() : title;
		Image resolvedImage = image == null ? getImage("icons/palette_view.gif") : image;
		getDockComponent(position).addItem(view, resolvedTitle, resolvedImage);
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

	public void setSelection(int orientation, int viewIndex) {
		switch (orientation) {
		case SWT.RIGHT:
			east.setSelection(viewIndex);
			break;
		case SWT.BOTTOM:
			south.setSelection(viewIndex);
			break;
		case SWT.LEFT:
			west.setSelection(viewIndex);
			break;
		default:
			break;
		}
	}

	public int getIndex(DockableView view) {
		int index = east.getIndex(view);
		if (index > -1) {
			return index;
		}

		index = south.getIndex(view);
		if (index > -1) {
			return index;
		}

		index = west.getIndex(view);
		if (index > -1) {
			return index;
		}

		return Integer.MAX_VALUE;
	}

	public int getOrientation(DockableView view) {
		if (east.contains(view)) {
			return SWT.RIGHT;
		} else if (south.contains(view)) {
			return SWT.BOTTOM;
		} else {
			return SWT.LEFT;
		}
	}

	public int getSelectionIndex(int orientation) {
		switch (orientation) {
		case SWT.RIGHT:
			return east.getSelectionIndex();
		case SWT.BOTTOM:
			return south.getSelectionIndex();
		case SWT.LEFT:
			return west.getSelectionIndex();
		default:
			return 0;
		}
	}

	public boolean isMinimized(int orientation) {
		switch (orientation) {
		case SWT.RIGHT:
			return east.isMinimized();
		case SWT.BOTTOM:
			return south.isMinimized();
		case SWT.LEFT:
			return west.isMinimized();
		default:
			return false;
		}
	}

	public void setMinimized(int orientation, boolean minimized) {
		switch (orientation) {
		case SWT.RIGHT:
			east.setMinimized(minimized);
			break;
		case SWT.BOTTOM:
			south.setMinimized(minimized);
			break;
		case SWT.LEFT:
			west.setMinimized(minimized);
			break;
		default:
			break;
		}
	}
}
