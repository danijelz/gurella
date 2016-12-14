package com.gurella.studio.wizard.setup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;

public class Dependency {
	private HashMap<ProjectType, String[]> subDependencyMap = new HashMap<ProjectType, String[]>();
	private String[] gwtInherits;
	private String name;

	public Dependency(String name, String[] gwtInherits, String[]... subDependencies) {
		this.name = name;
		this.gwtInherits = gwtInherits;
		for (ProjectType type : ProjectType.values()) {
			subDependencyMap.put(type, subDependencies[type.ordinal()]);
		}
	}

	public String[] getDependencies(ProjectType type) {
		return subDependencyMap.get(type);
	}

	public List<String> getIncompatibilities(ProjectType type) {
		return subDependencyMap.get(type) == null
				? Collections.singletonList(
						"Dependency " + name + " is not compatible with sub module " + type.getName().toUpperCase())
				: Collections.emptyList();
	}

	public String[] getGwtInherits() {
		return gwtInherits;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return Optional.ofNullable(obj).filter(Dependency.class::isInstance).map(Dependency.class::cast)
				.filter(d -> d.getName().equals(getName())).isPresent();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
