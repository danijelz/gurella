package com.gurella.engine.graphics.vector.svg.property.value;

import com.badlogic.gdx.utils.ObjectMap;

public enum Display {
	inline,
	block,
	listItem("list-item"),
	runIn("run-in"),
	compact,
	marker,
	table,
	inlineTable("inline-table"),
	tableRowGroup("table-row-group"),
	tableHeaderGroup("table-header-group"),
	tableFooterGroup("table-footer-group"),
	tableRow("table-row"),
	tableColumnGroup("table-column-group"),
	tableColumn("table-column"),
	tableCell("table-cell"),
	tableCaption("table-caption"),
	none;

	private static final ObjectMap<String, Display> valuesByName = new ObjectMap<String, Display>();
	static {
		Display[] values = values();
		for (int i = 0; i < values.length; i++) {
			Display value = values[i];
			valuesByName.put(value.displayName, value);
		}
	}

	public final String displayName;

	private Display(String displayName) {
		this.displayName = displayName;
	}

	private Display() {
		this.displayName = name();
	}
	
	public static Display getValueByDisplayName(String displayName) {
		return valuesByName.get(displayName);
	}
}
