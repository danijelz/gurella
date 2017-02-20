package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.ui.GridData.HorizontalAlignment;
import com.gurella.engine.utils.ImmutableArray;

public class GridLayout implements Layout {
	private static final int DEFAULT = -1;

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
		int[] widths = new int[cols];
		int[] minWidths = new int[cols];
		int[] heights = new int[rows];

		calculateColumnWidths(width, widths, minWidths);
		computeWrapping(width, widths);
		computeRowHeights(height, heights);
		positionControls(x, y, widths, heights);

		// TODO Auto-generated method stub
	}

	private void positionControls(int x, int y, int[] widths, int[] heights) {
		int gridY = y + marginTop;
		for (int i = 0; i < rows; i++) {
			int gridX = x + marginLeft;
			for (int j = 0; j < cols; j++) {
				GridData data = getGridData(i, j);
				if (data != null) {
					int hSpan = Math.max(1, Math.min(data.horizontalSpan, cols));
					int vSpan = Math.max(1, data.verticalSpan);
					int cellWidth = 0, cellHeight = 0;
					for (int k = 0; k < hSpan; k++) {
						cellWidth += widths[j + k];
					}
					for (int k = 0; k < vSpan; k++) {
						cellHeight += heights[i + k];
					}
					cellWidth += horizontalSpacing * (hSpan - 1);
					int childX = gridX + data.horizontalIndent;
					int childWidth = Math.min(data.cacheWidth, cellWidth);
					switch (data.horizontalAlignment) {
					case CENTER:
						childX += Math.max(0, (cellWidth - data.horizontalIndent - childWidth) / 2);
						break;
					case RIGHT:
						childX += Math.max(0, cellWidth - data.horizontalIndent - childWidth);
						break;
					case FILL:
						childWidth = cellWidth - data.horizontalIndent;
						break;
					}
					cellHeight += verticalSpacing * (vSpan - 1);
					int childY = gridY + data.verticalIndent;
					int childHeight = Math.min(data.cacheHeight, cellHeight);
					switch (data.verticalAlignment) {
					case CENTER:
						childY += Math.max(0, (cellHeight - data.verticalIndent - childHeight) / 2);
						break;
					case BOTTOM:
						childY += Math.max(0, cellHeight - data.verticalIndent - childHeight);
						break;
					case FILL:
						childHeight = cellHeight - data.verticalIndent;
						break;
					}
					UiComponent child = getComponent(i, j);
					if (child != null) {
						//TODO child.setBounds (childX, childY, childWidth, childHeight);
					}
				}
				gridX += widths[j] + horizontalSpacing;
			}
			gridY += heights[i] + verticalSpacing;
		}
	}

	private void computeRowHeights(int height, int[] heights) {
		int availableHeight = height - verticalSpacing * (rows - 1) - (marginTop + marginBottom);
		int expandCount = 0;

		int[] minHeights = new int[rows];
		boolean[] expandRow = new boolean[rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				GridData data = getGridData(i, j);
				if (data != null) {
					int vSpan = Math.max(1, Math.min(data.verticalSpan, rows));
					if (vSpan == 1) {
						int h = data.cacheHeight + data.verticalIndent;
						heights[i] = Math.max(heights[i], h);
						if (data.grabExcessVerticalSpace) {
							if (!expandRow[i]) {
								expandCount++;
							}
							expandRow[i] = true;
						}
						if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
							h = !data.grabExcessVerticalSpace || data.minimumHeight == DEFAULT ? data.cacheHeight
									: data.minimumHeight;
							h += data.verticalIndent;
							minHeights[i] = Math.max(minHeights[i], h);
						}
					}
				}
			}

			for (int j = 0; j < cols; j++) {
				GridData data = getGridData(i, j);
				if (data != null) {
					int vSpan = Math.max(1, Math.min(data.verticalSpan, rows));
					if (vSpan > 1) {
						int spanHeight = 0, spanMinHeight = 0, spanExpandCount = 0;
						for (int k = 0; k < vSpan; k++) {
							spanHeight += heights[i - k];
							spanMinHeight += minHeights[i - k];
							if (expandRow[i - k]) {
								spanExpandCount++;
							}
						}
						if (data.grabExcessVerticalSpace && spanExpandCount == 0) {
							expandCount++;
							expandRow[i] = true;
						}
						int h = data.cacheHeight + data.verticalIndent - spanHeight - (vSpan - 1) * verticalSpacing;
						if (h > 0) {
							if (spanExpandCount == 0) {
								heights[i] += h;
							} else {
								int delta = h / spanExpandCount;
								int remainder = h % spanExpandCount, last = -1;
								for (int k = 0; k < vSpan; k++) {
									if (expandRow[i - k]) {
										heights[last = i - k] += delta;
									}
								}
								if (last > -1) {
									heights[last] += remainder;
								}
							}
						}
						if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
							h = !data.grabExcessVerticalSpace || data.minimumHeight == DEFAULT ? data.cacheHeight
									: data.minimumHeight;
							h += data.verticalIndent - spanMinHeight - (vSpan - 1) * verticalSpacing;
							if (h > 0) {
								if (spanExpandCount == 0) {
									minHeights[i] += h;
								} else {
									int delta = h / spanExpandCount;
									int remainder = h % spanExpandCount, last = -1;
									for (int k = 0; k < vSpan; k++) {
										if (expandRow[i - k]) {
											minHeights[last = i - k] += delta;
										}
									}
									if (last > -1) {
										minHeights[last] += remainder;
									}
								}
							}
						}
					}
				}
			}
		}

		if (height != DEFAULT && expandCount > 0) {
			int totalHeight = 0;
			for (int i = 0; i < rows; i++) {
				totalHeight += heights[i];
			}
			int c = expandCount;
			int delta = (availableHeight - totalHeight) / c;
			int remainder = (availableHeight - totalHeight) % c;
			int last = -1;
			while (totalHeight != availableHeight) {
				for (int i = 0; i < rows; i++) {
					if (expandRow[i]) {
						if (heights[i] + delta > minHeights[i]) {
							heights[last = i] = heights[i] + delta;
						} else {
							heights[i] = minHeights[i];
							expandRow[i] = false;
							c--;
						}
					}
				}

				if (last > -1) {
					heights[last] += remainder;
				}

				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						GridData data = getGridData(i, j);
						if (data != null) {
							int vSpan = Math.max(1, Math.min(data.verticalSpan, rows));
							if (vSpan > 1) {
								if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
									int spanHeight = 0, spanExpandCount = 0;
									for (int k = 0; k < vSpan; k++) {
										spanHeight += heights[i - k];
										if (expandRow[i - k]) {
											spanExpandCount++;
										}
									}
									int h = !data.grabExcessVerticalSpace || data.minimumHeight == DEFAULT
											? data.cacheHeight : data.minimumHeight;
									h += data.verticalIndent - spanHeight - (vSpan - 1) * verticalSpacing;
									if (h > 0) {
										if (spanExpandCount == 0) {
											heights[i] += h;
										} else {
											int delta2 = h / spanExpandCount;
											int remainder2 = h % spanExpandCount, last2 = -1;
											for (int k = 0; k < vSpan; k++) {
												if (expandRow[i - k]) {
													heights[last2 = i - k] += delta2;
												}
											}
											if (last2 > -1) {
												heights[last2] += remainder2;
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
				totalHeight = 0;
				for (int i = 0; i < rows; i++) {
					totalHeight += heights[i];
				}
				delta = (availableHeight - totalHeight) / c;
				remainder = (availableHeight - totalHeight) % c;
				last = -1;
			}
		}
	}

	private void computeWrapping(int width, int[] widths) {
		GridData[] flush = null;
		int flushLength = 0;
		if (width != DEFAULT) {
			for (int j = 0; j < cols; j++) {
				for (int i = 0; i < rows; i++) {
					GridData data = getGridData(i, j);
					if (data != null) {
						if (data.heightHint == DEFAULT) {
							UiComponent child = getComponent(i, j);
							//TEMPORARY CODE
							int hSpan = Math.max(1, Math.min(data.horizontalSpan, cols));
							int currentWidth = 0;
							for (int k = 0; k < hSpan; k++) {
								currentWidth += widths[j - k];
							}
							currentWidth += (hSpan - 1) * horizontalSpacing - data.horizontalIndent;
							if ((currentWidth != data.cacheWidth
									&& data.horizontalAlignment == HorizontalAlignment.FILL)
									|| (data.cacheWidth > currentWidth)) {
								int trim = 0;//TODO child.getBorderWidth() * 2;
								data.cacheWidth = data.cacheHeight = DEFAULT;
								data.computeSize(child, Math.max(0, currentWidth - trim), data.heightHint, false);
								if (data.grabExcessVerticalSpace && data.minimumHeight > 0) {
									data.cacheHeight = Math.max(data.cacheHeight, data.minimumHeight);
								}
								if (flush == null) {
									flush = new GridData[controlCount];
								}
								flush[flushLength++] = data;
							}
						}
					}
				}
			}
		}
	}

	private void calculateColumnWidths(int width, int[] widths, int[] minWidths) {
		int availableWidth = width - horizontalSpacing * (cols - 1) - (marginLeft + marginRight);
		int expandCount = 0;
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
