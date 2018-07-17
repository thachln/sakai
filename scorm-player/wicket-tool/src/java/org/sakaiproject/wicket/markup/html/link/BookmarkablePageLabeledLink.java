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
package org.sakaiproject.wicket.markup.html.link;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;

public class BookmarkablePageLabeledLink extends BookmarkablePageLink {

	private static final long serialVersionUID = 1L;

	public BookmarkablePageLabeledLink(String id, Class pageClass, PageParameters parameters) {
		super(id, pageClass, parameters);
	}

	public BookmarkablePageLabeledLink(String id, IModel model, Class pageClass) {
		super(id, pageClass);
		setModel(model);
	}
	
	public BookmarkablePageLabeledLink(String id, IModel model, Class pageClass, PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(model);
	}
	
	protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		replaceComponentTagBody(markupStream, openTag, getDefaultModelObjectAsString());
	}
}
