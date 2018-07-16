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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public class ContentPackageDaoImpl extends HibernateDaoSupport implements ContentPackageDao {

    private static Log log = LogFactory.getLog(ContentPackageDaoImpl.class);
	public int countContentPackages(String context, String name) {
		int count = 1;

		List<ContentPackage> contentPackages = find(context);

		for (ContentPackage cp : contentPackages) {

			Pattern p = Pattern.compile(name + "\\s*\\(?\\d*\\)?");
			Matcher m = p.matcher(cp.getTitle());
			if (m.matches()) {
				count++;
			}

		}

		return count;
	}

	public List<ContentPackage> find(String context) {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug ContentPackageDaoImpl find list<ContentPackage>");
        }
	    String statement = new StringBuilder("from ").append(ContentPackage.class.getName()).append(" where context = :context and deleted = :deleted order by category asc,viewSortOrder asc ").toString();

		return (List<ContentPackage>) getHibernateTemplate().findByNamedParam(statement, new String[]{"context","deleted"}, new Object[] { context, false });
	}
	
	// Hoctdy add start
	public int countContentPacketNumberNotDeleted(String context) {
		String statement = new StringBuilder("select count(*) from ").append(ContentPackage.class.getName()).append(" where context = :context and deleted = :deleted order by viewSortOrder asc ").toString();
		List<Long> results = (List<Long>) getHibernateTemplate().findByNamedParam(statement, new String[]{"context","deleted"}, new Object[] { context, false });
		return results.get(0).intValue();
	}
	
	public List<ContentPackage> findByPermission(String context, String groupId) {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug ContentPackageDaoImpl find list<ContentPackage>");
        }
	    String statement = new StringBuilder("from ").append(ContentPackage.class.getName()).append(" where context = :context and deleted = :deleted and groupsPermission like :groupsPermission order by category asc,viewSortOrder asc ").toString();

		return (List<ContentPackage>) getHibernateTemplate().findByNamedParam(statement, new String[]{"context","deleted","groupsPermission"}, new Object[] {  context, false, groupId});
	}

	// Hoctdy add end

	public ContentPackage load(long id) {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.load in ContentPackageDaoImpl Database.Id: " + id);
        }
		return (ContentPackage) getHibernateTemplate().load(ContentPackage.class, id);
	}

	/**
	 * @param resourceId
	 * @return 
	 * @see org.sakaiproject.scorm.dao.api.ContentPackageDao#loadByResourceId(java.lang.String)
	 */
	public ContentPackage loadByResourceId(String resourceId) {
		String statement = new StringBuilder("from ").append(ContentPackage.class.getName()).append(" where resourceId = :resourceId and deleted = :deleted ").toString();
		
		List<ContentPackage> result = (List<ContentPackage>) getHibernateTemplate().findByNamedParam(statement, new String[]{"resourceId","deleted"}, new Object[] { resourceId, false });
		if (result.isEmpty())
		{
			return null;
		}
		else
		{
		    log.debug("Invoke loadByResouceId");
		    log.debug("RESULT: "+result.get(0).getTitle());
			return result.get(0);
		}
	}

	public void remove(ContentPackage contentPackage) {
		contentPackage.setDeleted(true);
		getHibernateTemplate().saveOrUpdate(contentPackage);
	}

	public void save(ContentPackage contentPackage) {
		getHibernateTemplate().saveOrUpdate(contentPackage);
	}

}
