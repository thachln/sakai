/**********************************************************************************
 * $URL: $
 * $Id: $
 ***********************************************************************************
 *
 * Author: Charles Hedrick, hedrick@rutgers.edu
 *
 * Copyright (c) 2010 Rutgers, the State University of New Jersey
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");                                                                
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.lessonbuildertool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean.UrlItem;
import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedMetaData;
import org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AuthzQueriesFacadeAPI;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacadeQueriesAPI;
import org.sakaiproject.tool.assessment.qti.constants.QTIVersion;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.services.qti.QTIService;
import org.sakaiproject.util.FormattedText;

import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.site.cover.SiteService;
import org.w3c.dom.Document;
import uk.org.ponder.messageutil.MessageLocator;

import java.io.IOException;
import java.util.*;

/**
 * Interface to Message Forums, the forum that comes with Sakai
 *
 * @author Charles Hedrick <hedrick@rutgers.edu>
 * 
 */

// NOTE: almost no other class should import this. We want to be able
// to support both forums and jforum. So typically there will be a
// forumEntity, but it's injected, and it can be either forum and jforum.
// Hence it has to be declared LessonEntity. That leads to a lot of
// declarations like LessonEntity forumEntity. In this case forumEntity
// means either a ForumEntity or a JForumEntity. We can't just call the
// variables lessonEntity because the same module will probably have an
// injected class to handle tests and quizes as well. That will eventually
// be converted to be a LessonEntity.

public class ScormEntity implements LessonEntity {

	private static Logger log = LoggerFactory.getLogger(ScormEntity.class);

	private static Cache assessmentCache = null;
	protected static final int DEFAULT_EXPIRATION = 10 * 60;
	private static boolean scorm_linked = false;

	private SimplePageToolDao simplePageToolDao;
	
	protected ContentPackage contentPackage;

	public void setSimplePageToolDao(Object dao) {
		simplePageToolDao = (SimplePageToolDao) dao;
	}

	private SimplePageBean simplePageBean;

	public void setSimplePageBean(SimplePageBean simplePageBean) {
		this.simplePageBean = simplePageBean;
	}
	
	static ScormContentService scormContentService;

	public static ScormContentService getScormContentService() {
		return scormContentService;
	}

	public static void setScormContentService(ScormContentService scormContentService) {
		ScormEntity.scormContentService = scormContentService;
	}

	private LessonEntity nextEntity = null;

	public void setNextEntity(LessonEntity e) {
		nextEntity = e;
	}

	public LessonEntity getNextEntity() {
		return nextEntity;
	}

	static MemoryService memoryService = null;

	public void setMemoryService(MemoryService m) {
		memoryService = m;
	}

	static MessageLocator messageLocator = null;

	public void setMessageLocator(MessageLocator m) {
		messageLocator = m;
	}

	public void init() {
		assessmentCache = memoryService.newCache("org.sakaiproject.lessonbuildertool.service.ScormEntity.cache");

		scorm_linked = ServerConfigurationService.getBoolean("lessonbuilder.scorm.editlink", false);
		
		log.info("SamigoEntity edit link " + scorm_linked);

		log.info("init()");

	}

	public void destroy() {
		// assessmentCache.destroy();
		// assessmentCache = null;

		log.info("destroy()");
	}

	// to create bean. the bean is used only to call the pseudo-static
	// methods such as getEntitiesInSite. So type, id, etc are left
	// uninitialized

	protected ScormEntity() {
	}

	protected ScormEntity(int type, Long id, int level) {
		this.type = type;
		this.id = id;
		this.level = level;
	}

	public String getToolId() {
		return "sakai.scorm.tool";
	}

	// the underlying object, something Sakaiish
	protected Long id;
	protected int type;
	protected int level;
	// not required fields. If we need to look up
	// the actual objects, lets us cache them
	//protected PublishedAssessmentData assessment;
	
	
	public ContentPackage getContentPackage(Long contentPackageId) {
		return getContentPackage(contentPackageId, false);
	}

