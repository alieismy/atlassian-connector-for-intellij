package com.atlassian.theplugin.idea.action.bitbucket;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class OpenBitbucketLinkInBrowserAction extends AnAction
{

    @Override
    public void actionPerformed(final AnActionEvent event)
    {
        VcsActionDetails details = IntelliJGitUtils.extractVcsActionDetails(event);
        String remoteUrl = IntelliJGitUtils.createBitbucketUrl(details);

        if (remoteUrl != null) {
            BrowserUtil.browse(remoteUrl);
        }

    }

}