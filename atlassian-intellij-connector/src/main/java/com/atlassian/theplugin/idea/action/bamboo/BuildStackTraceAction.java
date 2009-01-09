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

package com.atlassian.theplugin.idea.action.bamboo;

import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.bamboo.BambooTableToolWindowPanel;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class BuildStackTraceAction extends AbstractBamboo2BuildAction {
	@Override
	public void actionPerformed(AnActionEvent event) {
		BambooTableToolWindowPanel tw = IdeaHelper.getBambooToolWindowPanel(event);
		if (tw != null) {
			tw.showBuildStackTrace();
		}
	}

	@Override
	public void update(AnActionEvent event) {
		super.update(event);
		if (build != null && build.getTestsFailed() == 0) {
			event.getPresentation().setEnabled(false);
		}
	}
}