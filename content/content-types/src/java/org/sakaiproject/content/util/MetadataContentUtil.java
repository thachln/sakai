/**
 * Copyright (c) 2003-2016 The Apereo Foundation
 *
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
 */
package org.sakaiproject.content.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.util.Resource;
import org.sakaiproject.util.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataContentUtil {
	public static final String MT_STREAMING = "streaming";
    public static final String MT_TINCAN = "tincan";
    private static final String MT_UNDEFINED = null;

    private static final String KEY_LAUNCH_PAGE = "launchPage";
    private static final String KEY_ACTIVITY_ID = "activityId";
    private static final String KEY_NAME = "name";
    private static final Object KEY_URL = "url";
    private static final String KEY_RESOURCE_TYPE = "resourceType";
    private static final Object KEY_LIST_SUBTITLE_URL = "listSubtitleUrl";
    
    private static final String EXCEL_EXTENSION = ".xlsx";
	private static final int BUFFER_SIZE = 32000;
	private static final MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
	public static final String PREFIX = "resources.";
	public static final String REQUEST = "request.";
	private static final String STATE_HOME_COLLECTION_ID = PREFIX + REQUEST + "collection_home";
	private static final String STATE_HOME_COLLECTION_DISPLAY_NAME = PREFIX + REQUEST + "collection_home_display_name";
	public static final String STATE_MESSAGE = "message";
    
    private static final String DEFAULT_RESOURCECLASS = "org.sakaiproject.localization.util.ContentProperties";
    private static final String DEFAULT_RESOURCEBUNDLE = "org.sakaiproject.localization.bundle.content.content";
    private static final String RESOURCECLASS = "resource.class.content";
    private static final String RESOURCEBUNDLE = "resource.bundle.content";
	private static ResourceLoader rb = new Resource().getLoader(ServerConfigurationService.getString(RESOURCECLASS, DEFAULT_RESOURCECLASS), ServerConfigurationService.getString(RESOURCEBUNDLE, DEFAULT_RESOURCEBUNDLE));

	
    /**
     * Export all link of media in the folder to Excel file.
     * @author Le Ngoc Thach
     * @param reference
     */
    public void exportMetadataFolder(Reference reference) { 
        File temp = null;
        
        // Workbook to export data
        Workbook wb = null;
        Sheet metadataSheet = null;
        // Template to export metadata
        String templateMetadata = ServerConfigurationService.getString("content.metadata.template", "${sakai.home}/content/Template_Metadata.xlsx");
        
        FileInputStream fis = null;
        
        ToolSession toolSession = SessionManager.getCurrentToolSession();
        try {
            // Create the compressed archive in the filesystem

            OutputStream os = null;
            try {
                try {
                    // wb = PoiUtil.loadWorkbookByResource(templateMetadata);
                    wb = new XSSFWorkbook(new File(templateMetadata));
                    metadataSheet = wb.getSheetAt(0);
                } catch (Exception ex) {
                    log.warn(String.format("Could not load template at '%s'. Create new file.", ex));
                    wb = new XSSFWorkbook();
                    metadataSheet = wb.createSheet("Data");
                }

                ContentCollection collection = ContentHostingService.getCollection(reference.getId());

                // Check to process in case the selected folder is the Tincan package for the Video streaming .m3u8

                ContentResource crTincan = getTincanResource(collection);

                if (crTincan != null) {
                    storeContentMetadataResource(reference.getId(), crTincan, MT_TINCAN, metadataSheet);
                } else {
                    // Check the resource is folder of streaming video with .m3u8 format
                    ContentResource crStreaming = getTincanResource(collection);
                    
                    if (crStreaming != null) {
                        storeContentMetadataResource(reference.getId(), crStreaming, MT_STREAMING, metadataSheet);
                    } else {
                        // Write content into the sheet "metadataSheet"
                        storeContentMetadataCollection(reference.getId(), collection, metadataSheet);
                    }
                }

                temp = File.createTempFile("sakai_metadata-", ".xlsx");
                os = new BufferedOutputStream(new FileOutputStream(temp), BUFFER_SIZE);
                wb.write(os);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                    }
                }
            }
            
            
            // Store the compressed archive in the repository
            String resourceId = reference.getId().substring(0,reference.getId().lastIndexOf(Entity.SEPARATOR));
            String resourceName = extractName(resourceId);          
            String homeCollectionId = (String) toolSession.getAttribute(STATE_HOME_COLLECTION_ID);
            if (homeCollectionId != null && homeCollectionId.equals(reference.getId())) {
                //place the zip file into the home folder of the resource tool
                resourceId = reference.getId() + resourceName;
                
                String homeName = (String) toolSession.getAttribute(STATE_HOME_COLLECTION_DISPLAY_NAME);
                if (homeName != null) {
                    resourceName = homeName;
                }               
            }
            int count = 0;
            String displayName="";
            

            // Write workbook of metadata into file
            ContentResourceEdit resourceEditMetadata = null;
            while (true) {
                try{
                    String newResourceId = resourceId;
                    String newResourceName = resourceName;
                    displayName = newResourceName;
                    count++;
                    if (count > 1) {
                        //previous naming convention failed, try another one
                        newResourceId += "_" + count;
                        newResourceName += "_" + count;
                    }
                    newResourceId += "_Metadata" + EXCEL_EXTENSION;
                    newResourceName += "_Metadata" + EXCEL_EXTENSION;
                    ContentCollectionEdit currentEdit;
                    if (reference.getId().split(Entity.SEPARATOR).length > 3) {
                        currentEdit = (ContentCollectionEdit) ContentHostingService.getCollection(resourceId + Entity.SEPARATOR);
                        displayName = currentEdit.getProperties().getProperty(ResourcePropertiesEdit.PROP_DISPLAY_NAME);

                        if (displayName != null && displayName.length() > 0) {
                            displayName += "_Metadata" + EXCEL_EXTENSION;
                        } else {
                            displayName = newResourceName;
                        }
                    }
                    resourceEditMetadata = ContentHostingService.addResource(newResourceId);
                    //success, so keep track of name/id
                    resourceId = newResourceId;
                    resourceName = newResourceName;
                    break;
                } catch (IdUsedException e) {
                    // do nothing, just let it loop again
                } catch (Exception e) {
                    throw new Exception(e);
                }
            }
            



            fis = new FileInputStream(temp);
            resourceEditMetadata.setContent(fis);
            resourceEditMetadata.setContentType(mime.getContentType(resourceId));
            ResourcePropertiesEdit props = resourceEditMetadata.getPropertiesEdit();

            props.addProperty(ResourcePropertiesEdit.PROP_DISPLAY_NAME, displayName);
            ContentHostingService.commitResource(resourceEditMetadata, NotificationService.NOTI_NONE);
        }
        catch (PermissionException pE){
            addAlert(toolSession, rb.getString("permission_error_zip"));
            log.warn(pE.getMessage(), pE);
        }
        catch (Exception e) {
            addAlert(toolSession, rb.getString("generic_error_zip"));
            log.error(e.getMessage(), e);
        } 
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (temp != null && temp.exists()) { 
                if (!temp.delete()) {
                    log.warn("failed to remove temp file");
                }
            }
        }

    }

	private void addAlert(ToolSession toolSession, String alert){
		String errorMessage = (String) toolSession.getAttribute(STATE_MESSAGE);
		if(errorMessage == null){
			errorMessage = alert;
		}else{
			errorMessage += "\n\n" + alert;
		}
		toolSession.setAttribute(STATE_MESSAGE, errorMessage);
	}

	/**
	 * Exports a the ContentResource zip file to the operating system
	 * 
	 * @param resource
	 * @return
	 */
	private File exportResourceToFile(ContentResource resource) {
		File temp = null;
		FileOutputStream out = null;
		try {
			temp = File.createTempFile("sakai_content-", ".tmp");

			temp.deleteOnExit();

			// Write content to file 
			out = new FileOutputStream(temp);
			IOUtils.copy(resource.streamContent(),out);
			out.flush();
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (ServerOverloadException e) {
			log.error(e.getMessage(), e);
		}
		finally {
			if (out !=null) {
				try {
					out.close();
				} catch (IOException e) {
					
				}
			}
		}
        return temp;
	}

    /**
     * Iterates the collection.getMembers() and streams content resources recursively to the ZipOutputStream
     * @author Le Ngoc Thach
     * @param rootId
     * @param collection
     * @param out
     * @throws Exception
     */
    private void storeContentMetadataCollection(String rootId, ContentCollection collection, Sheet metadataSheet) throws Exception {
        List<String> members = collection.getMembers();
        
        if (members != null) {
            for (String memberId: members) {
                
                // Folder resources
                if (memberId.endsWith(Entity.SEPARATOR)) {
                    ContentCollection memberCollection = ContentHostingService.getCollection(memberId);
                    ContentResource resource = getTincanResource(memberCollection);

                    Properties props = detectResource(memberCollection);
                    if (props != null) {
                        String resourceType = (String) props.get(KEY_RESOURCE_TYPE);
                        if (MT_TINCAN.equals(resourceType)) {
                            storeContentMetadataResource(rootId, resource, MT_TINCAN, metadataSheet);
                        } if (MT_STREAMING.equals(resourceType)) {
                            storeContentMetadataResource(rootId, resource, MT_STREAMING, metadataSheet);
                        } else {
                            // Recursively calling
                            storeContentMetadataCollection(rootId, memberCollection, metadataSheet);
                        }
                    } else {
                        // Recursively calling
                        storeContentMetadataCollection(rootId, memberCollection, metadataSheet);
                    }
                } else { // File resources
                    log.debug("rootId=" + rootId + ";memberId=" + memberId);
                    ContentResource resource = ContentHostingService.getResource(memberId);
                    
                    // Debug to check the folder of xAPI package
//                    if (memberId.endsWith("tincan.xml")) {
//                        log.debug("Get content of file tincan.xml");
//                        ContentResource tinCanResource = ContentHostingService.getResource(memberId);
//                        String activityId = getActivityId(tinCanResource);
//                        log.debug("activityId=" + activityId);
//                    }
                    
                    storeContentMetadataResource(rootId, resource, MT_UNDEFINED, metadataSheet);
                }
            }
        }
    }

    /**
     * Check folder is a Tincan package or not?
     * @param collection
     * @return ContentResource of file tincan.xml
     * @throws TypeException 
     * @throws IdUnusedException 
     * @throws PermissionException 
     */
    private ContentResource getTincanResource(ContentCollection collection) throws PermissionException, IdUnusedException, TypeException {
        List<String> members = collection.getMembers();
        
        if (members != null) {
            for (String memberId: members) {

            }
        }
        
        return null;
    }
    
    private Properties detectResource(ContentCollection collection) throws PermissionException, IdUnusedException, TypeException {
        Properties props = new Properties();
        List<String> members = collection.getMembers();
        
        if (members != null) {
            for (String memberId: members) {
                if (MT_TINCAN.equals(getMediaType(memberId))) {
                    props.put(KEY_RESOURCE_TYPE, MT_TINCAN);
                    
                    // Get properties of Tincan package
                    props.putAll(getTincanProperties(ContentHostingService.getResource(memberId)));
                }

                if (MT_STREAMING.equals(getMediaType(memberId))) {
                    props.put(KEY_RESOURCE_TYPE, MT_STREAMING);
                    
                    // Get properties of Stream package
                    props.putAll(getStreamProperties(collection, ContentHostingService.getResource(memberId), memberId));
                }
            }
        }
        
        return null;
    }

    /**
     * Get some properties of tincan package: id, launch, name.
     * @param tinCanResource
     * @return
     */
    private Properties getTincanProperties(ContentResource tinCanResource) {
        Properties props = new Properties();
        byte[] xmlContent;
        InputStream bis = null;

        try {
            xmlContent = tinCanResource.getContent();
            bis = new ByteArrayInputStream(xmlContent);
            Document doc = parseXML(bis);
            
            String activityId = getStringValue(doc, "/tincan/activities/activity[@type = 'http://adlnet.gov/expapi/activities/course']/@id");
            String launchPage = getStringValue(doc, "/tincan/activities/activity[@type = 'http://adlnet.gov/expapi/activities/course']/launch/text()");
            String name = getStringValue(doc, "/tincan/activities/activity[@type = 'http://adlnet.gov/expapi/activities/course']/name/text()");
            
            if (activityId != null) {
                props.put(KEY_ACTIVITY_ID, activityId);
            }
            
            if (launchPage != null) {
                props.put(KEY_LAUNCH_PAGE, launchPage);
            }
            
            if (name != null) {
                props.put(KEY_NAME, name);
            }
        } catch (ServerOverloadException e) {
            log.error("Could not get content of the resource: " + tinCanResource.getUrl());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }


        return props;
    }
    
    /**
     * [Give the description for method].
     * @param streamResource Content of folder contains the file .m3u8
     * @param streamResource Resource of the file .m3u8
     * @param m3u8Filename filename of file .m3u8
     * @return
     * @throws TypeException 
     * @throws IdUnusedException 
     * @throws PermissionException 
     */
    private Properties getStreamProperties(ContentCollection collection, ContentResource resource, String m3u8Filename) throws PermissionException, IdUnusedException, TypeException {
        Properties props = new Properties();
        List<String> subTitleUrls = new ArrayList<String>();

        List<String> members = collection.getMembers();
        
        // Get Name of video streaming
        int idxDot = m3u8Filename.indexOf(".m3u8");
        String name = (idxDot > 0) ? m3u8Filename.substring(0, idxDot) : m3u8Filename;
        
        props.put(KEY_NAME, name);
        props.put(KEY_URL, resource.getUrl());
        
        if (members != null) {
            ContentResource tmpResource;

            for (String memberId: members) {
                
                // Folder resources
                if (memberId.endsWith(".vtt")) {
                    tmpResource = ContentHostingService.getResource(memberId);
                    subTitleUrls.add(tmpResource.getUrl());
                }
            }
            
            if (subTitleUrls.size() > 0) {
                props.put(KEY_LIST_SUBTITLE_URL, subTitleUrls);
            }
        }

        
        return props;
    }
    
    /**
     * method getStringValue evaluate the compiled XPath expression in the specified context 
     * and return the result as String.
     * @param path href of resource 
     */
    private String getStringValue(Document doc, final String path) {
        String strValue = null;
        XPath xp = XPathFactory.newInstance().newXPath();

        try {
            XPathExpression xpe = xp.compile(path);
            strValue = (String) xpe.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("Could not get value of path '" + path + "'", ex);
        }

        return strValue;
    }

    /**
     * Parse a XML File into a document model.
     * @param xmlFile file of xml.
     * @return Document model if no error.
     */
    private Document parseXML(InputStream is) {
        Document xmlDoc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
            xmlDoc = db.parse(is);
            
        } catch (ParserConfigurationException ex) {
            log.error("Could not parse the file.", ex);
        } catch (SAXException ex) {
            log.error("Error in XML file.", ex);
        } catch (IOException ex) {
            log.error("Could not open file.", ex);
        }
        
        return xmlDoc;
    }

    /**
     * Streams content resource to the media metadata file.
     * @param rootId
     * @param resource
     * @param suggestedMediaType MT_TINCAN | MT_STREAM | null
     * @param out
     * @throws Exception
     */
    private void storeContentMetadataResource(String rootId, ContentResource resource, String suggestedMediaType, Sheet metadataSheet) throws Exception {      
        String filename = resource.getId().substring(rootId.length(), resource.getId().length());

        //Inorder to have username as the folder name rather than having eids
        if (ContentHostingService.isInDropbox(rootId) && ServerConfigurationService.getBoolean("dropbox.zip.haveDisplayname", true)) {
            try {
                filename = getContainingFolderDisplayName(rootId, filename);
            } catch(Exception e){
                log.warn("Unexpected error occurred when trying to scan media resources:" + extractName(rootId), e);
                return;
            }
        }
        // Get media type from filename
        String mediaType = (suggestedMediaType == null) ? getMediaType(filename) : suggestedMediaType;
        
        if (mediaType != null) {
            int rowIdx = metadataSheet.getLastRowNum() + 1;
            
            // Set No
            setContent(metadataSheet, rowIdx, 0, String.format("%04d", rowIdx));
            
            // Set GROUP NAME
            String[] groupNames = extractGroupName(rootId);
            String[] groupNamesFromFilename = (MT_TINCAN.equals(mediaType) || MT_STREAMING.equals(mediaType)) ? filename.split("/") : null;
            
            int nGroup = (groupNames != null) ? groupNames.length : 0;
            for (int i = 0; i < nGroup; i++) {
                setContent(metadataSheet, rowIdx, 1 + i, groupNames[i]);
            }
            
            // Append the group from filename
            int len = (groupNamesFromFilename != null) ? groupNamesFromFilename.length : 0;
            for (int i = 0; i < len - 1; i++) {
                nGroup++;
                setContent(metadataSheet, rowIdx, nGroup, groupNamesFromFilename[i]);
            }
            
            if ((len > 0) && MT_STREAMING.equals(mediaType)) {
                filename = groupNamesFromFilename[len - 1];
            }

            // SET KEY
            setContent(metadataSheet, rowIdx, 1 + nGroup, String.format("%04d", rowIdx));
            
            // Reserve 2 + nGroup for Name
            
            // Media Type: Video|Streaming|Tincan...
            setContent(metadataSheet, rowIdx, 3 + nGroup, mediaType);
            
            
            // SET ACTIVITY ID
            if (MT_TINCAN.equals(mediaType)) {
                // Update
                Properties props = getTincanProperties(resource);
                
                if (props.containsKey(KEY_ACTIVITY_ID)) {
                    setContent(metadataSheet, rowIdx, 4 + nGroup, props.get(KEY_ACTIVITY_ID));
                }
                
                // SET URL
                if (props.containsKey(KEY_LAUNCH_PAGE)) {
                    // resource.getUrl(): .../tincan.xml
                    String url = resource.getUrl();
                    
                    // Remove end text "tincan.xml".
                    int lenRoot = url.length() - "tincan.xml".length();
                    
                    url = url.substring(0, lenRoot) + props.get(KEY_LAUNCH_PAGE);
                    setContent(metadataSheet, rowIdx, 5 + nGroup, url);
                }
                
                // Set Name
                if (props.containsKey(KEY_NAME)) {
                    String name = (String) props.get(KEY_NAME);
                    setContent(metadataSheet, rowIdx, 2 + nGroup, name);
                }
            } else if (MT_STREAMING.equals(mediaType)) {
                // Streaming m3u8
                Properties props = getStreamProperties(resource.getContainingCollection(), resource, filename);
                
                // Set Name
                if (props.containsKey(KEY_NAME)) {
                    String name = (String) props.get(KEY_NAME);
                    setContent(metadataSheet, rowIdx, 2 + nGroup, name);
                }

                // SET URL
                if (props.containsKey(KEY_URL)) {
                    String url = (String) props.get(KEY_URL);
                    setContent(metadataSheet, rowIdx, 5 + nGroup, url);
                }

                // Set Subtitle links
                if (props.containsKey(KEY_LIST_SUBTITLE_URL)) {
                    List<String> subtitleUrls = (List<String>) props.get(KEY_LIST_SUBTITLE_URL);
                    
                    if (subtitleUrls != null) {
                        int i = 0;
                        for (String url : subtitleUrls) {
                            setContent(metadataSheet, rowIdx, 10 + nGroup + i, url);
                            i++;
                        }
                    }
                    
                }
            } else if (mediaType != null) {
                // SET NAME
                setContent(metadataSheet, rowIdx, 2 + nGroup, filename);

                // Check previous data, if the previous is tincan
                setContent(metadataSheet, rowIdx, 5  + nGroup, resource.getUrl());
            }
            
        } else {
            // Skip the file
        }
        
    }
    
    private String getMediaType(String collectionName) {
        
        if (collectionName.endsWith(".m3u8")) {
            return MT_STREAMING;
        } else if (collectionName.endsWith(".mp4") || collectionName.endsWith(".avi")) {
            return "video";
        } else if (collectionName.endsWith(".pdf")) {
            return "pdf";
        } else if (collectionName.endsWith(".txt") || collectionName.endsWith(".html")) {
            return "text";
        } else if (collectionName.endsWith("tincan.xml")) {
           return MT_TINCAN;
        } else if (collectionName.contains("youtu.be")) {
            return "youtube"; 
        }else {
            return null;
        }
    }

    /**
     * Parse string of parent folder.<br/>
     * 
     * @param rootId
     * @return
     */
    private String[] extractGroupName(String rootId) {
        String[] groupNames = null;
        
        log.debug("rootId=" + rootId);

        // Remove the last character /
        if (rootId.endsWith("/") && (rootId.length() > 1)) {
            String parentName = null;
            // root Id starts with "/"
            parentName = rootId.substring(1, rootId.length() - 1);

            log.debug("parentName=" + parentName);

//            int lastSeparatorIdx = parentName.lastIndexOf('/');
//            parentName = parentName.substring(lastSeparatorIdx);
            // Get list of Group
            String[] allGroupNames = parentName.split("/");

            // Skip 2 first elements: "group", group code
            int len = (allGroupNames != null) ? allGroupNames.length : 0;
            
            if (len > 2) {
                groupNames = new String[len - 2];
                for (int i = 2; i < len; i++) {
                    groupNames[i - 2] = allGroupNames[i];
                }
            } else {
                groupNames = allGroupNames;
            }
            
            log.debug("parentName=" + parentName);
        } else {
            log.warn("Could not get parent folder name from resourse: " + rootId);
            groupNames = new String[] {"Unknown-name"};
        }

        return groupNames;
    }

	private String extractName(String collectionName) {
		String[] tmp = collectionName.split(Entity.SEPARATOR);
		return tmp[tmp.length-1];
	}
	
    /**
     * Set value into location.
     * @param sheet
     * @param rowIdx
     * @param colIdx
     * @param value
     * @return
     */
    public static Cell setContent(Sheet sheet, int rowIdx, int colIdx, Object value) {
        Row row = sheet.getRow(rowIdx);

        // Build Row and Cell
        if (row == null) {
            row = sheet.createRow(rowIdx);
        }
        Cell cell = row.getCell(colIdx);

        if (cell == null) {
            cell = row.createCell(colIdx);
        }

        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Double) {
            cell.setCellValue(((Double) value).doubleValue());
        } else if (value instanceof Integer) {
            cell.setCellValue(((Integer) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof BigInteger) {
            cell.setCellValue(((BigInteger) value).longValue());
        } else if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            // Remove content of value
            cell.setCellType(CellType.BLANK);
        }

        return cell;
    }
    /**
     * Add an empty folder to the zip
     * 
     * @param rootId
     * @param resource
     * @param out
     * @throws Exception
     */
    private void storeEmptyFolder(String rootId, ContentCollection resource, ZipOutputStream out) throws Exception {
        String folderName = resource.getId().substring(rootId.length(),resource.getId().length());
        if(ContentHostingService.isInDropbox(rootId) && ServerConfigurationService.getBoolean("dropbox.zip.haveDisplayname", true)) {
            try {
                folderName = getContainingFolderDisplayName(rootId, folderName);
            } catch (Exception e) {
                log.warn("Unexpected error when trying to create empty folder for Zip archive {} : {}", extractName(rootId), e.getMessage());
                return;
            }
        }
        ZipEntry zipEntry = new ZipEntry(folderName);
        out.putNextEntry(zipEntry);
        out.closeEntry();
    }

	private String getContainingFolderDisplayName(String rootId,String filename) throws IdUnusedException, TypeException, PermissionException {
		//dont manipulate filename when you are a zip file from a root folder level
		if (!(rootId.split("/").length > 3) && (filename.split("/").length<2) && filename.endsWith(".zip")) {
			return filename;
		}

		String filenameArr[] = filename.split(Entity.SEPARATOR);

		//return rootId when you you zip from sub folder level and gives something like "group-user/site-id/user-id/" when zipping from root folder level by using filenameArr
		String contentEditStr = (rootId.split("/").length > 3) ? rootId : rootId+filenameArr[0] + Entity.SEPARATOR;
		ContentCollectionEdit collectionEdit = (ContentCollectionEdit) ContentHostingService.getCollection(contentEditStr);
		ResourcePropertiesEdit props = collectionEdit.getPropertiesEdit();
		String displayName = props.getProperty(ResourcePropertiesEdit.PROP_DISPLAY_NAME);

		//returns displayname along with the filename for zipping from sub folder level
        if (contentEditStr.equals(rootId)) {
            return displayName + Entity.SEPARATOR + filename;
        } else { // just replaces the user-id with the displayname and returns the filename
            return filename.replaceFirst(filenameArr[0], displayName);
        }

	}

}
