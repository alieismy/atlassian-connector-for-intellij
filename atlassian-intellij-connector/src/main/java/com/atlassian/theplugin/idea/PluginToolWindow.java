/**
 * Copyright (C) 2008 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.theplugin.idea;

import com.atlassian.theplugin.cfg.CfgUtil;
import com.atlassian.theplugin.commons.ServerType;
import com.atlassian.theplugin.commons.cfg.CfgManager;
import com.atlassian.theplugin.commons.exception.ThePluginException;
import com.atlassian.theplugin.idea.bamboo.BambooTableToolWindowPanel;
import com.atlassian.theplugin.idea.bamboo.BambooToolWindowPanel;
import com.atlassian.theplugin.idea.crucible.ReviewsToolWindowPanel;
import com.atlassian.theplugin.idea.jira.IssuesToolWindowPanel;
import com.atlassian.theplugin.util.PluginUtil;
import com.atlassian.theplugin.util.Util;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * @author Jacek Jaroczynski
 */
public class PluginToolWindow {

	private ArrayList<ToolWindowPanels> panels = new ArrayList<ToolWindowPanels>(INITIAL_NUMBER_OF_TABS);

	private ToolWindow ideaToolWindow;
	private Project project;
	private final ReviewsToolWindowPanel reviewsToolWindowPanel;
	private final IssuesToolWindowPanel issuesToolWindowPanel;
	private final BambooTableToolWindowPanel bambooTableToolWindowPanel;
	//private String selectedContent = null;
	// one space to have better horizontal separation between the header and the tabs
	public static final String TOOL_WINDOW_NAME = "Atlassian ";
	private static final int INITIAL_NUMBER_OF_TABS = 4;
	private static final String CONFIGURE_TAB_NAME = "Configure";
	public static final Icon ICON_CRUCIBLE = IconLoader.getIcon("/icons/crucible-16.png");
	private final CfgManager cfgManager;
	private final BambooToolWindowPanel bambooToolWindowPanel;


	public static void showHidePluginWindow(AnActionEvent event) {
		ToolWindow tw = IdeaHelper.getToolWindow(IdeaHelper.getCurrentProject(event.getDataContext()));
		if (tw != null) {
			if (tw.isVisible()) {
				tw.hide(new Runnable() {
					public void run() {
					}
				});
			} else {

				tw.show(new Runnable() {
					public void run() {
					}
				});
			}

		}

	}

	public PluginToolWindow(@NotNull Project project, @NotNull CfgManager cfgManager,
			@NotNull BambooToolWindowPanel bambooToolWindowPanel,
			@NotNull ReviewsToolWindowPanel reviewsToolWindowPanel,
			@NotNull IssuesToolWindowPanel issuesToolWindowPanel,
			@NotNull BambooTableToolWindowPanel bambooTableToolWindowPanel) {
		this.cfgManager = cfgManager;
		this.bambooToolWindowPanel = bambooToolWindowPanel;
		this.project = project;
		this.reviewsToolWindowPanel = reviewsToolWindowPanel;
		this.issuesToolWindowPanel = issuesToolWindowPanel;
		this.bambooTableToolWindowPanel = bambooTableToolWindowPanel;
	}


	public void register(@NotNull ToolWindowManager toolWindowManager) {
		this.ideaToolWindow = toolWindowManager.registerToolWindow(
				TOOL_WINDOW_NAME, false, ToolWindowAnchor.BOTTOM);
	}

	/**
	 * @return {@link ToolWindow} objects wraped up by this class
	 */
	public ToolWindow getIdeaToolWindow() {
		return ideaToolWindow;
	}


	/**
	 * Register type of panel. Register panel can be then shown/hidden using {@link #showHidePanels}
	 *
	 * @param toolWindowPanel ToolWindowPanels enum value
	 */
	public void registerPanel(ToolWindowPanels toolWindowPanel) {
		if (!panels.contains(toolWindowPanel)) {
			panels.add(toolWindowPanel);
		}
	}

//	public void registerBottomPanel(ToolWindowPanels toolWindowPanel) {
//		bottomPanels.add(toolWindowPanel);
//	}


	public void showHidePanels() {
		//stopTabChangeListener();

		final ContentManager contentManager = ideaToolWindow.getContentManager();
		if (cfgManager.getAllEnabledServers(CfgUtil.getProjectId(project)).size() == 0) {
			// no servers defined, show config panel
			if (contentManager.findContent(CONFIGURE_TAB_NAME) == null) {
				final Content content = contentManager.getFactory().createContent(
						new ToolWindowConfigPanel(project), CONFIGURE_TAB_NAME, false);
				content.setCloseable(false);
				ideaToolWindow.getContentManager().addContent(content);
			}
		} else {
			// servers defined, find config panel, hide config panel
			final Content content = contentManager.findContent(CONFIGURE_TAB_NAME);
			if (content != null) {
				ideaToolWindow.getContentManager().removeContent(content, true);
			}
		}

		//bottomIdeaToolWindow
		for (ToolWindowPanels entry : panels) {
			try {
				ServerType serverType = Util.toolWindowPanelsToServerType(entry);

				// servers are defined
				if (cfgManager.getAllEnabledServers(CfgUtil.getProjectId(project), serverType).isEmpty() == false) {
					// tab is not visible
					if (ideaToolWindow.getContentManager().findContent(entry.toString()) == null) {
						// show tab
						final Content content = createContent(entry);
						if (content != null) {
							ideaToolWindow.getContentManager().addContent(content);
						}

					}
					// servers are not defined
				} else {
					// tab is visible
					Content content = ideaToolWindow.getContentManager().findContent(entry.toString());
					if (content != null) {
						// hide tab
						ideaToolWindow.getContentManager().removeContent(content, true);
					}
				}
			} catch (ThePluginException e) {
				PluginUtil.getLogger().error(e.getMessage(), e);
			}
		}

		//startTabChangeListener();
	}

