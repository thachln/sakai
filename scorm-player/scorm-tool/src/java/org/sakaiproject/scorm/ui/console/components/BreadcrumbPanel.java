/*
 * #%L
 * SCORM Tool
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
package org.sakaiproject.scorm.ui.console.components;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.sakaiproject.wicket.markup.html.link.BookmarkablePageLabeledLink;

public class BreadcrumbPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private final RepeatingView breadcrumbList;
	private int numberOfCrumbs;
	
	public BreadcrumbPanel(String id) {
		super(id);
		add(breadcrumbList = new RepeatingView("breadcrumbList"));
	}

	public void addBreadcrumb(IModel model, Class<?> pageClass, PageParameters params, boolean isEnabled) {
		BookmarkablePageLabeledLink link = new BookmarkablePageLabeledLink("breadcrumb", model, pageClass, params);
		link.setEnabled(isEnabled);
		link.add(new SimpleAttributeModifier("style", "font-size: 16px"));
		
		Label separator = new Label("separator", new Model(">"));
		separator.setVisible(numberOfCrumbs > 0);
		
		WebMarkupContainer item = new WebMarkupContainer(breadcrumbList.newChildId());
		item.setRenderBodyOnly(true);
		item.add(separator);
		item.add(link);

		breadcrumbList.add(item);
		numberOfCrumbs++;
	}

	public int getNumberOfCrumbs() {
		return numberOfCrumbs;
	}
	
	
}
