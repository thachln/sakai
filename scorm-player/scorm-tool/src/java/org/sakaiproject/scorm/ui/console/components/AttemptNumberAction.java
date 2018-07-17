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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.util.lang.PropertyResolver;
import org.sakaiproject.wicket.markup.html.repeater.data.table.Action;

public class AttemptNumberAction extends Action {

	private static final long serialVersionUID = 1L;

	public AttemptNumberAction(String propertyExpression, Class<?> pageClass, String[] paramPropertyExpressions) {
		super(propertyExpression, pageClass, paramPropertyExpressions);
	}
	
	@Override
	public Component newLink(String id, Object bean) {
		String number = String.valueOf(PropertyResolver.getValue(labelPropertyExpression, bean));
		long numberOfAttempts = 0;
		
		numberOfAttempts = Long.parseLong(number);
		
		PageParameters params = buildPageParameters(paramPropertyExpressions, bean);
		
		return new AttemptNumberPanel(id, numberOfAttempts, pageClass, params);
	}
	
}
