package com.atlassian.theplugin.jira.model;

import com.atlassian.theplugin.commons.cfg.JiraServerCfg;
import com.atlassian.theplugin.commons.cfg.ServerId;
import com.atlassian.theplugin.jira.api.JIRAQueryFragment;
import com.atlassian.theplugin.jira.api.JIRASavedFilter;
import com.atlassian.theplugin.jira.api.JIRASavedFilterBean;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * User: pmaruszak
 */
public class JIRAFilterListModelTest extends TestCase {
	JIRAFilterListModel listModel;
	private int notifiedModelChanged = 0;
	final private JiraServerCfg jServer = new JiraServerCfg("DZira serwer", new ServerId());

	public void setUp() throws Exception {
		super.setUp();
		listModel = new JIRAFilterListModel();
		notifiedModelChanged = 0;
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNotifyListeners() {
		JIRAServerFiltersBean serverFilter1 = new JIRAServerFiltersBean();
		fillInServerFiltersBean(serverFilter1, 2);

		listModel.addModelListener(new JIRAFilterListModelListener() {

			public void modelChanged(final JIRAFilterListModel listModel) {
				notifiedModelChanged++;
			}

		});

		for (int i = 0; i < 10; i++) {
			listModel.fireModelChanged();
		}

		assertEquals(notifiedModelChanged, 10);

		listModel.setManualFilter(jServer, serverFilter1.getManualFilter());
		listModel.setSavedFilters(jServer, serverFilter1.getSavedFilters());


		for (int i = 0; i < 10; i++) {
			listModel.fireModelChanged();
		}

		assertEquals(20, notifiedModelChanged);
	}

	public void testAddSavedManualFilter() {

		JIRAServerFiltersBean serverFilter1 = new JIRAServerFiltersBean();
		final JIRAManualFilter manual = new JIRAManualFilter("Custom filter", new ArrayList<JIRAQueryFragment>());


		fillInServerFiltersBean(serverFilter1, 3);
		serverFilter1.setManualFilter(manual);

		listModel.setSavedFilters(jServer, serverFilter1.getSavedFilters());
		listModel.setManualFilter(jServer, serverFilter1.getManualFilter());

		assertEquals(listModel.getSavedFilters(jServer).size(), 3);
		assertEquals(listModel.getManualFilter(jServer), manual);

	}

	private void fillInServerFiltersBean(JIRAServerFiltersBean bean, int savedFiltersNo) {
		ArrayList<JIRASavedFilter> savedFilters = new ArrayList<JIRASavedFilter>();
		for (int i = 0; i < savedFiltersNo; i++) {
			JIRASavedFilter filter = new JIRASavedFilterBean("saved filter" + i, i);
			savedFilters.add(filter);
		}
		bean.setSavedFilters(savedFilters);
		JIRAManualFilter manual = new JIRAManualFilter("Custom filter", new ArrayList<JIRAQueryFragment>());
		bean.setManualFilter(manual);

	}
}