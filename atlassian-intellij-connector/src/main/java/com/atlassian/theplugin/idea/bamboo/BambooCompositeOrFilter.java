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
package com.atlassian.theplugin.idea.bamboo;

import com.atlassian.connector.intellij.bamboo.BambooBuildAdapter;

import java.util.Collection;

class BambooCompositeOrFilter implements BambooBuildFilter {
	private final Collection<BambooBuildFilter> filters;

	public BambooCompositeOrFilter(final Collection<BambooBuildFilter> filters) {
		this.filters = filters;
	}

	public Collection<BambooBuildFilter> getFilters() {
		return filters;
	}

	public boolean doesMatch(final BambooBuildAdapter build) {
		for (BambooBuildFilter filter : filters) {
			if (filter.doesMatch(build)) {
				return true;
			}
		}
		return false;
	}
}
