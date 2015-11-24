package com.gurella.engine.graphics.vector.svg.property.parser;

import com.gurella.engine.graphics.vector.svg.property.PropertyParser;
import com.gurella.engine.graphics.vector.svg.property.value.Unit;

public class UnitParser implements PropertyParser<Unit> {
	public static final UnitParser instance = new UnitParser();

	private UnitParser() {
	}

	@Override
	public Unit parse(String strValue) {
		if (strValue.matches(".*[e|E][m|M]")) {
			return Unit.em;
		} else if (strValue.matches(".*[e|E][x|X]")) {
			return Unit.ex;
		} else if (strValue.matches(".*[p|P][x|X]")) {
			return Unit.px;
		} else if (strValue.matches(".*[c|C][m|M]")) {
			return Unit.cm;
		} else if (strValue.matches(".*[m|M][m|M]")) {
			return Unit.mm;
		} else if (strValue.matches(".*[i|I][n|N]")) {
			return Unit.in;
		} else if (strValue.matches(".*[p|P][t|T]")) {
			return Unit.pt;
		} else if (strValue.matches(".*[p|P][c|C]")) {
			return Unit.pc;
		} else if (strValue.indexOf("%") != -1) {
			return Unit.percent;
		} else {
			return Unit.unknown;
		}
	}
}