	public ContentPackage getContentPackage(Long contentPackageId, boolean nocache) {
		ContentPackage ret = (ContentPackage) assessmentCache.get(contentPackageId.toString());

		if (!nocache && ret != null) {
			return ret;
		}

		try {
			ret = scormContentService.getContentPackage(contentPackageId);			
		} catch (Exception e) {
			return null;
		}

		if (ret != null) {			
			assessmentCache.put(contentPackageId.toString(), ret);
		}

		return ret;
	}


	// type of the underlying object
	public int getType() {
		return type;
	}

	public int getTypeOfGrade() {
		return 1;
	}

	public int getLevel() {
		return level;
	}

	// hack for forums. not used for assessments, so always ok
	public boolean isUsable() {
		return true;
	}

	public String getReference() {
		return "/" + SCORM + "/" + id;
	}

	public List<LessonEntity> getEntitiesInSite() {
		return getEntitiesInSite(null);
	}

	// find topics in site, but organized by forum
	public List<LessonEntity> getEntitiesInSite(SimplePageBean bean) {

		Session ses = SessionManager.getCurrentSession();

		List<ContentPackage> contentPackages = scormContentService.getContentPackages();

		List<LessonEntity> ret = new ArrayList<LessonEntity>();
		// security. assume this is only used in places where it's OK, so skip
		// security checks
		for (ContentPackage contentPackage : contentPackages) {
			ScormEntity entity = new ScormEntity(TYPE_SCORM, contentPackage.getContentPackageId(), 1);
			ret.add(entity);

		}

		if (nextEntity != null)
			ret.addAll(nextEntity.getEntitiesInSite(bean));

		return ret;
	}

	public LessonEntity getEntity(String ref) {
		return getEntity(ref, null);
	}

	public LessonEntity getEntity(String ref, SimplePageBean o) {

		int i = ref.indexOf("/", 1);
		if (i < 0) {
			// old format, just the number
			try {
				return new ScormEntity(TYPE_SCORM, Long.valueOf(ref), 1);
			} catch (Exception ignore) {
				return null;
			}
		}
		String typeString = ref.substring(1, i);
		String idString = ref.substring(i + 1);
		Long id = 0L;
		try {
			id = Long.parseLong(idString);
		} catch (Exception ignore) {
			return null;
		}

		if (typeString.equals(SCORM)) {
			return new ScormEntity(TYPE_SCORM, id, 1);
		} else if (nextEntity != null) {
			return nextEntity.getEntity(ref);
		} else
			return null;
	}

	// properties of entities
	public String getTitle() {
		if (contentPackage == null)
			contentPackage = getContentPackage(id);
		if (contentPackage == null)
			return null;
		return FormattedText.convertFormattedTextToPlaintext(contentPackage.getTitle());
	}

	public String getAssessmentAlias(Long publishedId) {
		return null;
	}

	public String getUrl() {
		Site site = null;
		try {
		    site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		} catch (Exception impossible) {
		    return null;
		}
		ToolConfiguration tool = site.getToolForCommonId("sakai.scorm.tool");
		
		// LSNBLDR-21. If the tool is not in the current site we shouldn't return a url
		if(tool == null) {
		    return null;
		}
		
		if (contentPackage == null)
			contentPackage = getContentPackage(id);
		if (contentPackage == null)
			return null;
		
		String placement = tool.getId();
		String url = ServerConfigurationService.getToolUrl() + "/" + placement + "?wicket:bookmarkablePage=ScormPlayer:org.sakaiproject.scorm.ui.player.pages.PlayerPage";
		url += "&contentPackageId=" + contentPackage.getContentPackageId();
		url += "&resourceId=" + contentPackage.getResourceId();
		url += "&title="+ contentPackage.getTitle();
		
		return url;
	}

	// I don't think they have this
	public Date getDueDate() {
		if (contentPackage == null)
			contentPackage = getContentPackage(id);
		if (contentPackage == null)
			return null;
		return contentPackage.getDueOn();
	}

