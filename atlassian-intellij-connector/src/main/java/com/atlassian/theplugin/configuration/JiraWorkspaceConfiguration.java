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

package com.atlassian.theplugin.configuration;

import com.atlassian.connector.commons.jira.beans.JIRAProject;
import com.atlassian.connector.commons.jira.beans.JIRAProjectBean;
import com.atlassian.theplugin.commons.cfg.ServerId;
import com.atlassian.theplugin.commons.cfg.ServerIdImpl;
import com.atlassian.theplugin.commons.jira.JiraServerData;
import com.atlassian.theplugin.commons.jira.api.JiraIssueAdapter;
import com.atlassian.theplugin.idea.jira.RemainingEstimateUpdateMode;
import com.atlassian.theplugin.jira.model.ActiveJiraIssueBean;
import com.atlassian.theplugin.jira.model.JiraPresetFilter;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@State(name = "atlassian-ide-plugin-workspace-issues",
		storages = {@Storage(id = "atlassian-ide-plugin-workspace-issues-id", file = "$WORKSPACE_FILE$")})
public class JiraWorkspaceConfiguration implements PersistentStateComponent<JiraWorkspaceConfiguration> {
    public static final String RECENTLY_OPEN_FILTER_ID = "recentlyOpenFilterId";

	private Map<ServerIdImpl, JiraCustomFilterMap> filtersMap = new HashMap<ServerIdImpl, JiraCustomFilterMap>();
    private Map<ServerIdImpl, JiraPresetFilterMap> presetFiltersMap = new HashMap<ServerIdImpl, JiraPresetFilterMap>();
	private JiraViewConfigurationBean view = new JiraViewConfigurationBean();
	private LinkedList<IssueRecentlyOpenBean> recentlyOpenIssuess = new LinkedList<IssueRecentlyOpenBean>();
	public static final int RECENLTY_OPEN_ISSUES_LIMIT = 10;
	private ActiveJiraIssueBean activeJiraIssuee;
	private long selectedWorkflowAction;
	private boolean activeIssueProgressWorkflowAction;
	private boolean activeIssueLogWork;
	private boolean activeIssueCommitChanges;
	private int activeIssueAfterCommit;
	private boolean logWorkOnCommit;
	private RemainingEstimateUpdateMode remainingEstimateUpdateMode;
    private CheckinHandler checkinHandler;

    public JiraWorkspaceConfiguration() {
	}

	public void copyConfiguration(JiraWorkspaceConfiguration that) {
		this.filtersMap = that.filtersMap;
		this.view = that.view;
		this.recentlyOpenIssuess = that.recentlyOpenIssuess;
		this.activeJiraIssuee = that.activeJiraIssuee;
		this.selectedWorkflowAction = that.selectedWorkflowAction;
		this.activeIssueProgressWorkflowAction = that.activeIssueProgressWorkflowAction;
		this.activeIssueLogWork = that.activeIssueLogWork;
		this.activeIssueCommitChanges = that.activeIssueCommitChanges;
		this.activeIssueAfterCommit = that.activeIssueAfterCommit;
		this.logWorkOnCommit = that.logWorkOnCommit;
		this.remainingEstimateUpdateMode = that.remainingEstimateUpdateMode;
        this.presetFiltersMap = that.presetFiltersMap;
        this.checkinHandler = that.checkinHandler;
	}

	public Map<ServerIdImpl, JiraCustomFilterMap> getFiltersMap() {
		return filtersMap;
	}



	public void setFiltersMap(final Map<ServerIdImpl, JiraCustomFilterMap> filtersMap) {
		this.filtersMap = filtersMap;
	}

	public JiraViewConfigurationBean getView() {
		return view;
	}

	public void setView(final JiraViewConfigurationBean view) {
		this.view = view;
	}

	public LinkedList<IssueRecentlyOpenBean> getRecentlyOpenIssuess() {
		return recentlyOpenIssuess;
	}

	public void setRecentlyOpenIssuess(final LinkedList<IssueRecentlyOpenBean> recentlyOpenIssuess) {
		this.recentlyOpenIssuess = recentlyOpenIssuess;
	}


    public Map<ServerIdImpl, JiraPresetFilterMap> getPresetFiltersMap() {
        return presetFiltersMap;
    }

    public void setPresetFiltersMap(Map<ServerIdImpl, JiraPresetFilterMap> presetFiltersMap) {
        this.presetFiltersMap = presetFiltersMap;
    }

    public void addRecentlyOpenIssue(final JiraIssueAdapter issue) {
		if (recentlyOpenIssuess == null) {
			recentlyOpenIssuess = new LinkedList<IssueRecentlyOpenBean>();
		}

		if (issue != null) {
			String issueKey = issue.getKey();
			ServerId serverId = issue.getJiraServerData().getServerId();

			// add element and make sure it is not duplicated and it is insterted at the top
			IssueRecentlyOpenBean r = new IssueRecentlyOpenBean(serverId, issueKey, issue.getIssueUrl());

			if (recentlyOpenIssuess != null) {
				recentlyOpenIssuess.remove(r);
				recentlyOpenIssuess.addFirst(r);

				while (recentlyOpenIssuess.size() > RECENLTY_OPEN_ISSUES_LIMIT) {
					recentlyOpenIssuess.removeLast();
				}
			}
		}
	}

