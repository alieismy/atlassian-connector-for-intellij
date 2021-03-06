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

package com.atlassian.theplugin.idea;

import com.atlassian.theplugin.ConnectionWrapper;
import com.atlassian.theplugin.commons.cfg.ServerCfg;
import com.atlassian.theplugin.commons.remoteapi.ProductServerFacade;
import com.atlassian.theplugin.commons.remoteapi.ServerData;
import com.atlassian.theplugin.idea.config.ProjectCfgManagerImpl;
import com.atlassian.theplugin.idea.config.serverconfig.ProductConnector;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PasswordDialog extends JDialog implements TestConnectionListener.ServerDataProvider {

	private JPanel passwordPanel;
//	private JCheckBox chkRememberPassword;
	private JPasswordField passwordField;
	private JButton testConnectionButton;
	private JLabel lblCommand;
	private JTextField userName;
    private JCheckBox cbUseDefault;
    private transient ServerCfg server;
    private final Project project;

    public PasswordDialog(final ServerCfg server, final ProductServerFacade serverFacade, final Project project) {
		this.server = server;
        this.project = project;
        setContentPane(passwordPanel);
		setModal(true);
// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		lblCommand.setText("<html><p>Please provide password to connect \"" + this.server.getName() + "\" server:</p> <p><i>"
				+ this.server.getUrl() + "</i></p></html>");
		userName.setText(server.getUsername());
// call onCancel() on ESCAPE
		passwordPanel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		testConnectionButton.addActionListener(
				new TestConnectionListener(project, new ProductConnector(serverFacade), this, new TestConnectionProcessor() {
					public void setConnectionResult(final ConnectionWrapper.ConnectionState result) {
					}

					public void onSuccess() {
					}

					public void onError(final String errorMessage, Throwable exception, String helpUrl) {
					}
				}));

        cbUseDefault.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                passwordField.setEnabled(!cbUseDefault.isSelected());
                userName.setEnabled(!cbUseDefault.isSelected());
            }
        });

        cbUseDefault.setSelected(server.isUseDefaultCredentials());
	}

	private void onCancel() {
		dispose();
	}

	public ServerData getServer() {
        //@todo server change might not be good
        server.setUsername(getUserName());
        server.setPassword(getPasswordString());
        server.setUseDefaultCredentials(cbUseDefault.isSelected());
        ServerData.Builder builder = new ServerData.Builder(server);
        builder.useDefaultUser(cbUseDefault.isSelected());
        builder.defaultUser(((ProjectCfgManagerImpl)IdeaHelper.getProjectCfgManager(project)).getDefaultCredentials());        
		return builder.build();

	}

	public JPanel getPasswordPanel() {
		return passwordPanel;
	}

	public String getPasswordString() {
		return String.valueOf(passwordField.getPassword());
	}

	public Boolean getShouldPasswordBeStored() {
		return true;//chkRememberPassword.isSelected();
	}

	public String getUserName() {
		return this.userName.getText();
	}

	private void createUIComponents() {
	}


	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
		passwordPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		passwordField = new JPasswordField();
		panel1.add(passwordField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0,
				false));
		testConnectionButton = new JButton();
		testConnectionButton.setText("Test connection");
		testConnectionButton.setMnemonic('T');
		testConnectionButton.setDisplayedMnemonicIndex(0);
		panel1.add(testConnectionButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
				null, null, null, 0, false));
//		chkRememberPassword = new JCheckBox();
//		chkRememberPassword.setEnabled(true);
//		chkRememberPassword.setSelected(false);
//		chkRememberPassword.setText("Store password in configuration");
//		panel1.add(chkRememberPassword, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
//				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
//				null, null, null, 0, false));
		lblCommand = new JLabel();
		lblCommand.setText("");
		panel1.add(lblCommand, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Password");
		panel1.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("User Name");
		panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		userName = new JTextField();
		panel1.add(userName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0,
				false));
		label1.setLabelFor(passwordField);
		label2.setLabelFor(userName);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return passwordPanel;
	}

    public boolean isUseDefaultCredentials() {
        return cbUseDefault.isSelected();
    }
}
