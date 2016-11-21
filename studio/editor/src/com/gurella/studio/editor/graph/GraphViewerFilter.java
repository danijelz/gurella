package com.gurella.studio.editor.graph;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.LITERAL;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.Values;

class GraphViewerFilter extends ViewerFilter {
	private String filter;
	private final Map<SceneNode2, Boolean> nodes = new IdentityHashMap<>();
	private final Map<SceneNode2, Boolean> names = new IdentityHashMap<>();
	private Matcher matcher;

	void setFilter(String filter) {
		this.filter = filter;
		nodes.clear();
		names.clear();
		matcher = null;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (Values.isBlank(filter)) {
			return true;
		}

		if (element instanceof SceneNodeComponent2) {
			return nodeNameMatchesFilter((SceneNode2) parentElement);
		} else if (element instanceof SceneNode2) {
			return nodes.computeIfAbsent((SceneNode2) element, this::isNodeEnabled).booleanValue();
		} else {
			return false;
		}
	}

	private Boolean isNodeEnabled(SceneNode2 node) {
		if (nodeNameMatchesFilter(node)) {
			return Boolean.TRUE;
		}

		return Boolean.valueOf(Arrays.stream(node.childNodes.<SceneNode2> toArray(SceneNode2.class))
				.map(n -> nodes.computeIfAbsent(n, this::isNodeEnabled)).filter(e -> e == Boolean.TRUE).findAny()
				.isPresent());
	}

	private boolean nodeNameMatchesFilter(SceneNode2 node) {
		String name = node.getName();
		return names.computeIfAbsent(node, n -> Boolean.valueOf(Values.isNotBlank(name) && getMatcher(name).find()))
				.booleanValue();
	}

	private Matcher getMatcher(String name) {
		if (matcher == null) {
			matcher = Pattern.compile(filter, CASE_INSENSITIVE | LITERAL).matcher(name);
		} else {
			matcher.reset(name);
		}
		return matcher;
	}
}
