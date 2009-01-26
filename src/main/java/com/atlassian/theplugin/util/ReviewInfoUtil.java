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

package com.atlassian.theplugin.util;

import com.atlassian.theplugin.commons.crucible.ValueNotYetInitialized;
import com.atlassian.theplugin.commons.crucible.api.model.Review;
import com.atlassian.theplugin.commons.crucible.api.model.ReviewAdapter;
import com.atlassian.theplugin.commons.crucible.api.model.Reviewer;


public final class ReviewInfoUtil {

	private ReviewInfoUtil() {
	}

	public static int getNumOfCompletedReviewers(ReviewAdapter review) {
		int numCompleted = 0;

		try {
			for (Reviewer reviewer : review.getReviewers()) {
				if (reviewer.isCompleted()) {
					numCompleted++;
				}
			}
		} catch (ValueNotYetInitialized valueNotYetInitialized) {
			// ignore
		}
		return numCompleted;
	}

	public static String toString(Review review) {
		return review.getPermId().getId() + ": " + review.getName();
	}
}