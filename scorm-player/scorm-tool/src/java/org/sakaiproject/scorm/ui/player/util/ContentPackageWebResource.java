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
package org.sakaiproject.scorm.ui.player.util;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.watch.IModifiable;
import org.sakaiproject.scorm.model.api.ContentPackageResource;

public class ContentPackageWebResource extends WebResource implements IModifiable {
	
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(ContentPackageWebResource.class);
	 
	private static final String[] candidateCompressionContentTypes = { "text/html", "text/javascript", "text/css"  };
	
	private ContentPackageResource resource;
	private ContentPackageResourceStream resourceStream;

    // private Request request;
	
	public ContentPackageWebResource(ContentPackageResource resource) {
		setCacheable(true);
		this.resource = resource;
		this.resourceStream = new ContentPackageResourceStream(resource);
	}
	
//	public ContentPackageWebResource(ContentPackageResource cpResource, Request request) {
//        setCacheable(true);
//        this.resource = resource;
//        this.resourceStream = new ContentPackageResourceStream(resource);
//        this.request = request;
//    }
//
//    @Override
	public IResourceStream getResourceStream() {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.getResourceStream in ContentPackageWebResource");
        }
		if (canCompress()) {
			return new CompressingContentPackageResourceStream(resource);
		}
		
		return resourceStream;
	}
	
	@Override
	protected void setHeaders(WebResponse response) {
		super.setHeaders(response);
		if (canCompress()) {
			response.setHeader("Content-Encoding", "gzip");
		}
		
      // Thach
//		long lastModified = resourceStream.lastModifiedTime().getMilliseconds();
//        long length = resourceStream.length();
//        String resourceName = resource.getPath(); // Todo:
//        String fileName = resourceName.substring(resourceName.lastIndexOf("/") + 1, resourceName.length());
//        log.debug("ThachLN: File name: " + fileName);
//        
//		String eTag = fileName + "_" + length  + "_" + lastModified;
		
        // Prepare some variables for get output stream of the response
//      String ifNoneMatch = request.getHeader("If-None-Match");
//      long ifModifiedSince = request.getDateHeader("If-Modified-Since");
//      String ifMatch = request.getHeader("If-Match");
		
//      String ifNoneMatch = request.getParameter("If-None-Match");
//      Long ifModifiedSince = (Long) request.getParameter("If-Modified-Since");
//      String ifMatch = request.getParameter("If-Match");
      
        /** If-None-Match header should contain "*" or ETag. If so, then return 304. */
//      if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
//          response.setHeader("ETag", eTag); // Required in 304.
////          response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
//
//          return; // Stop write content
//      }
	}
	
    /**
     * Returns true if the given match header matches the given value.
     * @param matchHeader The match header.
     * @param toMatch The value to be matched.
     * @return True if the given match header matches the given value.
     */
//    private static boolean matches(final String matchHeader, final String toMatch) {
//        String[] matchValues = matchHeader.split("\\s*,\\s*");
//        Arrays.sort(matchValues);
//
//        return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
//    }

	private boolean canCompress() {
		return isCandidateForCompression() && supportsCompression();	
	}
	
	private boolean isCandidateForCompression() {
		String contentType = resourceStream.getContentType();
		
		if (contentType != null)
			for (int i=0;i<candidateCompressionContentTypes.length;i++) 
				if (contentType.startsWith(candidateCompressionContentTypes[i]))
					return true;
		
		return false;
	}
	
	private boolean supportsCompression() {
		if (Application.get().getResourceSettings().getDisableGZipCompression())
		{
			return false;
		}
		if (RequestCycle.get() == null)
			return false;
		
		WebRequest request = (WebRequest)RequestCycle.get().getRequest();
		String s = request.getHttpServletRequest().getHeader("Accept-Encoding");
		if (s == null)
		{
			return false;
		}
		else
		{
			return s.indexOf("gzip") >= 0;
		}
	}

	public Time lastModifiedTime() {
	    return resourceStream.lastModifiedTime();
    }
}
