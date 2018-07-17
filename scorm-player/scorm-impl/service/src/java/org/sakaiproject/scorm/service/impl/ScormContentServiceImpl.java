/*
 * #%L
 * SCORM Service Impl
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
package org.sakaiproject.scorm.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.adl.parsers.dom.DOMTreeUtility;
import org.adl.sequencer.ADLSeqUtilities;
import org.adl.sequencer.ISeqActivityTree;
import org.adl.validator.IValidator;
import org.adl.validator.IValidatorOutcome;
import org.adl.validator.contentpackage.CPValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.dao.LearnerDao;
import org.sakaiproject.scorm.dao.api.ContentPackageDao;
import org.sakaiproject.scorm.dao.api.ContentPackageManifestDao;
import org.sakaiproject.scorm.dao.api.DataManagerDao;
import org.sakaiproject.scorm.dao.api.ScormCategoryDao;
import org.sakaiproject.scorm.exceptions.InvalidArchiveException;
import org.sakaiproject.scorm.exceptions.ResourceNotDeletedException;
import org.sakaiproject.scorm.exceptions.ResourceStorageException;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.ContentPackageManifest;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResourceService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.cover.ToolManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ScormContentServiceImpl implements ScormContentService, ScormConstants {
	private static Log log = LogFactory.getLog(ScormContentServiceImpl.class);

	// Data access objects (also dependency injected by lookup method)
	protected abstract ContentPackageDao contentPackageDao();

	protected abstract ContentPackageManifestDao contentPackageManifestDao();
	
	protected abstract ScormCategoryDao scormCategoryDao();
		

	/**
	 * Takes the identifier for a content package that's been stored in the
	 * content repository and creates the necessary objects in the database to
	 * make it recognizable as a content package.
	 * @return 
	 */
	private ContentPackage convertToContentPackage(String resourceId, IValidator validator, IValidatorOutcome outcome) throws Exception {

		ContentPackageManifest manifest = createManifest(outcome.getDocument(), validator);

		// Grab some important info about the site and user
		String context = lms().currentContext();
		String learnerId = lms().currentLearnerId();
		Date now = new Date();

		String title = getContentPackageTitle(outcome.getDocument());

		int packageCount = contentPackageDao().countContentPackages(context, title);

		if (packageCount > 1) {
			title = new StringBuilder(title).append(" (").append(packageCount).append(")").toString();
		}

		String archiveId = resourceService().convertArchive(resourceId, title);

		Serializable manifestId = contentPackageManifestDao().save(manifest);
		
		int numberOfpackage = contentPackageDao().countContentPacketNumberNotDeleted(context);

		// Now create a representation of this content package in the database
		ContentPackage cp = new ContentPackage(title, archiveId);
		cp.setContext(context);
		cp.setManifestId(manifestId);
		cp.setReleaseOn(new Date());
		cp.setCreatedBy(learnerId);
		cp.setModifiedBy(learnerId);
		cp.setCreatedOn(now);
		cp.setModifiedOn(now);
		cp.setStartNewAttempt(true);
		cp.setViewSortOrder(numberOfpackage + 1);

		contentPackageDao().save(cp);
		return cp;
	}

	private File createFile(InputStream inputStream) {
		File tempFile = null;
		log.debug("Invoke createFile in ScromContentService");
		try {
			tempFile = File.createTempFile("scorm", ".zip");

			FileOutputStream fileOut = new FileOutputStream(tempFile);

			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = inputStream.read(buf)) > 0) {
				fileOut.write(buf, 0, len);
			}

			fileOut.close();
			inputStream.close();
		} catch (IOException ioe) {
			log.error("Caught an io exception trying to write byte array into temp file");
		}

		return tempFile;
	}

	private ContentPackageManifest createManifest(Document document, IValidator validator) {
		ContentPackageManifest manifest = new ContentPackageManifest();

		// Grab the launch data
		manifest.setLaunchData(validator.getLaunchData(false, false));

		Node firstOrg = document.getElementsByTagName("organization").item(0);
		// Build a new seq activity tree
		ISeqActivityTree prototype = ADLSeqUtilities.buildActivityTree(firstOrg, DOMTreeUtility.getNode(document, "sequencingCollection"));

		manifest.setActTreePrototype(prototype);

		return manifest;
	}

	protected abstract DataManagerDao dataManagerDao();

	public ContentPackage getContentPackage(long contentPackageId) {
		return contentPackageDao().load(contentPackageId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#getContentPackageByResourceId(java.lang.String)
	 */
	public ContentPackage getContentPackageByResourceId(String resourceId) {
		return contentPackageDao().loadByResourceId(resourceId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#getContentPackages()
	 */
	public List<ContentPackage> getContentPackages()
	{
		return getAllContentPackages( null );
	}

		/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#getContentPackages( java.lang.String )
	 */
	public List<ContentPackage> getContentPackages( String siteID )
	{
		return getAllContentPackages( siteID );
	}

	/**
	 * Private method to do the work; if site ID is not supplied, the current site is implied.
	 * 
	 * @param siteID
	 * @return 
	 */
	
	private List<ContentPackage> getAllContentPackages( String siteID )
	{
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.ScormContentServiceImpl getAllContentPackages. Site ID: " + siteID);
        }
		String context = StringUtils.isNotBlank( siteID ) ? siteID : lms().currentContext();
		List<ContentPackage> allPackages = contentPackageDao().find( context );
		List<ContentPackage> releasedPackages = new LinkedList<ContentPackage>();

		if( lms().canModify( siteID ) )
		{			
			return updateLearnerInfo(scormResultService(), allPackages);
		}

		for( ContentPackage contentPackage : allPackages )
		{
//            if (contentPackage.isReleased()) {
                releasedPackages.add(contentPackage);
//            }
		}
		
		return updateLearnerInfo(scormResultService(), releasedPackages);
	}
	
	private List<ContentPackage> updateLearnerInfo(ScormResultService resultService, List<ContentPackage> contentPackages){
		List<ContentPackage> result = new ArrayList<>();
		
		if(null != resultService 
				&& null != contentPackages && !contentPackages.isEmpty()){
			for( ContentPackage contentPackage : contentPackages )
			{
				int numOfAttem = resultService.countAttempts(contentPackage.getContentPackageId(), lms().currentLearnerId());
				if(numOfAttem > 0){					
					List<ActivitySummary> summaries = resultService.getActivitySummaries(contentPackage.getContentPackageId(), lms().currentLearnerId(),numOfAttem);
					if(null != summaries && !summaries.isEmpty()){
						ActivitySummary activitySummary = summaries.get(0);
						contentPackage.setLearningStatus(activitySummary.getCompletionStatus());
						contentPackage.setScore(activitySummary.getScaled());
					}
				}
				result.add(contentPackage);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#getContentPackageStatus(org.sakaiproject.scorm.model.api.ContentPackage)
	 */
	public int getContentPackageStatus(ContentPackage contentPackage) {
		int status = CONTENT_PACKAGE_STATUS_UNKNOWN;
		Date now = new Date();

		if (now.after(contentPackage.getReleaseOn())) {
			if (contentPackage.getDueOn() == null || contentPackage.getAcceptUntil() == null) {
				status = CONTENT_PACKAGE_STATUS_OPEN;
			} else if (now.before(contentPackage.getDueOn())) {
				status = CONTENT_PACKAGE_STATUS_OPEN;
			} else if (now.before(contentPackage.getAcceptUntil())) {
				status = CONTENT_PACKAGE_STATUS_OVERDUE;
			} else {
				status = CONTENT_PACKAGE_STATUS_CLOSED;
			}
		} else {
			status = CONTENT_PACKAGE_STATUS_NOTYETOPEN;
		}

		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#getContentPackageTitle(org.w3c.dom.Document)
	 */
	public String getContentPackageTitle(Document document) {
		String title = null;
		try {
			Node orgRoot = document.getElementsByTagName("organizations").item(0);
			String defaultId = DOMTreeUtility.getAttributeValue(orgRoot, "default");

			NodeList orgs = document.getElementsByTagName("organization");

			Node defaultNode = null;
			for (int i = 0; i < orgs.getLength(); ++i) {
				if (DOMTreeUtility.getAttributeValue(orgs.item(i), "identifier").equalsIgnoreCase(defaultId)) {
					defaultNode = orgs.item(i);
					break;
				}
			}
			List<Node> titleNodes = DOMTreeUtility.getNodes(defaultNode, "title");

			if (!titleNodes.isEmpty()) {
				title = DOMTreeUtility.getNodeValue(titleNodes.get(0));
			}

		} catch (Exception e) {
			log.warn("Caught an exception looking for content package title");
		}

		if (null == title) {
			title = "Unknown";
		}

		return title;
	}

	protected abstract LearnerDao learnerDao();

	protected abstract LearningManagementSystem lms();
	protected abstract ScormResultService scormResultService();
	protected abstract ServerConfigurationService serverConfigurationService();
	
	protected abstract SiteService siteService();

	public void removeContentPackage(long contentPackageId) throws ResourceNotDeletedException {
		LearningManagementSystem lms = lms();
		if (lms.canDelete(lms.currentContext())) {
			ContentPackage contentPackage = contentPackageDao().load(contentPackageId);

			contentPackageDao().remove(contentPackage);

			resourceService().removeResources(contentPackage.getResourceId());
		}

	}

	// Dependency injected lookup methods
	protected abstract ScormResourceService resourceService();

	// Helper methods

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#updateContentPackage(org.sakaiproject.scorm.model.api.ContentPackage)
	 */
	public void updateContentPackage(ContentPackage contentPackage) {
		String learnerId = lms().currentLearnerId();

		if (learnerId == null) {
			learnerId = "unknown";
		}

		contentPackage.setModifiedBy(learnerId);
		contentPackage.setModifiedOn(new Date());

		contentPackageDao().save(contentPackage);
	}

	public IValidator validate(File contentPackage, boolean iManifestOnly, boolean iValidateToSchema, String encoding) {
		String directoryPath = contentPackage.getParent();
		IValidator validator = new CPValidator(directoryPath);
		((CPValidator) validator).setSchemaLocation(directoryPath);
		validator.setPerformValidationToSchema(iValidateToSchema);
		validator.validate(contentPackage.getPath(), "pif", "contentaggregation", iManifestOnly, encoding);

		return validator;
	}

	public int validate(String resourceId, String encoding) throws ResourceStorageException {
	    return validate(resourceId, false, true, encoding, false);
	}
	
	public int storeAndValidate(String resourceId, boolean isValidateToSchema, String encoding) throws ResourceStorageException {
		return validate(resourceId, false, isValidateToSchema, encoding, true);
	}

	
	/* (non-Javadoc)
	 * @see org.sakaiproject.scorm.service.api.ScormContentService#validate(java.lang.String, boolean, boolean, java.lang.String)
	 */
	public int validate(String resourceId, boolean isManifestOnly, boolean isValidateToSchema, String encoding, boolean createContentPackage) throws ResourceStorageException {
		File file = createFile(resourceService().getArchiveStream(resourceId));

		int result = VALIDATION_SUCCESS;

		if (!file.exists())
			return VALIDATION_NOFILE;

		IValidator validator = validate(file, isManifestOnly, isValidateToSchema, encoding);
		IValidatorOutcome validatorOutcome = validator.getADLValidatorOutcome();

		if (!validatorOutcome.getDoesIMSManifestExist())
			return VALIDATION_NOMANIFEST;

		if (!validatorOutcome.getIsWellformed()) {
			result = VALIDATION_NOTWELLFORMED;
		}

		if (!validatorOutcome.getIsValidRoot()) {
			result = VALIDATION_NOTVALIDROOT;
		}

		if (isValidateToSchema) {
			if (!validatorOutcome.getIsValidToSchema()) {
				result = VALIDATION_NOTVALIDSCHEMA;
			}

			if (!validatorOutcome.getIsValidToApplicationProfile()) {
				result = VALIDATION_NOTVALIDPROFILE;
			}

			if (!validatorOutcome.getDoRequiredCPFilesExist()) {
				result = VALIDATION_MISSINGREQUIREDFILES;
			}
		}

		if (createContentPackage) {
			try {
				convertToContentPackage(resourceId, validator, validatorOutcome);
			} catch (InvalidArchiveException iae) {
				return VALIDATION_WRONGMIMETYPE;
			} catch (Exception e) {
				log.error("Failed to convert content package for resourceId: " + resourceId, e);
				return VALIDATION_CONVERTFAILED;
			}
		}

		return result;
	}
	// Hoctdy add start
	public int getMaxViewOrderContentPackages(String title){
		String context = lms().currentContext();
		int packageCount = contentPackageDao().countContentPackages(context, title);
		return packageCount;
	}
	// Hoctdy add end

	/*
	 * private File createFile(byte[] bytes) { File tempFile = null; try {
	 * tempFile = File.createTempFile("scorm", ".zip");
	 * 
	 * FileOutputStream fileOut = new FileOutputStream(tempFile);
	 * 
	 * fileOut.write(bytes);
	 * 
	 * fileOut.close(); } catch (IOException ioe) {
	 * log.error("Caught an io exception trying to write byte array into temp file"
	 * ); }
	 * 
	 * return tempFile; }
	 * 
	 * private byte[] getFileAsBytes(File file) { int len = 0; byte[] buf = new
	 * byte[1024]; ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	 * 
	 * try { FileInputStream fileIn = new FileInputStream(file); while ((len =
	 * fileIn.read(buf)) > 0) { byteOut.write(buf,0,len); }
	 * 
	 * fileIn.close(); } catch (IOException ioe) {
	 * log.error("Caught an io exception trying to write file into byte array!",
	 * ioe); }
	 * 
	 * return byteOut.toByteArray(); }
	 */
	
	public String getToolSiteUrl(){
		Site site = null;
		try {
		    site = siteService().getSite(ToolManager.getCurrentPlacement().getContext());
		} catch (Exception impossible) {
		    return null;
		}
		ToolConfiguration tool = site.getToolForCommonId("sakai.scorm.tool");
		
		// LSNBLDR-21. If the tool is not in the current site we shouldn't return a url
		if(tool == null) {
		    return null;
		}
		
		String placement = tool.getId();
		
		return serverConfigurationService().getPortalUrl()+"/site/" + site.getId()+ "/tool" + "/" + placement;
	}

}
