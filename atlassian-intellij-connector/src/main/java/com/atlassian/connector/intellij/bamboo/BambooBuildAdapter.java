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

package com.atlassian.connector.intellij.bamboo;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import com.atlassian.theplugin.commons.bamboo.BambooBuild;
import com.atlassian.theplugin.commons.bamboo.BambooServerData;
import com.atlassian.theplugin.commons.bamboo.BuildStatus;
import com.atlassian.theplugin.commons.cfg.ConfigurationListenerAdapter;
import com.atlassian.theplugin.commons.remoteapi.ServerData;

/**
 * @author Jacek Jaroczynski
 */
public class BambooBuildAdapter extends ConfigurationListenerAdapter {
	protected final BambooBuild build;
	public static final SimpleDateFormat BAMBOO_BUILD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final BambooServerData serverData;

	public BambooBuildAdapter(BambooBuild build, BambooServerData serverData) {
		this.build = build;
		this.serverData = serverData;
	}

	public BambooServerData getServer() {
		return serverData;
	}

	public String getServerName() {
		final ServerData server = getServer();
		if (server != null) {
			return server.getName() == null ? "" : server.getName();
		} else {
			return "";
		}
	}

	public boolean isBamboo2() {
//		final BambooServerCfg server = build.getServer();
//		return server != null && server.isBamboo2();
		//todo: implement
		return true;
	}

	public Collection<String> getCommiters() {
		return build.getCommiters();
	}

	public String getProjectName() {
		return build.getProjectName() == null ? "" : build.getProjectName();
	}

	public String getBuildUrl() {
		return build.getBuildUrl() == null ? "" : build.getBuildUrl();
	}

	public String getPlanName() {
		return build.getPlanName() == null ? "" : build.getPlanName();
	}

	public String getPlanKey() {
		return build.getPlanKey() == null ? "" : build.getPlanKey();
	}

	public boolean isEnabled() {
		return build.getEnabled();
	}

	public int getNumber() throws UnsupportedOperationException {
		return build.getNumber();
	}

	public boolean isValid() {
		return build.isValid();
	}

	/**
	 * @return build number as string (base 10) or empty string when this object does not represent successfully fetched build
	 */
	public String getBuildNumberAsString() {
		return build.isValid() ? Integer.toString(build.getNumber()) : "";
	}

	public String getResultUrl() {
		return build.getResultUrl() == null ? "" : build.getResultUrl();
	}

	public BuildStatus getStatus() {
		return build.getStatus();
	}

	public int getTestsPassed() {
		return build.getTestsPassed();
	}

	public int getTestsFailed() {
		return build.getTestsFailed();
	}

	public int getTestsNumber() {
		return build.getTestsPassed() + build.getTestsFailed();
	}

	public String getTestsPassedSummary() {
		if (getStatus() == BuildStatus.UNKNOWN) {
			return "-/-";
		} else {
			return getTestsFailed() + "/" + getTestsNumber();
		}
	}

	public Date getCompletionDate() {
		return build.getCompletionDate();
	}

	public Date getPollingTime() {
		return build.getPollingTime();
	}

	public String getReason() {
		return build.getReason() == null ? "" : build.getReason();
	}

	public BambooBuild getBuild() {
		return build;
	}

	public boolean isMyBuild() {
		return build.isMyBuild();
	}

	@Override
	public void serverDataChanged(final ServerData serverData) {
		// todo PL-1536 set new server for build (but build is immutable for some reason)

	}
}