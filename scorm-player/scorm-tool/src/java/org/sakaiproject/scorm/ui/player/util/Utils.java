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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.adl.datamodels.IDataManager;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.util.string.Strings;

public final class Utils {
	
	public static final String COMPLETED_STATUS = "Completed";
	public static final String IMCOMPLETE_STATUS = "Incomplete";

	public final static String generateUrl(IBehavior behavior, RequestListenerInterface rlix, 
			Component component, boolean isRelative) {
		
		if (component == null)
			throw new IllegalArgumentException("Behavior must be bound to a component to create the URL");
		
		final RequestListenerInterface rli = IBehaviorListener.INTERFACE;
		
		String relativePagePath = component.urlFor(behavior, rli).toString();
		
		String url = null;
		
		if (!isRelative) {
			WebRequest webRequest = (WebRequest)component.getRequest();
			HttpServletRequest servletRequest = webRequest.getHttpServletRequest();
			//url.append(servletRequest.getContextPath()).append("/");
			//String requestUrl = servletRequest.getRequestURL().toString();
			//url = RequestUtils.toAbsolutePath(requestUrl, relativePagePath);
			String contextPath = servletRequest.getContextPath();
			String relativePath = relativePagePath.replaceAll("\\.\\.\\/", "");
			url = new StringBuilder(contextPath).append("/").append(relativePath).toString();
		} else {
			url = relativePagePath;
		}
		
		return url;
	}
	
	
	static String removeDoubleDots(String path)
	{
		List newcomponents = new ArrayList(Arrays.asList(path.split("/")));

		for (int i = 0; i < newcomponents.size(); i++)
		{
			if (i < newcomponents.size() - 1)
			{
				// Verify for a ".." component at next iteration
				if (((String)newcomponents.get(i)).length() > 0 &&
					newcomponents.get(i + 1).equals(".."))
				{
					newcomponents.remove(i);
					newcomponents.remove(i);
					i = i - 2;
					if (i < -1)
						i = -1;
				}
			}
		}
		String newpath = Strings.join("/", (String[])newcomponents.toArray(new String[0]));
		if (path.endsWith("/"))
			return newpath + "/";
		return newpath;
	}
	
	public static String getPercentageString(double d) {
		
		String percentage = "Not available";
		if(d > 0) {
			double p = d * 100.0;			
			long result = Math.round(p);			
			percentage = "" + result + " %";
		}
		return percentage;
	}
	public static String getNumberPercententString(double d) {
		
		String percentage = "Not available";
		if(d > 0) {
			double p = d * 100.0;			
			long result = Math.round(p);			
			percentage = "" + result + "";
		}
		return percentage;
	}
	
	public static String getValueAsString(String element) {
		String result = element;

		if (StringUtils.isBlank(result) || result.equals("unknown"))
			return "Not available";

		return result;
	}
	
}
