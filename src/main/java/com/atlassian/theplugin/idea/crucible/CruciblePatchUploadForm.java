package com.atlassian.theplugin.idea.crucible;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CruciblePatchUploadForm extends DialogWrapper {
	private JTextArea patchPreview;
	private JPanel rootComponent;

	protected CruciblePatchUploadForm() {
		super(false);
		init();
	}

	public JComponent getRootComponent() {
		return rootComponent;
	}

	public void setPatchPreview(String preview) {
		patchPreview.setText(preview);
	}

	@Nullable
	protected JComponent createCenterPanel() {
		return getRootComponent();
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
		rootComponent = new JPanel();
		rootComponent.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JScrollPane scrollPane1 = new JScrollPane();
		rootComponent.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		patchPreview = new JTextArea();
		patchPreview.setEditable(false);
		patchPreview.setEnabled(true);
		patchPreview.setText("");
		scrollPane1.setViewportView(patchPreview);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootComponent;
	}
}
