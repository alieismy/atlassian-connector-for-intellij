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

import com.atlassian.theplugin.idea.ui.tree.paneltree.AbstractTreeNode;

import javax.swing.*;

/**
 * @author Jacek Jaroczynski
 */
public class BuildGroupTreeNode extends AbstractTreeNode {
	public BuildGroupTreeNode(String name) {
		super(name, null, null);
	}

	@Override
	public String toString() {
		return name != null && name.length() > 0 ? name : "Unknown";
	}

	@Override
	public JComponent getRenderer(final JComponent c, final boolean selected, final boolean expanded, final boolean hasFocus) {
		return getDefaultRenderer(c, selected, expanded, hasFocus);
	}

//	@Override
//	public void onSelect() {
//
//	}
}
