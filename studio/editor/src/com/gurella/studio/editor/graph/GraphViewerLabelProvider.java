package com.gurella.studio.editor.graph;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.GurellaStudioPlugin;

class GraphViewerLabelProvider extends BaseLabelProvider implements ILabelProvider {
	@Override
	public Image getImage(Object element) {
		if (element instanceof SceneNode) {
			return GurellaStudioPlugin.getImage("icons/ice_cube.png");
		} else if (element instanceof SceneNodeComponent) {
			if (element instanceof TransformComponent) {
				return GurellaStudioPlugin.getImage("icons/transform.png");
			} else {
				return GurellaStudioPlugin.getImage("icons/16-cube-green_16x16.png");
			}
		} else {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof SceneNode) {
			String name = ((SceneNode) element).getName();
			return name == null ? "" : name;
		} else if (element instanceof SceneNodeComponent) {
			return MetaTypes.getMetaType(element).getName();
		} else {
			return "";
		}
	}
}
