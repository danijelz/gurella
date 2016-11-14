package com.gurella.studio.editor.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;

import com.gurella.engine.event.EventService;
import com.gurella.studio.editor.subscription.ViewOrientationListener;

final class DragListener implements Listener {
	private final Dockable dockable;

	private CTabItem dragItem;
	private final Point mouseLocation = new Point(0, 0);

	DragListener(Dockable dockable) {
		this.dockable = dockable;
	}

	@Override
	public void handleEvent(Event event) {
		mouseLocation.x = event.x;
		mouseLocation.y = event.y;

		if (dragItem == null) {
			dragItem = this.dockable.tabFolder.getItem(mouseLocation);
			if (dragItem == null) {
				return;
			}

			final Dock mainView = this.dockable.getParent();
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
			final Dock mainView = this.dockable.getParent();
			Point point = mainView.toControl(this.dockable.getDisplay().getCursorLocation());
			Rectangle bounds = mainView.getBounds();
			if (point.x >= 0 && point.x <= 200 && mainView.west.getItemCount() < 1) {
				transferItem(mainView.west, 0);
			} else if (point.x >= bounds.width - 200 && point.x <= bounds.width && mainView.east.getItemCount() < 1) {
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

	private int getDragItemNewIndex(Dockable dockable) {
		CTabFolder dragTabFolder = dockable.tabFolder;
		if (dockable.isContentVisible()) {
			Point point = dragTabFolder.toControl(this.dockable.getDisplay().getCursorLocation());
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
			Point point = dockable.itemsToolBar.toControl(this.dockable.getDisplay().getCursorLocation());
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

	private void transferItem(Dockable dockable, int dragItemNewIndex) {
		int itemIndex = dockable.addItem(null, dragItem.getText(), dragItem.getImage(), dragItemNewIndex);
		CTabFolder targetTabFolder = dockable.tabFolder;
		CTabItem newItem = targetTabFolder.getItem(itemIndex);
		DockableView view = (DockableView) dragItem.getControl();
		if (view != null) {
			view.setParent(targetTabFolder);
			newItem.setControl(view);
			dragItem.setControl(null);
		}

		ToolItem toolItem = (ToolItem) dragItem.getData();
		toolItem.dispose();
		dragItem.dispose();

		targetTabFolder.setSelection(newItem);
		targetTabFolder.pack();

		this.dockable.tabFolder.setSingle(this.dockable.tabFolder.getItemCount() < 2);
		dockable.layout(true);
		view.setVisible(true);
		this.dockable.layoutParent();
		targetTabFolder.layout(true, true);

		int editorId = dockable.getParent().editor.id;
		int position = dockable.position;
		EventService.post(editorId, ViewOrientationListener.class, l -> l.orientationChanged(view, position));
	}

	private Dockable findDockComponent() {
		Point cursorLocation = this.dockable.getDisplay().getCursorLocation();
		Dock parent = this.dockable.getParent();
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
			Point point = mainView.toControl(this.dockable.getDisplay().getCursorLocation());
			Rectangle bounds = mainView.getBounds();
			if (point.x >= 0 && point.x <= 200 && mainView.west.getItemCount() < 1) {
				Rectangle clientArea = mainView.getClientArea();
				int southDockHeight = mainView.getDockHeight(mainView.south);
				tracker.setRectangles(new Rectangle[] {
						new Rectangle(clientArea.x, clientArea.y, 200, clientArea.height - southDockHeight - 2) });
				tracker.setCursor(mainView.dragWest);
			} else if (point.x >= bounds.width - 200 && point.x <= bounds.width && mainView.east.getItemCount() < 1) {
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
				tracker.setCursor(this.dockable.getDisplay().getSystemCursor(SWT.CURSOR_NO));
			}
		} else {
			tracker.setRectangles(new Rectangle[] {});
			tracker.setCursor(this.dockable.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		}
	}
}