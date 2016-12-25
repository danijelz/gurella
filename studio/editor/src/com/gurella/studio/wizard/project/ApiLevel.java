package com.gurella.studio.wizard.project;

import com.gurella.studio.wizard.project.setup.SetupConstants;

class ApiLevel implements Comparable<ApiLevel> {
	int level;

	static ApiLevel parse(String line) {
		String levelString = line.split("\\=")[1];
		int level = Integer.parseInt(levelString);
		return new ApiLevel(level);
	}

	ApiLevel(int level) {
		this.level = level;
	}

	public boolean isValid() {
		return level >= SetupConstants.androidApiLevel;
	}

	@Override
	public int hashCode() {
		return 31 + level;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApiLevel other = (ApiLevel) obj;
		return level == other.level;
	}

	@Override
	public String toString() {
		return String.valueOf(level);
	}

	@Override
	public int compareTo(ApiLevel other) {
		return Integer.compare(level, other.level);
	}
}