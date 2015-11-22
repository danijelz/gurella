package com.gurella.studio.project;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gurella.studio.assets.AssetsContainer;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

public class ProjectFooterContainer extends VisTable {
	private TabbedPane tabbedPane = new TabbedPane();
	private AssetsContainer assetsContainer = new AssetsContainer();
	private ConsoleContainer consoleContainer = new ConsoleContainer();
	private Actor spacer = new Actor();

	public ProjectFooterContainer() {
		setBackground("border");
		tabbedPane.add(new SimpleTab("Assets", assetsContainer));
		tabbedPane.add(new SimpleTab("Console", consoleContainer));

		add(tabbedPane.getTable()).top().left().expandX().height(30);
		row();
		add(spacer).top().left().fill().expand();

		tabbedPane.switchTab(0);
	}

	private class SimpleTab extends Tab {
		private String title;
		private Table content;

		public SimpleTab(String title, Table content) {
			super(false, false);
			this.title = title;
			this.content = content;
		}

		@Override
		public String getTabTitle() {
			return title;
		}

		@Override
		public Table getContentTable() {
			return content;
		}

		@Override
		public void onHide() {
			super.onHide();
			clearChildren();
			add(tabbedPane.getTable()).top().left().expandX().height(30);
			row();
			add(spacer).top().left().fill().expand();
		}

		@Override
		public void onShow() {
			super.onShow();
			clearChildren();
			add(tabbedPane.getTable()).top().left().expandX().height(30);
			row();
			add(content).top().left().fill().expand();
		}
	}
}
