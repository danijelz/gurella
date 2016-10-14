package com.gurella.studio.editor.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.event.SceneEditorViewClosedEvent;
import com.gurella.studio.editor.utils.UiUtils;

class Dockable extends Composite {
	CTabFolder tabFolder;
	private DockableTabFolderRenderer renderer;
	private Composite sash;
	private SashDragManager sashDragManager;
	int extent = 300;
	int extentMinimized = 300;
	private int position;
	private boolean expanded;

	private Composite closedComposite;
	private ToolBar dockToolBar;
	private ToolItem maxItem;
	private Image maxImage;

	private ToolBar itemsToolBar;

	private CollapseRunnable collapseRunnable = new CollapseRunnable();

	public Dockable(Dock parent, int position) {
		super(parent, SWT.NONE);
		this.position = position;

		GridLayout layout = new GridLayout(position == SWT.BOTTOM ? 1 : 2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

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
		tabFolder = new CTabFolderImpl(this, SWT.BORDER | SWT.MULTI);
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

		tabFolder.addCTabFolder2Listener(new CTabFolder2ListenerImpl());

		tabFolder.addListener(SWT.MouseDoubleClick, e -> onTabDoubleClick(e));
		tabFolder.addListener(SWT.DragDetect, new DragListener());
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

		sashDragManager = new SashDragManager();
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

	private void stateChanged() {
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

	private void handleSashDragged(int shiftAmount) {
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

	public int addItem(String title, Image image, Control control) {
		return addItem(title, image, control, tabFolder.getItemCount());
	}

	public int addItem(String title, Image image, Control control, int index) {
		CTabItem tabItem = new CTabItem(tabFolder, SWT.CLOSE, index);
		tabItem.setText(title);
		tabItem.setImage(image);
		tabItem.setControl(control);
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

	void setSelection(Control control) {
		for (CTabItem item : tabFolder.getItems()) {
			if (item.getControl() == control) {
				tabFolder.setSelection(item);
			}
		}
	}

	private final class CTabFolder2ListenerImpl extends CTabFolder2Adapter {
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
			EventService.post(getParent().editor.id, new SceneEditorViewClosedEvent(view));
		}
	}

	private final class CTabFolderImpl extends CTabFolder {
		private CTabFolderImpl(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		public Rectangle getClientArea() {
			checkWidget();
			Rectangle trim = renderer.computeTrim(CTabFolderRenderer.PART_BODY, SWT.FILL, 0, 0, 0, 0);
			Point size = getSize();

			if (!isContentVisible()) {
				return new Rectangle(-trim.x, -trim.y, 0, 0);
			} else {
				int width = size.x - trim.width;
				int height = size.y - trim.height;
				return new Rectangle(-trim.x, -trim.y, width, height);
			}
		}
	}

	private final class DragListener implements Listener {
		private CTabItem dragItem;
		private final Point mouseLocation = new Point(0, 0);

		@Override
		public void handleEvent(Event event) {
			mouseLocation.x = event.x;
			mouseLocation.y = event.y;

			if (dragItem == null) {
				dragItem = tabFolder.getItem(mouseLocation);
				if (dragItem == null) {
					return;
				}

				final Dock mainView = getParent();
				final Tracker tracker = new Tracker(mainView, SWT.NONE);
				tracker.setStippled(true);
				tracker.addListener(SWT.Move, e -> onMouseMoved(mainView, tracker));

				if (tracker.open()) {
					completeDrag();
				}

				tracker.dispose();
			}
		}

		private void completeDrag() {
			Dockable dockable = findDockComponent();
			if (dockable == null) {
				final Dock mainView = getParent();
				Point point = mainView.toControl(getDisplay().getCursorLocation());
				Rectangle bounds = mainView.getBounds();
				if (point.x >= 0 && point.x <= 200 && mainView.west.getItemCount() < 1) {
					transferItem(mainView.west, 0);
				} else if (point.x >= bounds.width - 200 && point.x <= bounds.width
						&& mainView.east.getItemCount() < 1) {
					transferItem(mainView.east, 0);
				} else if (point.y <= bounds.height && point.y >= bounds.height - 200
						&& mainView.south.getItemCount() < 1) {
					transferItem(mainView.south, 0);
				}
			} else {
				int dragItemNewIndex = getDragItemNewIndex(dockable);
				CTabFolder dragTabFolder = dockable.tabFolder;
				if (dragItemNewIndex >= 0 && (dragTabFolder.getItemCount() <= dragItemNewIndex
						|| dragTabFolder.getItem(dragItemNewIndex) != dragItem)) {
					transferItem(dockable, dragItemNewIndex);
				}
			}

			dragItem = null;
		}

		protected int getDragItemNewIndex(Dockable dockable) {
			CTabFolder dragTabFolder = dockable.tabFolder;
			if (dockable.isContentVisible()) {
				Point point = dragTabFolder.toControl(getDisplay().getCursorLocation());
				CTabItem item = dragTabFolder.getItem(point);
				if (dragItem == item) {
					return -1;
				}

				if (item == null) {
					return dragTabFolder.getItemCount();
				} else {
					Rectangle bounds = item.getBounds();
					boolean after = point.x > bounds.x + bounds.width / 2;
					int itemIndex = dragTabFolder.indexOf(item);
					return after ? itemIndex + 1 : itemIndex;
				}
			} else {
				Point point = dockable.itemsToolBar.toControl(getDisplay().getCursorLocation());
				ToolItem item = dockable.itemsToolBar.getItem(point);
				if (item == null) {
					return dragTabFolder.getItemCount();
				} else {
					Rectangle bounds = item.getBounds();
					boolean after = point.x > bounds.x + bounds.width / 2;
					int itemIndex = dockable.itemsToolBar.indexOf(item);
					return after ? itemIndex + 1 : itemIndex;
				}
			}
		}

		protected void transferItem(Dockable dockable, int dragItemNewIndex) {
			int itemIndex = dockable.addItem(dragItem.getText(), dragItem.getImage(), null, dragItemNewIndex);
			CTabFolder targetTabFolder = dockable.tabFolder;
			CTabItem newItem = targetTabFolder.getItem(itemIndex);
			Control itemControl = dragItem.getControl();
			if (itemControl != null) {
				itemControl.setParent(targetTabFolder);
				newItem.setControl(itemControl);
				dragItem.setControl(null);
			}

			ToolItem toolItem = (ToolItem) dragItem.getData();
			toolItem.dispose();
			dragItem.dispose();

			targetTabFolder.setSelection(newItem);
			targetTabFolder.pack();

			tabFolder.setSingle(tabFolder.getItemCount() < 2);
			dockable.layout(true);
			itemControl.setVisible(true);
			layoutParent();
		}

		private Dockable findDockComponent() {
			Point cursorLocation = getDisplay().getCursorLocation();
			Dock parent = getParent();
			Point controlLocation = parent.toControl(cursorLocation);
			if (parent.east.getBounds().contains(controlLocation)) {
				return parent.east;
			} else if (parent.west.getBounds().contains(controlLocation)) {
				return parent.west;
			} else if (parent.south.getBounds().contains(controlLocation)) {
				return parent.south;
			}
			return null;
		}

		private void onMouseMoved(final Dock mainView, final Tracker tracker) {
			Dockable dockable = findDockComponent();
			if (dockable == null) {
				Point point = mainView.toControl(getDisplay().getCursorLocation());
				Rectangle bounds = mainView.getBounds();
				if (point.x >= 0 && point.x <= 200 && mainView.west.getItemCount() < 1) {
					Rectangle clientArea = mainView.getClientArea();
					int southDockHeight = mainView.getDockHeight(mainView.south);
					tracker.setRectangles(new Rectangle[] {
							new Rectangle(clientArea.x, clientArea.y, 200, clientArea.height - southDockHeight - 2) });
					tracker.setCursor(mainView.dragWest);
				} else if (point.x >= bounds.width - 200 && point.x <= bounds.width
						&& mainView.east.getItemCount() < 1) {
					Rectangle clientArea = mainView.getClientArea();
					int southDockHeight = mainView.getDockHeight(mainView.south);
					tracker.setRectangles(new Rectangle[] { new Rectangle(clientArea.width - 200, clientArea.y, 200,
							clientArea.height - southDockHeight - 2) });
					tracker.setCursor(mainView.dragEast);
				} else if (point.y <= bounds.height && point.y >= bounds.height - 200
						&& mainView.south.getItemCount() < 1) {
					Rectangle clientArea = mainView.getClientArea();
					tracker.setRectangles(new Rectangle[] {
							new Rectangle(clientArea.x, clientArea.height - 200, clientArea.width, 200) });
					tracker.setCursor(mainView.dragSouth);
				} else {
					tracker.setRectangles(new Rectangle[] {});
					tracker.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_NO));
				}
			} else {
				tracker.setRectangles(new Rectangle[] {});
				tracker.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			}
		}
	}

	private class CollapseRunnable implements Runnable {
		@Override
		public void run() {
			if (!expanded) {
				return;
			} else if (sashDragManager.dragging || isDescendantOf(getDisplay().getCursorControl())) {
				Display.getCurrent().timerExec(500, this);
			} else {
				expanded = false;
				stateChanged();
			}
		}

		private boolean isDescendantOf(Control descendant) {
			Control temp = descendant;
			if (temp == null) {
				return false;
			}

			while (temp != null) {
				if (Dockable.this == temp) {
					return true;
				}
				temp = temp.getParent();
			}

			return false;
		}
	}

	private class SashDragManager extends MouseAdapter implements MouseMoveListener {
		protected boolean dragging = false;
		protected boolean correctState = false;
		protected boolean mouseDown = false;
		protected int originPosition;

		@Override
		public void mouseDown(MouseEvent me) {
			if (me.button != 1) {
				return;
			}
			mouseDown = true;
			correctState = expanded || !tabFolder.getMinimized();
			originPosition = position == SWT.BOTTOM ? me.y : me.x;
		}

		@Override
		public void mouseMove(MouseEvent me) {
			if (mouseDown) {
				dragging = true;
			}

			if (dragging && correctState) {
				if (position == SWT.BOTTOM) {
					handleSashDragged(me.y - originPosition);
				} else {
					handleSashDragged(me.x - originPosition);
				}
			}
		}

		@Override
		public void mouseUp(MouseEvent me) {
			dragging = false;
			correctState = false;
			mouseDown = false;
			layoutParent();
		}
	}
}