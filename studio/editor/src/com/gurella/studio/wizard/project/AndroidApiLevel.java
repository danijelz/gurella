package com.gurella.studio.wizard.project;

import com.gurella.studio.wizard.project.setup.SetupConstants;

class AndroidApiLevel implements Comparable<AndroidApiLevel> {
	int level;

	static AndroidApiLevel parse(String line) {
		String levelString = line.split("\\=")[1];
		int level = Integer.parseInt(levelString);
		return new AndroidApiLevel(level);
	}

	AndroidApiLevel(int level) {
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
		AndroidApiLevel other = (AndroidApiLevel) obj;
		return level == other.level;
	}

	@Override
	public String toString() {
		return String.valueOf(level);
	}

	@Override
	public int compareTo(AndroidApiLevel other) {
		return Integer.compare(level, other.level);
	}
}