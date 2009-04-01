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
package com.atlassian.theplugin.jira.model;

import com.atlassian.theplugin.jira.api.JIRAIssue;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * User: pmaruszak
 */
public class ActiveJiraIssueBean implements ActiveJiraIssue {
	private DateTime lastStartTime = new DateTime();
	private long secondsSpent;
	private boolean active = false;
	private String serverId;
	private JIRAIssue issue;

	public ActiveJiraIssueBean() {
	}

	public ActiveJiraIssueBean(final String serverId, final JIRAIssue issue, DateTime lastStartTime, long secondsSpent) {
		this.serverId = serverId;
		this.issue = issue;
		this.lastStartTime = lastStartTime;
		this.secondsSpent = secondsSpent;
	}

	public ActiveJiraIssueBean(final String serverId, final JIRAIssue issue, DateTime lastStartTime) {
		this(serverId, issue, lastStartTime, 0);

	}

	public void resetTimeSpent() {
		secondsSpent = 0;
	}


	public long recalculateTimeSpent() {

		DateTime now = new DateTime();
		Period nextPeriod = new Period(lastStartTime, now, PeriodType.seconds());
		secondsSpent = secondsSpent + nextPeriod.getSeconds();
		lastStartTime = now;
		return secondsSpent;
	}

	public JIRAIssue getIssue() {
		return issue;
	}

	public long getSecondsSpent() {
		recalculateTimeSpent();
		return this.secondsSpent;
	}

	public void setSecondsSpent(final long secondsSpent) {
		this.secondsSpent = secondsSpent;
	}


	public void setActive(final boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(final String serverId) {
		this.serverId = serverId;
	}

	public String getIssueKey() {
		return issue.getKey();
	}
}
