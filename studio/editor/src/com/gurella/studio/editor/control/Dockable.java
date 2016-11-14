package com.gurella.studio.editor.control;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.ViewActivityListener;
import com.gurella.studio.editor.utils.UiUtils;

class Dockable extends Composite {
	int extent = 300;
	int extentMinimized = 300;
	int position;
	boolean expanded;

	CTabFolder tabFolder;
	SashDragManager sashDragManager;
	private Composite sash;
	private DockableTabFolderRenderer renderer;

	private Composite closedComposite;
	private ToolBar dockToolBar;
	private ToolItem maxItem;
	private Image maxImage;

	ToolBar itemsToolBar;

	private CollapseRunnable collapseRunnable = new CollapseRunnable(this);

	public Dockable(Dock parent, int position) {
		super(parent, SWT.NONE);
		this.position = position;
		int cols = position == SWT.BOTTOM ? 1 : 2;
		GridLayoutFactory.swtDefaults().numColumns(cols).margins(0, 0).spacing(0, 0).applyTo(this);

		createTabFolder();
		createSash();
		createClosedComposite();

		if (position != SWT.LEFT) {
			sash.moveAbove(tabFolder);
		}
	}

	@Override
	public Dock getParent() {
		return (Dock) super.getParent();
	}

	protected void createTabFolder() {
		tabFolder = new DockableTabFolder(this, SWT.BORDER | SWT.MULTI);
		tabFolder.setMinimizeVisible(true);
		tabFolder.setSingle(true);
		renderer = new DockableTabFolderRenderer(tabFolder);

		GridData data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalIndent = 0;
		data.horizontalIndent = 0;
		tabFolder.setLayoutData(data);

		tabFolder.addCTabFolder2Listener(new DockableTabFolderListener());

		tabFolder.addListener(SWT.MouseDoubleClick, e -> onTabDoubleClick(e));
		tabFolder.addListener(SWT.DragDetect, new DragListener(this));
	}

	private void onTabDoubleClick(Event e) {
		if (!tabFolder.getMinimized() && tabFolder.getItem(new Point(e.x, e.y)) == null) {
			tabFolder.setMinimized(true);
			stateChanged();
		}
	}

	protected void createSash() {
		sash = new Composite(this, 0);
		Display display = getDisplay();
		sash.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		sash.setCursor(display.getSystemCursor(position == SWT.BOTTOM ? SWT.CURSOR_SIZENS : SWT.CURSOR_SIZEWE));
		sash.addListener(SWT.Paint, e -> paintSash(e.gc));

		sashDragManager = new SashDragManager(this);
		sash.addMouseMoveListener(sashDragManager);
		sash.addMouseListener(sashDragManager);

		if (position == SWT.BOTTOM) {
			GridData data = new GridData(10, 4);
			data.verticalAlignment = SWT.BEGINNING;
			data.horizontalAlignment = SWT.FILL;
			data.grabExcessHorizontalSpace = true;
			sash.setLayoutData(data);
		} else {
			GridData data = new GridData(4, 10);
			data.verticalAlignment = SWT.FILL;
			data.horizontalAlignment = SWT.END;
			data.verticalSpan = 2;
			data.grabExcessVerticalSpace = true;
			sash.setLayoutData(data);
		}
	}

