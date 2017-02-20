package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.ImmutableArray;

public class GridLayout implements Layout {
	public int marginLeft = 0;
	public int marginTop = 0;
	public int marginRight = 0;
	public int marginBottom = 0;
	public int horizontalSpacing = 5;
	public int verticalSpacing = 5;
	public boolean columnsEqualWidth = false;

	private int cols;
	private int rows;
	private final Array<UiComponent> grid = new Array<UiComponent>();
	private int controlCount;

	@Override
	public GridPoint2 computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void layout(Composite composite, boolean flushCache) {
		prepareGrid(composite);
		if (controlCount < 1) {
			return;
		}

		initGridDatas(flushCache);

		// TODO get from composite
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		calculateColumnWidths(width);

		// TODO Auto-generated method stub
	}

	private void calculateColumnWidths(int width) {
		int availableWidth = width - horizontalSpacing * (cols - 1) - (marginLeft + marginRight);
		int expandCount = 0;
		int[] widths = new int[cols];
		int[] minWidths = new int[cols];
		boolean[] expandColumn = new boolean[cols];

		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				GridData data = getGridData(i, j);
				if (data != null) {
					int hSpan = Math.max(1, Math.min(data.horizontalSpan, cols));
					if (hSpan == 1) {
						int w = data.cacheWidth + data.horizontalIndent;
						widths[j] = Math.max(widths[j], w);
						if (data.grabExcessHorizontalSpace) {
							if (!expandColumn[j]) {
								expandCount++;
							}
							expandColumn[j] = true;
						}
						if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
							w = !data.grabExcessHorizontalSpace || data.minimumWidth == -1 ? data.cacheWidth
									: data.minimumWidth;
							w += data.horizontalIndent;
							minWidths[j] = Math.max(minWidths[j], w);
						}
					}
				}
			}

			for (int i = 0; i < rows; i++) {
				GridData data = getGridData(i, j);
				if (data != null) {
					int hSpan = Math.max(1, Math.min(data.horizontalSpan, cols));
					if (hSpan > 1) {
						int spanWidth = 0, spanMinWidth = 0, spanExpandCount = 0;
						for (int k = 0; k < hSpan; k++) {
							spanWidth += widths[j - k];
							spanMinWidth += minWidths[j - k];
							if (expandColumn[j - k])
								spanExpandCount++;
						}
						if (data.grabExcessHorizontalSpace && spanExpandCount == 0) {
							expandCount++;
							expandColumn[j] = true;
						}
						int w = data.cacheWidth + data.horizontalIndent - spanWidth - (hSpan - 1) * horizontalSpacing;
						if (w > 0) {
							if (columnsEqualWidth) {
								int equalWidth = (w + spanWidth) / hSpan;
								int remainder = (w + spanWidth) % hSpan, last = -1;
								for (int k = 0; k < hSpan; k++) {
									widths[last = j - k] = Math.max(equalWidth, widths[j - k]);
								}
								if (last > -1)
									widths[last] += remainder;
							} else {
								if (spanExpandCount == 0) {
									widths[j] += w;
								} else {
									int delta = w / spanExpandCount;
									int remainder = w % spanExpandCount, last = -1;
									for (int k = 0; k < hSpan; k++) {
										if (expandColumn[j - k]) {
											widths[last = j - k] += delta;
										}
									}
									if (last > -1) {
										widths[last] += remainder;
									}
								}
							}
						}
						if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
							w = !data.grabExcessHorizontalSpace || data.minimumWidth == -1 ? data.cacheWidth
									: data.minimumWidth;
							w += data.horizontalIndent - spanMinWidth - (hSpan - 1) * horizontalSpacing;
							if (w > 0) {
								if (spanExpandCount == 0) {
									minWidths[j] += w;
								} else {
									int delta = w / spanExpandCount;
									int remainder = w % spanExpandCount, last = -1;
									for (int k = 0; k < hSpan; k++) {
										if (expandColumn[j - k]) {
											minWidths[last = j - k] += delta;
										}
									}
									if (last > -1) {
										minWidths[last] += remainder;
									}
								}
							}
						}
					}
				}
			}
		}

		if (columnsEqualWidth) {
			int minColumnWidth = 0;
			int columnWidth = 0;
			for (int i = 0; i < cols; i++) {
				minColumnWidth = Math.max(minColumnWidth, minWidths[i]);
				columnWidth = Math.max(columnWidth, widths[i]);
			}
			columnWidth = width == -1 || expandCount == 0 ? columnWidth
					: Math.max(minColumnWidth, availableWidth / cols);
			for (int i = 0; i < cols; i++) {
				expandColumn[i] = expandCount > 0;
				widths[i] = columnWidth;
			}
		} else if (width != -1 && expandCount > 0) {
			int totalWidth = 0;
			for (int i = 0; i < cols; i++) {
				totalWidth += widths[i];
			}
			int c = expandCount;
			int delta = (availableWidth - totalWidth) / c;
			int remainder = (availableWidth - totalWidth) % c;
			int last = -1;
			while (totalWidth != availableWidth) {
				for (int j = 0; j < cols; j++) {
					if (expandColumn[j]) {
						if (widths[j] + delta > minWidths[j]) {
							widths[last = j] = widths[j] + delta;
						} else {
							widths[j] = minWidths[j];
							expandColumn[j] = false;
							c--;
						}
					}
				}
				if (last > -1) {
					widths[last] += remainder;
				}

				for (int j = 0; j < cols; j++) {
					for (int i = 0; i < rows; i++) {
						GridData data = getGridData(i, j);
						if (data != null) {
							int hSpan = Math.max(1, Math.min(data.horizontalSpan, cols));
							if (hSpan > 1) {
								if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
									int spanWidth = 0, spanExpandCount = 0;
									for (int k = 0; k < hSpan; k++) {
										spanWidth += widths[j - k];
										if (expandColumn[j - k]) {
											spanExpandCount++;
										}
									}
									int w = !data.grabExcessHorizontalSpace || data.minimumWidth == -1 ? data.cacheWidth
											: data.minimumWidth;
									w += data.horizontalIndent - spanWidth - (hSpan - 1) * horizontalSpacing;
									if (w > 0) {
										if (spanExpandCount == 0) {
											widths[j] += w;
										} else {
											int delta2 = w / spanExpandCount;
											int remainder2 = w % spanExpandCount, last2 = -1;
											for (int k = 0; k < hSpan; k++) {
												if (expandColumn[j - k]) {
													widths[last2 = j - k] += delta2;
												}
											}
											if (last2 > -1) {
												widths[last2] += remainder2;
											}
										}
									}
								}
							}
						}
					}
				}
				if (c == 0) {
					break;
				}
				totalWidth = 0;
				for (int i = 0; i < cols; i++) {
					totalWidth += widths[i];
				}
				delta = (availableWidth - totalWidth) / c;
				remainder = (availableWidth - totalWidth) % c;
				last = -1;
			}
		}
	}

	private void initGridDatas(boolean flushCache) {
		for (int i = 0; i < controlCount; i++) {
			UiComponent child = grid.get(i);
			if (child == null) {
				continue;
			}

			GridData data = null; // TODO (GridData) child.getLayoutData();
			if (flushCache) {
				data.flushCache();
			}

			data.computeSize(child, data.widthHint, data.heightHint, flushCache);
			if (data.grabExcessHorizontalSpace && data.minimumWidth > 0) {
				if (data.cacheWidth < data.minimumWidth) {
					int trim = 0; // TODO child.getBorderWidth() * 2;
					data.cacheWidth = data.cacheHeight = -1;
					data.computeSize(child, Math.max(0, data.minimumWidth - trim), data.heightHint, false);
				}
			}

			if (data.grabExcessVerticalSpace && data.minimumHeight > 0) {
				data.cacheHeight = Math.max(data.cacheHeight, data.minimumHeight);
			}
		}
	}

	private void prepareGrid(Composite composite) {
		grid.clear();
		cols = 0;
		rows = 0;
		controlCount = 0;
		ImmutableArray<UiComponent> components = composite.components();
		for (int i = 0, n = components.size(); i < n; i++) {
			UiComponent component = components.get(i);
			GridData gridData = null;// TODO
			if (isIncluded(gridData)) {
				controlCount++;
				cols = Math.max(cols, gridData.column + 1);
				rows = Math.max(rows, gridData.row + 1);
			}
		}

		if (controlCount < 1) {
			return;
		}

		grid.ensureCapacity(cols * rows);
		for (int i = 0, n = components.size(); i < n; i++) {
			UiComponent component = components.get(i);
			GridData gridData = null;// TODO
			if (isIncluded(gridData)) {
				int index = (gridData.row * cols) + gridData.column;
				grid.insert(index, component);
			}
		}
	}

	private boolean isIncluded(GridData gridData) {
		return gridData != null && !gridData.exclude && gridData.column >= 0 && gridData.row >= 0;
	}

	private UiComponent getComponent(int column, int row) {
		return grid.get(row * cols + column);
	}

	private GridData getGridData(int column, int row) {
		return null;// TODO
	}
}