	public long getSelectedWorkflowAction() {
		return selectedWorkflowAction;
	}

	public void setSelectedWorkflowAction(long selectedWorkflowAction) {
		this.selectedWorkflowAction = selectedWorkflowAction;
	}

	public boolean isActiveIssueProgressWorkflowAction() {
		return activeIssueProgressWorkflowAction;
	}

	public void setActiveIssueProgressWorkflowAction(boolean activeIssueProgressWorkflowAction) {
		this.activeIssueProgressWorkflowAction = activeIssueProgressWorkflowAction;
	}

	public boolean isActiveIssueLogWork() {
		return activeIssueLogWork;
	}

	public void setActiveIssueLogWork(boolean activeIssueLogWork) {
		this.activeIssueLogWork = activeIssueLogWork;
	}

	public boolean isActiveIssueCommitChanges() {
		return activeIssueCommitChanges;
	}

	public void setActiveIssueCommitChanges(boolean activeIssueCommitChanges) {
		this.activeIssueCommitChanges = activeIssueCommitChanges;
	}

	public int getActiveIssueAfterCommit() {
		return activeIssueAfterCommit;
	}

	public void setActiveIssueAfterCommit(int activeIssueAfterCommit) {
		this.activeIssueAfterCommit = activeIssueAfterCommit;
	}

	public boolean isLogWorkOnCommit() {
		return logWorkOnCommit;
	}

	public void setLogWorkOnCommit(boolean logWorkOnCommit) {
		this.logWorkOnCommit = logWorkOnCommit;
	}

	public RemainingEstimateUpdateMode getRemainingEstimateUpdateMode() {
		return remainingEstimateUpdateMode != null ? remainingEstimateUpdateMode : RemainingEstimateUpdateMode.AUTO;
	}

	public void setRemainingEstimateUpdateMode(RemainingEstimateUpdateMode remainingEstimateUpdateMode) {
		this.remainingEstimateUpdateMode = remainingEstimateUpdateMode;
	}

	@Transient
	public JiraCustomFilterMap getJiraFilterConfiguaration(ServerId id) {
		JiraCustomFilterMap filterMap = filtersMap.get((ServerIdImpl) id);
		if (filterMap == null) {
			filterMap = new JiraCustomFilterMap();
			filtersMap.put((ServerIdImpl) id, filterMap);
		}
		return filterMap;
	}

    @Transient
    @Nullable
    public JIRAProject getPresetFilterProject(JiraServerData jiraServerData, JiraPresetFilter presetFilter) {
        final JiraPresetFilterMap map = presetFiltersMap.get((ServerIdImpl) jiraServerData.getServerId());
        if (map != null) {
            return map.getProject(presetFilter);
        }

        return null;
    }

    @Transient
    public void setPresetFilterProject(JiraServerData jiraServerData, JiraPresetFilter presetFilter,
                                       JIRAProjectBean jiraProject) {
       JiraPresetFilterMap map = presetFiltersMap.get((ServerIdImpl) jiraServerData.getServerId());
        if (map == null) {
            map = new JiraPresetFilterMap();
            presetFiltersMap.put((ServerIdImpl) jiraServerData.getServerId(), map);
        }

        map.setPresetFilter(presetFilter, jiraProject);
    }
//	@Transient
//	public void setFilterConfigurationBean(String serverId, JiraFilterConfigurationBean filterConfiguration) {
//		filtersMap.put(serverId, filterConfiguration);
//	}


    @Transient
    public CheckinHandler getCheckinHandler() {
        return checkinHandler;
    }

    @Transient
    public void setCheckinHandler(CheckinHandler checkinHandler) {
        this.checkinHandler = checkinHandler;
    }

    public JiraWorkspaceConfiguration getState() {
		return this;
	}

	public void loadState(final JiraWorkspaceConfiguration jiraProjectConfiguration) {
		copyConfiguration(jiraProjectConfiguration);
	}


	public ActiveJiraIssueBean getActiveJiraIssuee() {
		return activeJiraIssuee;
	}

	public void setActiveJiraIssuee(final ActiveJiraIssueBean activeJiraIssuee) {
		this.activeJiraIssuee = activeJiraIssuee;
	}

    @Transient
    public void clearPresetFilterProject(JiraServerData jiraServerData, JiraPresetFilter presetFilter) {
              JiraPresetFilterMap map = presetFiltersMap.get(jiraServerData.getServerId());
        if (map != null) {
             map.clearPresetFilter(presetFilter);
        }


    }
}
