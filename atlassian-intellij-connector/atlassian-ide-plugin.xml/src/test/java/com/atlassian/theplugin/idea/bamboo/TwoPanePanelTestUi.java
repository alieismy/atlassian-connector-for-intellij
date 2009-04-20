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

import com.atlassian.theplugin.idea.ui.SwingAppRunner;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;


public final class TwoPanePanelTestUi {
	private TwoPanePanelTestUi() {
	}

	public static void main(String[] args) {
		SwingAppRunner.run(new TwoPanePanel() {
			{
				init();
//				setStatusMessage("Very long message<br/>THIS IS VERY LOOOOOOOOOOOOOOOOOONG line<br/>Another line<br/>", true);
			}

			@Override
			protected JTree getRightTree() {
				return new JTree();
			}

			@Override
			protected JComponent getToolBar() {
				final JButton button = new JButton("my toolbar");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						if (new Random().nextBoolean()) {
							if (new Random().nextBoolean()) {
								setStatusMessage("All OK");
							} else {
								setStatusMessage(Arrays.asList("Very looooooooooooooooooong message",
										"THIS IS VERY LOOOOOOOOOOOOOOOOOONG line", "The last line"),
										Collections.<String>emptySet());
							}
						} else {
							setStatusMessage(Arrays.<String>asList("Very long message",
									"THIS IS VERY LOOOOOOOOOOOOOOOOOONG line",
									"Another line"), Arrays.asList("My Error", "Second line of my error",
									"Last lineWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"
											+ "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWXYZWWWWWWWWWWWWWWWWWWWWWWWWWWWW"
											+ "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWAAAAAAAAAMMMMWWWWWWZZZ!"));
						}
					}
				});
				return button;
			}

			@Override
			protected JComponent getLeftPanel() {
				return new JLabel(
						"<html>Very long message<br/>THIS IS VERY LOOOOOOOOOOOOOOOOOONG line<br/>Another line<br/>");
			}
		});
	}

}
