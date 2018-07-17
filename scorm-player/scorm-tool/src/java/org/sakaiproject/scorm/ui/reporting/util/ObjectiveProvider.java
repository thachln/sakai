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
package org.sakaiproject.scorm.ui.reporting.util;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.scorm.model.api.Objective;
import org.sakaiproject.wicket.markup.html.repeater.util.EnhancedDataProvider;

public class ObjectiveProvider extends EnhancedDataProvider {

	private static final long serialVersionUID = 1L;

	private final List<Objective> objectives;
	
	public ObjectiveProvider(List<Objective> objectives) {
		this.objectives = objectives;
	}
	
	public Iterator<Objective> iterator(int first, int count) {
		return objectives.subList(first, first + count).iterator();
	}

	public int size() {
		return objectives.size();
	}
}
