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
package com.atlassian.theplugin.idea.crucible;

import com.atlassian.theplugin.commons.cfg.CfgManager;
import com.atlassian.theplugin.commons.cfg.CrucibleServerCfg;
import com.atlassian.theplugin.commons.crucible.CrucibleServerFacade;
import com.atlassian.theplugin.commons.crucible.api.model.Review;
import com.atlassian.theplugin.commons.exception.ServerPasswordNotProvidedException;
import com.atlassian.theplugin.commons.remoteapi.RemoteApiException;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DataSink;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CrucibleCreatePreCommitUploadReviewForm extends AbstractCrucibleCreatePreCommitReviewForm
		implements DataProvider {
	public static final String LOCAL_CHANGES_BROWSER = "theplugin.crucible.localchangesbrowser";
	public static final DataKey<CrucibleCreatePostCommitReviewForm> COMMITTED_CHANGES_BROWSER_KEY = DataKey
			.create(LOCAL_CHANGES_BROWSER);

	private final Collection<Change> changes;

	public CrucibleCreatePreCommitUploadReviewForm(final Project project, final CrucibleServerFacade crucibleServerFacade,
			Collection<Change> changes,
			@NotNull final CfgManager cfgManager) {
		super(project, crucibleServerFacade, "", cfgManager);
		this.changes = changes;

		setTitle("Create Review");
		pack();
	}

	@Override
	protected Review createReview(final CrucibleServerCfg server, final ReviewProvider reviewProvider)
			throws RemoteApiException, ServerPasswordNotProvidedException {
		return createReviewImpl(server, reviewProvider, changes);
	}

/*
	@Override
	protected Review createReview(CrucibleServerCfg server, ReviewProvider reviewProvider) throws RemoteApiException,
			ServerPasswordNotProvidedException {
		return crucibleServerFacade.createReviewFromUpload(server, reviewProvider, uploadItems);
	}
	
	 */


	public Object getData(@NonNls final String dataId) {
		if (dataId.equals(LOCAL_CHANGES_BROWSER)) {
			return this;
		}
		return null;
	}

	private class MyDataSink implements DataSink {
		public ChangeList[] getChanges() {
			return changes;
		}

		private ChangeList[] changes;

		public <T> void put(final DataKey<T> key, final T data) {
			changes = (ChangeList[]) data;
		}
	}
}