	// the following methods all take references. So they're in effect static.
	// They ignore the entity from which they're called.
	// The reason for not making them a normal method is that many of the
	// implementations seem to let you set access control and find submissions
	// from a reference, without needing the actual object. So doing it this
	// way could save some database activity

	// access control
	public boolean addEntityControl(String siteId, String groupId) throws IOException {

		// I don't want to do a full load of the facade most of the time. So we
		// use
		// PublishedAssessmentData normally. Unfortunately here we need it
		/*PublishedAssessmentFacade assessment = null;
		AssessmentAccessControlIfc control = null;

		try {
			assessment = pService.getPublishedAssessment(Long.toString(id));
			control = assessment.getAssessmentAccessControl();
		} catch (Exception e) {
			log.warn("can't find published " + id, e);
			return false;
		}

		AuthzQueriesFacadeAPI authz = PersistenceService.getInstance().getAuthzQueriesFacade();

		if (authz == null) {
			log.warn("Null Authorization");
			return false;
		}

		if (!control.getReleaseTo().equals(AssessmentAccessControlIfc.RELEASE_TO_SELECTED_GROUPS)) {
			control.setReleaseTo(AssessmentAccessControlIfc.RELEASE_TO_SELECTED_GROUPS);
			pService.saveAssessment(assessment);
			String qualifierIdString = assessment.getPublishedAssessmentId().toString();

			// the original one lists the site. once we set release to groups,
			// it will try to look
			// up the site id as a group id. very bad, so remove all existing
			// ones.
			authz.removeAuthorizationByQualifierAndFunction(qualifierIdString, "TAKE_PUBLISHED_ASSESSMENT");

			// and add our group
			authz.createAuthorization(groupId, "TAKE_PUBLISHED_ASSESSMENT", Long.toString(id));
		} else {
			// already release to groups. see if we need to add our group
			List<AuthorizationData> authorizations = authz
					.getAuthorizationByFunctionAndQualifier("TAKE_PUBLISHED_ASSESSMENT", Long.toString(id));
			boolean found = false;

			for (AuthorizationData ad : authorizations) {
				if (ad.getAgentIdString().equals(groupId)) {
					found = true;
					break;
				}
			}

			// if not, add it; can't add it otherwise or we get duplicates
			if (!found) {
				authz.createAuthorization(groupId, "TAKE_PUBLISHED_ASSESSMENT", Long.toString(id));
			}
		}*/

		return true;
	}

	public boolean removeEntityControl(String siteId, String groupId) throws IOException {
		// I don't want to do a full load of the facade most of the time. So we
		// use
		// PublishedAssessmentData normally. Unfortunately here we need it
		/*PublishedAssessmentFacade assessment = null;
		AssessmentAccessControlIfc control = null;

		try {
			assessment = pService.getPublishedAssessment(Long.toString(id));
			control = assessment.getAssessmentAccessControl();
		} catch (Exception e) {
			log.warn("can't find published " + id, e);
			return false;
		}

		AuthzQueriesFacadeAPI authz = PersistenceService.getInstance().getAuthzQueriesFacade();

		if (!control.getReleaseTo().equals(AssessmentAccessControlIfc.RELEASE_TO_SELECTED_GROUPS)) {
			// not release to groups, nothing to do
			return true;
		} else {
			// what do we do if it was originally released to groups, and then
			// we added ours? I
			// guess jsut remove ours?
			List<AuthorizationData> authorizations = authz
					.getAuthorizationByFunctionAndQualifier("TAKE_PUBLISHED_ASSESSMENT", Long.toString(id));
			boolean foundother = false;
			for (AuthorizationData ad : authorizations) {
				if (ad.getAgentIdString().equals(groupId)) {
				} else {
					foundother = true;
				}
			}

			if (foundother) {
				// just remove our group
				authz.removeAuthorizationByAgentQualifierAndFunction(groupId, Long.toString(id),
						"TAKE_PUBLISHED_ASSESSMENT");
			} else {
				// otherwise remove all groups
				authz.removeAuthorizationByQualifierAndFunction(Long.toString(id), "TAKE_PUBLISHED_ASSESSMENT");
			}

			Site site = null;

			try {
				site = SiteService.getSite(siteId);
			} catch (Exception e) {
				return false;
			}

			// put back the site
			authz.createAuthorization(siteId, "TAKE_PUBLISHED_ASSESSMENT", Long.toString(id));

			// and put back the access control
			control.setReleaseTo(site.getTitle()); // what if it's too long?

			// and save the updated info
			pService.saveAssessment(assessment);

		}*/
		return true;
	}

