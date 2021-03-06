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
package com.atlassian.theplugin.idea.bamboo.tree;

import com.atlassian.connector.cfg.ProjectCfgManager;
import com.atlassian.connector.intellij.bamboo.BambooBuildAdapter;
import com.atlassian.theplugin.commons.bamboo.BambooServerData;
import com.atlassian.theplugin.commons.cfg.ServerId;
import com.atlassian.theplugin.commons.remoteapi.ServerData;
import com.atlassian.theplugin.idea.bamboo.BuildListModel;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/**
 * @author Jacek Jaroczynski
 */
public class ServerBuildNodeManipulator extends BuildNodeManipulator {
    private final ProjectCfgManager projectCfgManager;


    public ServerBuildNodeManipulator(ProjectCfgManager projectCfgManager, final BuildListModel buildModel,
                                      final DefaultMutableTreeNode root) {
		super(buildModel, root);
        this.projectCfgManager = projectCfgManager;
    }

	@Override
	public int getChildCount(Object parent) {
		if (parent == rootNode) {
			return getDistinctServers().size();
		} else if (parent instanceof BuildServerTreeNode) {
			BuildServerTreeNode serverNode = (BuildServerTreeNode) parent;
			return gentNumOfBuildsForServer(serverNode.getServer().getServerId());
		}

		return 0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == rootNode) {

			DefaultMutableTreeNode p = (DefaultMutableTreeNode) parent;

			if (index < p.getChildCount()) {
				return p.getChildAt(index);
			}

			BambooServerData bambooServer = getDistinctServers().get(index);

			BuildServerTreeNode serverNode = new BuildServerTreeNode(projectCfgManager, bambooServer);
			p.add(serverNode);

			return serverNode;

		} else if (parent instanceof BuildServerTreeNode) {
			BuildServerTreeNode p = (BuildServerTreeNode) parent;

			if (index < p.getChildCount()) {
				return p.getChildAt(index);
			}

			BambooBuildAdapter build = getBuildForServer(p.getServer().getServerId(), index);
			BuildTreeNode node = new BuildTreeNode(buildModel, build);
			p.add(node);

			return node;
		}

		return null;
	}

	private List<BambooServerData> getDistinctServers() {
		Set<BambooServerData> servers = new TreeSet<BambooServerData>(COMPARATOR);

		for (BambooBuildAdapter build : buildModel.getBuilds()) {
			servers.add(build.getServer());
		}

		return new ArrayList<BambooServerData>(servers);
	}

	private static final Comparator<ServerData> COMPARATOR = new Comparator<ServerData>() {
		public int compare(ServerData lhs, ServerData rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	};

	private int gentNumOfBuildsForServer(ServerId serverId) {
		int ret = 0;
		for (BambooBuildAdapter build : buildModel.getBuilds()) {
			if (build.getServer().getServerId().equals(serverId)) {
				++ret;
			}
		}

		return ret;
	}

	private BambooBuildAdapter getBuildForServer(ServerId serverId, int index) {
		List<BambooBuildAdapter> array = new ArrayList<BambooBuildAdapter>();

		// get all builds for server
		for (BambooBuildAdapter build : buildModel.getBuilds()) {
			if (build.getServer().getServerId().equals(serverId)) {
				array.add(build);
			}
		}

		return array.get(index);
	}
}
