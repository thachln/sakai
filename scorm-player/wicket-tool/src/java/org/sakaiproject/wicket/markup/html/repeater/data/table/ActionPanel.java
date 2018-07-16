/*
 * #%L
 * SCORM Wicket Toolset
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sakaiproject.wicket.markup.html.repeater.data.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;

/**
 * This class provides associated markup for the {@link ActionColumn} class. 
 * It organizes the actions in each cell of the column. 
 * 
 * @author jrenfro
 */
public class ActionPanel extends Panel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	
	private static final ResourceReference CONSOLE_CSS = new CompressedResourceReference(ActionPanel.class, "res/ActionPanel.css");

	
	public ActionPanel(String id, List<Action> actions, Object bean) {
		super(id);
		
		Action primaryAction = actions.get(0);
		
		add(primaryAction.newLink("action", bean));
		
		if (actions.size() > 1) {
			List<Action> secondaryActions = new ArrayList<Action>(actions.subList(1, actions.size()));
 			add(new ActionListView("actionItemList", secondaryActions, bean));
		} else {
			ActionListView listView = new ActionListView("actionItemList", null, bean);
			listView.setVisible(false);
			add(listView);
		}
	}

	public class ActionListView extends ListView {
		private static final long serialVersionUID = 1L;
		
		private Object bean; 
		
		public ActionListView(String id, List<Action> actions, Object bean) {
			super(id, actions);
			this.bean = bean;
			this.setRenderBodyOnly(true);
 		}
 		
 		@Override
		protected void populateItem(ListItem item) {
			Action action = (Action)item.getModelObject();
			item.add(action.newLink("actionItem", bean));
		}
	}
	
	public void renderHead(IHeaderResponse response) {
		response.renderCSSReference(CONSOLE_CSS);
	}
	
}
