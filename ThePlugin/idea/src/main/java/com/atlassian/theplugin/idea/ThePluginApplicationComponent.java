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

import com.atlassian.theplugin.commons.ConfigurationListener;
import com.atlassian.theplugin.commons.SchedulableChecker;
import com.atlassian.theplugin.commons.cfg.CfgManager;
import com.atlassian.theplugin.commons.cfg.ProjectId;
import com.atlassian.theplugin.commons.configuration.ConfigurationFactory;
import com.atlassian.theplugin.commons.configuration.PluginConfigurationBean;
import com.atlassian.theplugin.commons.util.HttpClientFactory;
import com.atlassian.theplugin.commons.util.LoggerImpl;
import com.atlassian.theplugin.idea.autoupdate.NewVersionChecker;
import com.atlassian.theplugin.idea.config.ConfigPanel;
import com.atlassian.theplugin.util.HttpConfigurableIdeaImpl;
import com.atlassian.theplugin.util.PicoUtil;
import com.atlassian.theplugin.util.PluginTrustManager;
import com.atlassian.theplugin.util.PluginUtil;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.extensions.AreaPicoContainer;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.TrustManager;
import javax.swing.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

@State(name = "atlassian-ide-plugin",
		storages = { @Storage(id = "atlassian-ide-plugin-id", file = "$APP_CONFIG$/atlassian-ide-plugin.app.xml") })
public class ThePluginApplicationComponent
		implements ApplicationComponent, Configurable, PersistentStateComponent<PluginConfigurationBean>,
		ConfigurationListener {

	static {
		AreaPicoContainer apc = Extensions.getRootArea().getPicoContainer();
		PicoUtil.populateGlobalPicoContainer(apc);
	}

	private static final Icon PLUGIN_SETTINGS_ICON = IconLoader.getIcon("/icons/ico_plugin.png");


	private ConfigPanel configPanel;
	private final PluginConfigurationBean configuration;
	private final CfgManager cfgManager;

	private final Timer timer = new Timer("atlassian-idea-plugin background status checkers");
	private static final int TIMER_START_DELAY = 20000;

	private final Collection<TimerTask> scheduledComponents = new HashSet<TimerTask>();

	public Collection<SchedulableChecker> getSchedulableCheckers() {
		return schedulableCheckers;
	}

	private final Collection<SchedulableChecker> schedulableCheckers = new HashSet<SchedulableChecker>();


	public ThePluginApplicationComponent(PluginConfigurationBean configuration, CfgManager cfgManager) {
		this.configuration = configuration;
		this.cfgManager = cfgManager;
		this.configuration.transientSetHttpConfigurable(HttpConfigurableIdeaImpl.getInstance());

		this.schedulableCheckers.add(NewVersionChecker.getInstance(configuration));

		ConfigurationFactory.setConfiguration(configuration);
		TrustManager trustManager;
		try {
			trustManager = new PluginTrustManager(configuration);
			HttpClientFactory.initializeTrustManagers(trustManager);
		} catch (NoSuchAlgorithmException e) {
			PluginUtil.getLogger().error("Error initializing custom trust manager");
		} catch (KeyStoreException e) {
			PluginUtil.getLogger().error("Error initializing custom trust manager");
		}
	}

	@Nls
	public String getDisplayName() {
		return "Atlassian Plugin\nGlobal Settings";
	}

	@Nullable
	public Icon getIcon() {
		return PLUGIN_SETTINGS_ICON;
	}

	@Nullable
	@NonNls
	public String getHelpTopic() {
		return null;
	}


	@NotNull
	@NonNls
	public String getComponentName() {
		return "ThePluginApplicationComponent";
	}

	public void initComponent() {
		new IdeaLoggerImpl(com.intellij.openapi.diagnostic.Logger.getInstance(LoggerImpl.LOGGER_CATEGORY));

	}

	public void disposeComponent() {
		timer.cancel();
	}

	public JComponent createComponent() {
		if (configPanel == null) {
			configPanel = new ConfigPanel(configuration, cfgManager);
		}
		return configPanel;
	}

	public boolean isModified() {
		return true;
		//return configPanel.isModified();
	}

	private void disableTimers() {
		Iterator<TimerTask> i = scheduledComponents.iterator();
		while (i.hasNext()) {
			TimerTask timerTask = i.next();
			i.remove();
			timerTask.cancel();
		}

		timer.purge();
	}

	/**
	 * Reschedule the BambooStatusChecker with immediate execution trigger.
	 *
	 * @param rightNow set to false if the first execution should be delayed by {@link #TIMER_START_DELAY}.
	 */
	public void rescheduleStatusCheckers(boolean rightNow) {

		disableTimers();
		long delay = rightNow ? 0 : TIMER_START_DELAY;

		for (SchedulableChecker checker : schedulableCheckers) {
			if (checker.canSchedule()) {
				final TimerTask newTask = checker.newTimerTask();
				scheduledComponents.add(newTask);
				timer.schedule(newTask, delay, checker.getInterval());
			} else {
				checker.resetListenersState();
			}
		}
	}



	public void apply() throws ConfigurationException {
		if (configPanel != null) {
			// Get data from configPanel to component
			configPanel.saveData();
			//configPanel.setData();

			for (Project project : ProjectManager.getInstance().getOpenProjects()) {
				ThePluginProjectComponent projectComponent = project.getComponent(ThePluginProjectComponent.class);
				// show icons if necessary
				projectComponent.getStatusBarBambooIcon().showOrHideIcon();
				projectComponent.getStatusBarCrucibleIcon().showOrHideIcon();

				projectComponent.getToolWindow().showHidePanels();
			}
			rescheduleStatusCheckers(true);
		}

	}

	public void reset() {
		if (configPanel != null) {
			// Reset configPanel data from component
			configPanel.setData();
		}
	}

	public void disposeUIResources() {
		configPanel = null;
	}

	public PluginConfigurationBean getState() {
		return configuration;
	}

	public void loadState(PluginConfigurationBean state) {
		configuration.setConfiguration(state);
		configuration.transientSetHttpConfigurable(HttpConfigurableIdeaImpl.getInstance());
	}

	public void updateConfiguration(final ProjectId project, final CfgManager aCfgManager) {
		rescheduleStatusCheckers(true);
	}

	public void projectUnregistered() {
	}
}