	/**
	 * Methods opens the ToolWindow and focuses on a particular component.
	 * If component does not exists it is created
	 *
	 * @param component
	 */
	public void focusPanel(ToolWindowPanels component) {
		if (ideaToolWindow != null) {
			final ContentManager contentManager = ideaToolWindow.getContentManager();
			ideaToolWindow.activate(null);
			Content content = contentManager.findContent(component.toString());

			if (content == null) {
				content = createContent(component);
				if (content != null) {
					contentManager.addContent(content);
				}
			}
			if (content != null) {
				contentManager.setSelectedContent(content);
			}
		}
	}

	/**
	 * Methods opens the ToolWindow and focuses on a particular component.
	 * If component does not exists it is not created and focused
	 *
	 */
	public void focusPanelIfExists(String tabName) {

		if (ideaToolWindow != null) {
			ContentManager contentManager = ideaToolWindow.getContentManager();
			Content content = contentManager.findContent(tabName);

			if (content != null) {
				contentManager.setSelectedContent(content);
			}
		}
	}

	/**
	 * Shows/hides panel if exists at least one server configured for this panel.
	 * If component does not exists it is not created and focused.
	 * If component is not focused than is focused.
	 * If component is focused then is closed/hidden
	 *
	 * @param component what to show/hide
	 */
	public void showHidePanelIfExists(ToolWindowPanels component) {
		if (ideaToolWindow != null) {


			try {
				ServerType serverType = Util.toolWindowPanelsToServerType(component);
				// servers are defined
				final CfgManager myCfgManager = IdeaHelper.getCfgManager();
				if (myCfgManager.getAllEnabledServers(CfgUtil.getProjectId(project), serverType).size() > 0) {
					// tab is not visible
					final ContentManager contentManager = ideaToolWindow.getContentManager();
					Content content = contentManager.findContent(component.toString());
					if (content == null) {
						content = createContent(component);
						if (content != null) {
							ideaToolWindow.getContentManager().addContent(content);
						}

					} else { //tab exists so close it, hide
						if (content.isSelected() && ideaToolWindow.isVisible()) {
							ideaToolWindow.getContentManager().removeContent(content, true);
						} else {
							ideaToolWindow.getContentManager().setSelectedContent(content);
						}
					}
					// servers are not defined
				} else {
					// tab is visible
					Content content = ideaToolWindow.getContentManager().findContent(component.toString());
					if (content != null) {
						// hide tab


						if (content.isSelected() && ideaToolWindow.isVisible()) {
							ideaToolWindow.getContentManager().removeContent(content, true);
						} else {
							ideaToolWindow.getContentManager().setSelectedContent(content);
						}
					}
				}
			} catch (ThePluginException e) {
				PluginUtil.getLogger().error(e.getMessage(), e);
			}

			ideaToolWindow.activate(null);

			focusPanelIfExists(component.toString());
		}
	}

	@Nullable
	private Content createContent(final ToolWindowPanels component) {
		switch (component) {
			case BAMBOO_OLD:
					return createBambooContent();
			case BUILDS_WOJTEK:
				return createBamboo2Content();
			case CRUCIBLE:
				return createCrucibleContentNew();
			case ISSUES:
				return createIssuesContent();
			default:
				return null;
		}
	}


	public Content createBamboo2Content() {
		final ContentManager contentManager = ideaToolWindow.getContentManager();
		final Content content = contentManager.getFactory().createContent(bambooToolWindowPanel,
				PluginToolWindow.ToolWindowPanels.BUILDS_WOJTEK.toString(), false);
		content.setIcon(IconLoader.getIcon("/icons/tab_bamboo.png"));
		content.putUserData(com.intellij.openapi.wm.ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
		return content;
	}


	public Content createBambooContent() {
		final ContentManager contentManager = ideaToolWindow.getContentManager();
		final Content content = contentManager.getFactory().createContent(bambooTableToolWindowPanel,
				PluginToolWindow.ToolWindowPanels.BAMBOO_OLD.toString(), false);
		content.setIcon(IconLoader.getIcon("/icons/tab_bamboo.png"));
		content.putUserData(com.intellij.openapi.wm.ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
		return content;
	}

	public Content createCrucibleContentNew() {
		final ContentManager contentManager = ideaToolWindow.getContentManager();
		final Content content = contentManager.getFactory().createContent(
				reviewsToolWindowPanel, PluginToolWindow.ToolWindowPanels.CRUCIBLE.toString(), false);
		content.setIcon(IconLoader.getIcon("/icons/tab_crucible.png"));
		content.putUserData(com.intellij.openapi.wm.ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
		return content;
	}

	public Content createIssuesContent() {
		final ContentManager contentManager = ideaToolWindow.getContentManager();
		final Content content = contentManager.getFactory().createContent(
				issuesToolWindowPanel, PluginToolWindow.ToolWindowPanels.ISSUES.toString(), false);
		content.setIcon(IconLoader.getIcon("/icons/tab_jira.png"));
		content.putUserData(com.intellij.openapi.wm.ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
		return content;
	}

	

	/**
	 * List of available panels in tool window
	 */
	public enum ToolWindowPanels {
		BAMBOO_OLD("Builds old"),
		BUILDS_WOJTEK("Builds"),
		CRUCIBLE("Reviews"),
		ISSUES("Issues");

		private final String title;

		ToolWindowPanels(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}
}