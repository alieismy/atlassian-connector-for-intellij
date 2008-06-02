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

package com.atlassian.theplugin.idea.config.serverconfig;

import com.atlassian.theplugin.commons.ServerType;
import com.atlassian.theplugin.commons.configuration.*;
import com.atlassian.theplugin.idea.config.ContentPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jacek
 * Date: 2008-03-07
 * Time: 11:30:05
 * To change this template use File | Settings | File Templates.
 */
public class CrucibleGeneralForm extends JComponent implements ContentPanel {
	private JPanel rootComponent;
	private JSpinner pollTimeSpinner;
	private JRadioButton unreadCrucibleReviews;
	private JRadioButton never;
	private SpinnerModel model;

	private transient PluginConfigurationBean globalPluginConfiguration;

	private transient CrucibleConfigurationBean crucibleConfiguration;

	private transient PluginConfiguration localPluginConfigurationCopy;
	private static CrucibleGeneralForm instance;

	private CrucibleGeneralForm(PluginConfigurationBean globalPluginConfiguration) {

		this.globalPluginConfiguration = globalPluginConfiguration;

		$$$setupUI$$$();

		model = new SpinnerNumberModel(1, 1, 1000, 1);
		pollTimeSpinner.setModel(model);

		this.setLayout(new BorderLayout());
		add(rootComponent, BorderLayout.WEST);
	}

	public static CrucibleGeneralForm getInstance(PluginConfigurationBean globalPluginConfiguration) {
		if (instance == null) {
			instance = new CrucibleGeneralForm(globalPluginConfiguration);
		}
		return instance;
	}


	public boolean isEnabled() {
		return true;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isModified() {
		if (crucibleConfiguration.getCrucibleTooltipOption() != null) {
			if (crucibleConfiguration.getCrucibleTooltipOption() != getCrucibleTooltipOption()) {
				return true;
			}
		} else if (getCrucibleTooltipOption() != CrucibleTooltipOption.UNREAD_REVIEWS) {
			return true;
		}
		return (Integer) model.getValue() != crucibleConfiguration.getPollTime();
	}

	public String getTitle() {
		return "Crucible";
	}

	private CrucibleTooltipOption getCrucibleTooltipOption() {
		if (unreadCrucibleReviews.isSelected()) {
			return CrucibleTooltipOption.UNREAD_REVIEWS;
		} else if (never.isSelected()) {
			return CrucibleTooltipOption.NEVER;
		} else {
			return getDefaultTooltipOption();
		}
	}

	public void getData() {
		((CrucibleConfigurationBean) getLocalPluginConfigurationCopy()
				.getProductServers(ServerType.CRUCIBLE_SERVER))
				.setCrucibleTooltipOption(getCrucibleTooltipOption());

		((CrucibleConfigurationBean) globalPluginConfiguration
				.getProductServers(ServerType.CRUCIBLE_SERVER))
				.setCrucibleTooltipOption(getCrucibleTooltipOption());

		((CrucibleConfigurationBean) getLocalPluginConfigurationCopy()
				.getProductServers(ServerType.CRUCIBLE_SERVER))
				.setPollTime((Integer) model.getValue());

		((CrucibleConfigurationBean) globalPluginConfiguration
				.getProductServers(ServerType.CRUCIBLE_SERVER))
				.setPollTime((Integer) model.getValue());
	}

	public void setData(PluginConfiguration config) {

		localPluginConfigurationCopy = config;

		crucibleConfiguration =
				(CrucibleConfigurationBean) localPluginConfigurationCopy.getProductServers(ServerType.CRUCIBLE_SERVER);
		CrucibleTooltipOption configOption = this.crucibleConfiguration.getCrucibleTooltipOption();

		if (configOption != null) {
			switch (configOption) {
				case UNREAD_REVIEWS:
					unreadCrucibleReviews.setSelected(true);
					break;
				case NEVER:
					never.setSelected(true);
					break;
				default:
					never.setSelected(true);
					break;
			}
		} else {
			setDefaultTooltipOption();
		}

		model.setValue(crucibleConfiguration.getPollTime());
	}

	private void setDefaultTooltipOption() {
		unreadCrucibleReviews.setSelected(true);
	}

	private CrucibleTooltipOption getDefaultTooltipOption() {
		return CrucibleTooltipOption.UNREAD_REVIEWS;
	}

	public PluginConfiguration getLocalPluginConfigurationCopy() {
		return localPluginConfigurationCopy;
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		rootComponent = new JPanel();
		rootComponent.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootComponent.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null));
		final JLabel label1 = new JLabel();
		label1.setText("Polling Time [minutes]:");
		rootComponent.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		rootComponent.add(panel1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		pollTimeSpinner = new JSpinner();
		panel1.add(pollTimeSpinner, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootComponent.add(spacer1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		rootComponent.add(spacer2, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(70, 14), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Show Popup:");
		rootComponent.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		unreadCrucibleReviews = new JRadioButton();
		unreadCrucibleReviews.setText("Unread crucible reviews exist");
		rootComponent.add(unreadCrucibleReviews, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		never = new JRadioButton();
		never.setText("Never");
		rootComponent.add(never, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		ButtonGroup buttonGroup;
		buttonGroup = new ButtonGroup();
		buttonGroup.add(unreadCrucibleReviews);
		buttonGroup.add(never);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootComponent;
	}
}