	private void paintSash(GC gc) {
		Rectangle bounds = getBounds();
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		gc.drawLine(0, 0, bounds.width, 0);
		gc.drawLine(0, 0, 0, bounds.height);
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1);
		gc.drawLine(0, bounds.height - 1, bounds.width - 1, bounds.height - 1);
	}

	private void createClosedComposite() {
		closedComposite = new Composite(this, 0);
		closedComposite.addListener(SWT.MouseHover, e -> expandClosedComposite());
		closedComposite.addListener(SWT.MouseUp, e -> expandClosedComposite());
		closedComposite.addListener(SWT.MouseDoubleClick, e -> onClosedCompositeDoubleClick());
		closedComposite.addListener(SWT.Paint, e -> UiUtils.paintSimpleBorder(closedComposite, e.gc));

		GridData data = new GridData();
		data.verticalAlignment = SWT.FILL;
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.exclude = true;
		closedComposite.setLayoutData(data);

		int numColumns = position == SWT.BOTTOM ? 2 : 1;
		GridLayout gridLayout = new GridLayout(numColumns, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		closedComposite.setLayout(gridLayout);

		if (position == SWT.BOTTOM) {
			createItemsToolBar();
			createDockToolBar();
		} else {
			createDockToolBar();
			createItemsToolBar();
		}
	}

	private void expandClosedComposite() {
		if (tabFolder.getMinimized() && !expanded) {
			expanded = true;
			stateChanged();
			tabFolder.layout(true, true);
			getDisplay().timerExec(60, () -> tabFolder.redraw());
			getDisplay().timerExec(500, collapseRunnable);
		}
	}

	private void onClosedCompositeDoubleClick() {
		if (tabFolder.getMinimized()) {
			tabFolder.setMinimized(false);
			stateChanged();
		}
	}

	private void createDockToolBar() {
		boolean sideBar = position != SWT.BOTTOM;

		int style = SWT.FLAT;
		if (sideBar) {
			style |= SWT.VERTICAL;
		}

		dockToolBar = new ToolBar(closedComposite, style);
		GridData data = new GridData();
		data.verticalAlignment = sideBar ? SWT.BEGINNING : SWT.CENTER;
		data.horizontalAlignment = sideBar ? SWT.CENTER : SWT.END;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = !sideBar;
		dockToolBar.setLayoutData(data);

		maxImage = createMaxButtonImage();
		addDisposeListener(e -> maxImage.dispose());

		maxItem = new ToolItem(dockToolBar, SWT.PUSH);
		maxItem.setImage(maxImage);
		maxItem.setToolTipText(SWT.getMessage("SWT_Restore"));
		maxItem.addListener(SWT.Selection, e -> onMaxItemSelected());
		maxItem.addListener(SWT.DefaultSelection, e -> onMaxItemSelected());
	}

	private void onMaxItemSelected() {
		tabFolder.setMinimized(false);
		stateChanged();
	}

	private void createItemsToolBar() {
		boolean sideBar = position != SWT.BOTTOM;

		int style = SWT.FLAT;
		if (sideBar) {
			style |= SWT.VERTICAL;
		}

		itemsToolBar = new ToolBar(closedComposite, style);
		GridData data = new GridData();
		data.verticalAlignment = sideBar ? SWT.BEGINNING : SWT.CENTER;
		data.horizontalAlignment = sideBar ? SWT.CENTER : SWT.BEGINNING;
		data.grabExcessVerticalSpace = sideBar;
		itemsToolBar.setLayoutData(data);
	}

	private Image createMaxButtonImage() {
		Display display = getDisplay();
		GC tempGC = new GC(this);
		Point size = renderer.computeSize(CTabFolderRenderer.PART_MAX_BUTTON, SWT.NONE, tempGC, SWT.DEFAULT,
				SWT.DEFAULT);
		tempGC.dispose();
		Rectangle trim = renderer.computeTrim(CTabFolderRenderer.PART_MAX_BUTTON, SWT.NONE, 0, 0, 0, 0);
		Image image = new Image(display, size.x - trim.width, size.y - trim.height);
		GC gc = new GC(image);
		RGB transparent = new RGB(0xFD, 0, 0);
		Color transColor = new Color(display, transparent);
		gc.setBackground(transColor);
		gc.fillRectangle(image.getBounds());

		Rectangle maxRect = new Rectangle(trim.x, trim.y, size.x, size.y);
		int x = maxRect.x + (maxRect.width - 10) / 2;
		int y = maxRect.y + 3;

		gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		gc.fillRectangle(x, y + 3, 5, 4);
		gc.fillRectangle(x + 2, y, 5, 4);
		gc.drawRectangle(x, y + 3, 5, 4);
		gc.drawRectangle(x + 2, y, 5, 4);
		gc.drawLine(x + 3, y + 1, x + 6, y + 1);
		gc.drawLine(x + 1, y + 4, x + 4, y + 4);

		gc.dispose();
		transColor.dispose();
		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(transparent);
		image.dispose();
		return new Image(display, imageData);
	}

	public int getItemCount() {
		return tabFolder.getItemCount();
	}

	void stateChanged() {
		boolean contentVisible = isContentVisible();
		closedComposite.setVisible(!contentVisible);
		closedComposite.layout(true, true);
		((GridData) closedComposite.getLayoutData()).exclude = contentVisible;
		tabFolder.setVisible(contentVisible);
		((GridData) tabFolder.getLayoutData()).exclude = !contentVisible;
		sash.setVisible(contentVisible);
		((GridData) sash.getLayoutData()).exclude = !contentVisible;

		GridLayout layout = (GridLayout) getLayout();
		layout.numColumns = contentVisible && position != SWT.BOTTOM ? 2 : 1;

		layoutParent();
		itemsToolBar.pack();
	}

	public boolean isContentVisible() {
		return !tabFolder.getMinimized() || expanded;
	}

	protected boolean isMinimized() {
		return tabFolder.getMinimized();
	}

	public void setMinimized(boolean minimized) {
		if (tabFolder.getMinimized() != minimized) {
			tabFolder.setMinimized(minimized);
			stateChanged();
		}
	}

	public boolean isExpanded() {
		return expanded;
	}

	protected void layoutParent() {
		getParent().layout(true);
	}

	void handleSashDragged(int shiftAmount) {
		if (isMinimized()) {
			int newExtent = extentMinimized + (position != SWT.LEFT ? -shiftAmount : shiftAmount);
			if (extentMinimized != newExtent) {
				if (position == SWT.BOTTOM) {
					extentMinimized = Math.max(Math.min(newExtent, getParent().getClientArea().height), 50);
				} else {
					extentMinimized = Math.max(Math.min(newExtent, getParent().getClientArea().width), 50);
				}
			}
		} else {
			int newExtent = extent + (position != SWT.LEFT ? -shiftAmount : shiftAmount);
			if (extent != newExtent) {
				if (position == SWT.BOTTOM) {
					extent = Math.max(Math.min(newExtent, getParent().getClientArea().height - 50), 50);
				} else {
					int maxWidth = getParent().getClientArea().width - 60;
					extent = Math.max(Math.min(newExtent, maxWidth), 50);
					Dockable oppositeDock = getOppositeDock();
					int oppositeExtent = oppositeDock.extent;
					if (extent + oppositeExtent > maxWidth) {
						oppositeDock.extent = maxWidth - extent;
					}
				}
			}
		}
		layoutParent();
	}

	private Dockable getOppositeDock() {
		if (position == SWT.LEFT) {
			return getParent().east;
		} else {
			return getParent().west;
		}
	}

	public int addItem(DockableView view, String title, Image image) {
		return addItem(view, title, image, tabFolder.getItemCount());
	}

	public int addItem(DockableView view, String title, Image image, int index) {
		CTabItem tabItem = new CTabItem(tabFolder, SWT.CLOSE, index);
		tabItem.setText(title);
		tabItem.setImage(image);
		tabItem.setControl(view);
		tabFolder.setSingle(getItemCount() < 2);

		ToolItem toolItem = new ToolItem(itemsToolBar, SWT.PUSH, index);
		toolItem.setToolTipText(title);
		toolItem.setImage(image);
		toolItem.setData(tabItem);
		toolItem.addListener(SWT.Selection, e -> onToolItemSelected(e));

		tabItem.setData(toolItem);
		itemsToolBar.pack(true);
		tabFolder.setSelection(tabItem);
		layoutParent();

		return index;
	}

	private void onToolItemSelected(Event e) {
		ToolItem toolItem = (ToolItem) e.widget;
		tabFolder.setSelection((CTabItem) toolItem.getData());
		expanded = true;
		stateChanged();
		tabFolder.layout(true, true);
		getDisplay().timerExec(60, () -> tabFolder.redraw());
		getDisplay().timerExec(500, collapseRunnable);
	}

	void setSelection(int viewIndex) {
		CTabItem[] items = tabFolder.getItems();
		if (viewIndex < 0 || viewIndex >= items.length) {
			return;
		}
		tabFolder.setSelection(viewIndex);
	}

	Rectangle computeTabFolderTrim(int part, int state, int x, int y, int width, int height) {
		return renderer.computeTrim(part, state, x, y, width, height);
	}

	boolean contains(DockableView view) {
		return Arrays.stream(tabFolder.getItems()).filter(i -> i.getControl() == view).findAny().isPresent();
	}

	int getIndex(DockableView view) {
		return Arrays.stream(tabFolder.getItems()).map(i -> i.getControl()).collect(toList()).indexOf(view);
	}

	int getSelectionIndex() {
		return tabFolder.getSelectionIndex();
	}

	private final class DockableTabFolderListener extends CTabFolder2Adapter {
		@Override
		public void restore(CTabFolderEvent event) {
			tabFolder.setMinimized(false);
			stateChanged();
		}

		@Override
		public void minimize(CTabFolderEvent event) {
			tabFolder.setMinimized(true);
			stateChanged();
		}

		@Override
		public void close(CTabFolderEvent event) {
			CTabItem tabItem = (CTabItem) event.item;
			DockableView view = (DockableView) tabItem.getControl();
			ToolItem toolItem = (ToolItem) tabItem.getData();
			toolItem.dispose();
			itemsToolBar.pack();

			tabItem.dispose();
			tabFolder.setSingle(getItemCount() < 2);
			layoutParent();

			event.doit = false;
			int editorId = getParent().editor.id;
			EventService.post(editorId, ViewActivityListener.class, l -> l.viewClosed(view));
		}
	}
}