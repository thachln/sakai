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

import org.sakaiproject.scorm.dao.api.ScormCategoryDao;
import org.sakaiproject.scorm.model.api.ScormCategory;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public class ScormCategoryDaoImpl extends HibernateDaoSupport implements ScormCategoryDao {

	@Override
	public List<ScormCategory> find(String context) {
		String statement = new StringBuilder("from ").append(ScormCategory.class.getName()).append(" where deleted = :deleted order by categoryName asc ").toString();

		return (List<ScormCategory>) getHibernateTemplate().findByNamedParam(statement, new String[]{"deleted"}, new Object[] { false });
	}

	@Override
	public ScormCategory load(long id) {
		return (ScormCategory) getHibernateTemplate().load(ScormCategory.class, id);
	}

	@Override
	public void remove(ScormCategory category) {
		category.setDeleted(true);
		getHibernateTemplate().saveOrUpdate(category);		
	}

	@Override
	public void save(ScormCategory category) {
		getHibernateTemplate().saveOrUpdate(category);
		
	}

	
}