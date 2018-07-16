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

import java.util.Date;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.sakaiproject.scorm.model.api.ContentPackage;

public class StatusColumn extends AbstractColumn {

	private static final long serialVersionUID = 1L;

	public StatusColumn(IModel displayModel, String sortProperty) {
		super(displayModel, sortProperty);
	}

	public void populateItem(Item item, String componentId, IModel model) {
		item.add(new Label(componentId, createLabelModel(model)));
	}
	
	protected IModel createLabelModel(IModel embeddedModel)
	{
		String resourceId = "status.unknown";
		Object target = embeddedModel.getObject();
		
		if (target instanceof ContentPackage) {
			ContentPackage contentPackage = (ContentPackage)target;
			
			Date now = new Date();
			
			if (now.after(contentPackage.getReleaseOn())) {
				if (now.before(contentPackage.getDueOn())) 
					resourceId = "status.open";
				else if (now.before(contentPackage.getAcceptUntil()))
					resourceId = "status.overdue";
				else 
					resourceId = "status.closed";
			} else {
				resourceId = "status.notyetopen";
			}
		}
		
		
		return new ResourceModel(resourceId);
	}

	
	
	
}
