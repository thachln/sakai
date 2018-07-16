/*
 * #%L
 * SCORM Model Impl
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
package org.sakaiproject.scorm.dao.sakai;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.scorm.dao.LearnerDao;
import org.sakaiproject.scorm.model.api.Learner;

public class MockLearnerDaoImpl implements LearnerDao {

	public List<Learner> find(String context) {
		ArrayList<Learner> rv = new ArrayList<Learner>();
		rv.add(load("learner1"));
		return rv;
	}

	public Learner load(String id) {
		Learner learner = new Learner(id);

		return learner;
	}

}