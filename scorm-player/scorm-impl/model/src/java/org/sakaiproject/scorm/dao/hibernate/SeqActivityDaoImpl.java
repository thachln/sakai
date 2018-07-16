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
package org.sakaiproject.scorm.dao.hibernate;

import java.util.List;

import org.sakaiproject.scorm.dao.api.SeqActivityDao;
import org.sakaiproject.scorm.model.api.SeqActivitySnapshot;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public class SeqActivityDaoImpl extends HibernateDaoSupport implements SeqActivityDao {

	public SeqActivitySnapshot findSnapshot(String activityId) {
		String statement = "from " + SeqActivitySnapshot.class.getName() + " where activityId = :activityId";
		List<SeqActivitySnapshot> r = (List<SeqActivitySnapshot>) getHibernateTemplate().findByNamedParam(statement, new String[]{"activityId"}, new Object[] { activityId });

		if (r.isEmpty())
		{
			return null;
		}

		for (SeqActivitySnapshot snapshot : r) {
			if (snapshot.getScoId() != null)
				return snapshot;
		}

		return null;
	}

}