	// submission
	// do we need the data from submission?
	public boolean needSubmission() {
		return false;
	}

	public LessonSubmission getSubmission(String user) {
		return null;
/*		if (assessment == null)
			assessment = getPublishedAssessment(id);
		if (assessment == null) {
			log.warn("can't find published " + id);
			return null;
		}

		GradingService gradingService = new GradingService();

		Session ses = SessionManager.getCurrentSession();

		AssessmentGradingData grading = null;
		try {
			if (assessment.getEvaluationModel().getScoringType() == EvaluationModelIfc.LAST_SCORE) {
				grading = gradingService.getLastSubmittedAssessmentGradingByAgentId(Long.toString(id), ses.getUserId(),
						null);
			} else {
				// the declared return type changed from AssessmentGradingIfc to
				// Data. But the actual
				// underlying object is Data. In the old code Data implemented
				// Ifc, but that no longer
				// seems to be true. I believe this cast will work either way.
				grading = (AssessmentGradingData) gradingService.getHighestSubmittedAssessmentGrading(Long.toString(id),
						ses.getUserId());
			}
		} catch (Exception e) {
			log.info("unable to find submission for samigo item " + id);
			grading = null;
		}
		if (grading == null)
			return null;

		return new LessonSubmission(toDouble(grading.getFinalScore()));*/

	}

	public Double toDouble(Object f) {
		if (f instanceof Double)
			return (Double) f;
		else if (f instanceof Float)
			return ((Float) f).doubleValue();
		else
			return null;
	}

	// we can do this for real, but the API will cause us to get all the
	// submissions in full, not just a count.
	// I think it's cheaper to get the best assessment, since we don't actually
	// care whether it's 1 or >= 1.
	public int getSubmissionCount(String user) {
		return 0;
		/*if (getSubmission(user) == null)
			return 0;
		else
			return 1;*/
	}

	// URL to create a new item. Normally called from the generic entity, not a
	// specific one
	// can't be null
	public List<UrlItem> createNewUrls(SimplePageBean bean) {
		ArrayList<UrlItem> list = new ArrayList<UrlItem>();
		String tool = bean.getCurrentTool("sakai.samigo");
		if (tool != null) {
			tool = ServerConfigurationService.getToolUrl() + "/" + tool + "/jsf/index/mainIndex";
			list.add(new UrlItem(tool, messageLocator.getMessage("simplepage.create_samigo")));
		}
		if (nextEntity != null)
			list.addAll(nextEntity.createNewUrls(bean));
		return list;
	}

	// URL to edit an existing entity.
	// Can be null if we can't get one or it isn't needed
	public String editItemUrl(SimplePageBean bean) {
		return "";
		/*String tool = bean.getCurrentTool("sakai.samigo");
		if (tool == null)
			return null;

		if (false) {
			// code to verify that exportObject actually works
			if (assessment == null)
				assessment = getPublishedAssessment(id);
			String aid = assessment.getAssessmentId().toString();

			Document doc = exportObject(aid);
			log.info("foo " + doc.getElementsByTagName("questestinterop"));
		}

		if (scorm_linked)
			return ServerConfigurationService.getToolUrl() + "/" + tool + "/jsf/author/editLink?publishedAssessmentId="
					+ id;
		else
			return ServerConfigurationService.getToolUrl() + "/" + tool + "/jsf/index/mainIndex";*/
	}

