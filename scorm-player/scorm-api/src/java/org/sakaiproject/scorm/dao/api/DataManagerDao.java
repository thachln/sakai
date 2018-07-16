/*
 * #%L
 * SCORM API
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
package org.sakaiproject.scorm.dao.api;

import java.util.List;

import org.adl.datamodels.IDataManager;

public interface DataManagerDao {

	public List<IDataManager> find(long contentPackageId, String learnerId, long attemptNumber);

	//public List<IDataManager> find(String courseId);

	public IDataManager find(long contentPackageId, String learnerId, long attemptNumber, String scoId);

	public IDataManager find(String courseId, String scoId, String userId, boolean fetchAll, long attemptNumber);

	public IDataManager find(String courseId, String scoId, String userId, long attemptNumber);

	public IDataManager findByActivityId(long contentPackageId, String activityId, String userId, long attemptNumber);

	public IDataManager load(long id);

	public void save(IDataManager dataManager);

	public void update(IDataManager dataManager);

}
