package com.gurella.studio.wizard.setup;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.gurella.studio.wizard.setup.DependencyBank.ProjectDependency;
import com.gurella.studio.wizard.setup.DependencyBank.ProjectType;

public class Dependency {
	private HashMap<ProjectType, String[]> subDependencies = new HashMap<ProjectType, String[]>();
	private String[] gwtInherits;
	private String name;

	public Dependency(ProjectDependency dependency) {
		this.name = dependency.name();
		this.gwtInherits = dependency.getGwtInherits();
		Stream.of(ProjectType.values()).forEach(p -> subDependencies.put(p, dependency.getDependencies(p)));
	}

	public String[] getDependencies(ProjectType type) {
		return subDependencies.get(type);
	}

	public List<String> getIncompatibilities(ProjectType type) {
		if (subDependencies.get(type) == null) {
			String typeName = type.getName().toUpperCase();
			return Collections.singletonList("Dependency " + name + " is not compatible with sub module " + typeName);
		} else {
			return Collections.emptyList();
		}
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