	// for most entities editItem is enough, however tests allow separate
	// editing of
	// contents and settings. This will be null except in that situation
	public String editItemSettingsUrl(SimplePageBean bean) {
		return "";
		/*String tool = bean.getCurrentTool("sakai.samigo");
		if (tool == null)
			return null;

		if (scorm_linked)
			return ServerConfigurationService.getToolUrl() + "/" + tool + "/jsf/author/editLink?publishedAssessmentId="
					+ id + "&settings=true";
		else
			return ServerConfigurationService.getToolUrl() + "/" + tool + "/jsf/index/mainIndex";*/

	}
	

	public boolean objectExists() {
		if (contentPackage == null)
			contentPackage = getContentPackage(id);
		return contentPackage != null;
	}

	public boolean notPublished(String ref) {
		return false;
		/*if (ref.startsWith("/sam_core/"))
			return true;
		else
			return false;*/
	}

	public boolean notPublished() {
		return false;
	}

	// return the list of groups if the item is only accessible to specific
	// groups
	// null if it's accessible to the whole site. Update the data in the cache
	// use the comments field, since there's no place to put a list and we don't
	// use
	// that field
	public List<String> getGroups(boolean nocache) {
		if (nocache)
			contentPackage = getContentPackage(id, true);
		else if (contentPackage == null)
			contentPackage = getContentPackage(id);
		if (contentPackage == null)
			return null;

		// our model doens't include anonymous. Treat as no groups

		String groupString = contentPackage.getGroupsPermission();
		if (groupString != null) {
			if (groupString.equals("")) // release to site
				return null;
			else
				return Arrays.asList(groupString.split(","));
		} else {
			return null;
		}
		
	}

	// set the item to be accessible only to the specific groups.
	// null to make it accessible to the whole site
	public void setGroups(Collection<String> groups) {
		String groupPermistion = "";
		if (groups != null && groups.size() == 0)
			groups = null;

		if(null != groups){
			int i = 0;
			for(String item: groups){
				i++;
				groupPermistion += item;
				if(i < groups.size()){
					groupPermistion += ",";
				}
				
			}
		} 
		if (contentPackage == null) {
			contentPackage = getContentPackage(id);
		}
		if (contentPackage != null) {
			contentPackage.setGroupsPermission(groupPermistion);
			scormContentService.updateContentPackage(contentPackage);
		}
	}

	// this is what goes into the XML file. Samigo doesn't support RefMigrator,
	// so the only way
	// we can connect tests in the new site with the old one is by title. Thus
	// what we save is
	// use sam_core/TITLE
	// this is the title of the core assessment, since that's what gets copied.
	public String getObjectId() {
		if (contentPackage == null)
			contentPackage = getContentPackage(id);
		if (contentPackage == null)
			return null;
		Long coreId = contentPackage.getContentPackageId();
		String title = contentPackage.getTitle();
		return "scorm_core/" + title;
	}

	// normally this will look up the object ID and find the corresponding
	// sakaiid in the
	// new site. Unfortunately, the assessment hasn't been published, so the
	// best we
	// can do is return the corresponding core assessment. When we try to refer
	// to it,
	// getEntity will see if it's been published yet.
	// returns sam_core/NNNN
	public String findObject(String objectid, Map<String, String> objectMap, String siteid) {

		/*if (!objectid.startsWith("scorm_core/")) {
			if (nextEntity != null) {
				return nextEntity.findObject(objectid, objectMap, siteid);
			}
			return null;
		}

		String title = objectid.substring("scorm_core/".length());

		// this is an expensive query, but this is called pretty rarely, so it's
		// probably better to do this than
		// got to the database ourselves. We'd be a lot better with a
		// getBasicInfo version
		List<AssessmentData> list = assessmentService.getAllActiveAssessmentsbyAgent(siteid);
		for (AssessmentData data : list) {
			if (data.getTitle().equals(title)) {
				return "/sam_core/" + data.getAssessmentBaseId();
			}
		}*/
		return null;
	}

	public String getSiteId() {
		String siteId = "";
		ContentPackage contentPackage = scormContentService.getContentPackage(id);
		if(null != contentPackage){
			siteId = contentPackage.getContext();
		}
		return siteId;
	}

}
