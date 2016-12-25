package com.gurella.studio.wizard.project;

import java.util.Comparator;

import com.gurella.studio.wizard.project.setup.SetupConstants;

class BuildToolsVersion implements Comparable<BuildToolsVersion> {
	int maj;
	int mid;
	int min;

	static BuildToolsVersion parse(String line) {
		String versionString = line.split("\\=")[1];
		String[] versionComponents = versionString.split("\\.");
		int[] version = new int[3];

		for (int i = 0; i < 3; i++) {
			version[i] = versionComponents.length < i - 1 ? 0 : Integer.parseInt(versionComponents[i]);
		}
		return new BuildToolsVersion(version);
	}

	BuildToolsVersion(int[] version) {
		this.maj = version[0];
		this.mid = version[1];
		this.min = version[2];
	}

	public int getMaj() {
		return maj;
	}

	public int getMid() {
		return mid;
	}

	public int getMin() {
		return min;
	}

	public boolean isValid() {
		int[] androidbuildtoolsversion = SetupConstants.androidBuildToolsVersion;
		int maj = androidbuildtoolsversion[0];
		if (this.maj > maj) {
			return true;
		} else if (this.maj < maj) {
			return false;
		}

		int mid = androidbuildtoolsversion[1];
		if (this.mid > mid) {
			return true;
		} else if (this.mid < mid) {
			return false;
		}

		return this.min >= androidbuildtoolsversion[2];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maj;
		result = prime * result + mid;
		result = prime * result + min;
		return result;
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
		BuildToolsVersion other = (BuildToolsVersion) obj;
		if (maj != other.maj) {
			return false;
		}
		if (mid != other.mid) {
			return false;
		}
		if (min != other.min) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(maj);
		builder.append('.');
		builder.append(mid);
		builder.append('.');
		builder.append(min);
		return builder.toString();
	}

	@Override
	public int compareTo(BuildToolsVersion other) {
		return Comparator.comparingInt(BuildToolsVersion::getMaj).thenComparingInt(BuildToolsVersion::getMid)
				.thenComparingInt(BuildToolsVersion::getMin).compare(this, other);
	}
}