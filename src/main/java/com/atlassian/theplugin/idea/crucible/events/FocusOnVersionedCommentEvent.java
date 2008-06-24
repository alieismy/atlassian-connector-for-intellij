package com.atlassian.theplugin.idea.crucible.events;

import com.atlassian.theplugin.idea.crucible.comments.CrucibleReviewActionListener;
import com.atlassian.theplugin.idea.crucible.ReviewDataInfoAdapter;
import com.atlassian.theplugin.commons.crucible.api.model.ReviewItem;
import com.atlassian.theplugin.commons.crucible.api.model.VersionedComment;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: lguminski
 * Date: Jun 23, 2008
 * Time: 4:51:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class FocusOnVersionedCommentEvent extends CrucibleEvent {
	private ReviewDataInfoAdapter reviewDataInfoAdapter;
	private ReviewItem reviewItem;
	private Collection<VersionedComment> versionedComments;
	private VersionedComment selectedComment;

	public FocusOnVersionedCommentEvent(CrucibleReviewActionListener caller, ReviewDataInfoAdapter reviewDataInfoAdapter,
										ReviewItem reviewItem, Collection<VersionedComment> versionedComments,
										VersionedComment selectedComment) {
		super(caller);
		this.reviewDataInfoAdapter = reviewDataInfoAdapter;
		this.reviewItem = reviewItem;
		this.versionedComments = versionedComments;
		this.selectedComment = selectedComment;
	}

	protected void notify(CrucibleReviewActionListener listener) {
		listener.focusOnVersionedComment(reviewDataInfoAdapter, reviewItem, versionedComments, selectedComment);
	}
}
