/**********************************************************************************
 * $URL: $
 * $Id: $
 **********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;

import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageLogEntry;
import org.sakaiproject.lessonbuildertool.TrackProgress;
import org.sakaiproject.lessonbuildertool.TrackProgressImpl;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.FormattedText;

/**
 * <p>
 * Ajax server
 * </p>
 * .../lessonbuilder-tool/ajax?op=getmessage&code=simplepage.new-page&locale=fr
 * look up text messages in messages.properties. "" if can't find it this lets
 * us avoid passing the text of some larger messages as text on every HTML page.
 * The server doesn't have enough context to know the right locale for itself,
 * but the UI should know it
 * .../lessonbuilder-tool/ajax?op=getmimetype&url=http://www.rutgers.edu check a
 * URL to see what MIME type it is, "" if can't find it though the URL should
 * actually be urlencoded
 * 
 * @author Charles Hedrick, Rutgers University.
 * @version $Revision: $
 */
@SuppressWarnings({ "serial", "deprecation" })
@Slf4j
public class AjaxServer extends HttpServlet {
	private static final String UTF8 = "UTF-8";

	private static MessageSource messageSource;
	private static SiteService siteService;
	private static AuthzGroupService authzGroupService;
	private static SimplePageToolDao simplePageToolDao;
	private static LessonsAccess lessonsAccess;
	private static LessonBuilderAccessService lessonBuilderAccessService;

	public void setSimplePageToolDao(Object dao) {
		log.info("setdao " + dao);
		simplePageToolDao = (SimplePageToolDao) dao;
	}

	public static final String FILTERHTML = "lessonbuilder.filterhtml";
	private static String filterHtml = ServerConfigurationService.getString(FILTERHTML);

	static Class levelClass = null;
	static Object[] levels = null;
	static Class ftClass = null;
	static Method ftMethod = null;
	static Object ftInstance = setupFtStuff();

	static Object setupFtStuff() {
		Object ret = null;
		try {
			levelClass = Class.forName("org.sakaiproject.util.api.FormattedText$Level");
			levels = levelClass.getEnumConstants();
			ftClass = Class.forName("org.sakaiproject.util.api.FormattedText");
			ftMethod = ftClass.getMethod("processFormattedText",
					new Class[] { String.class, StringBuilder.class, levelClass });
			ret = org.sakaiproject.component.cover.ComponentManager.get("org.sakaiproject.util.api.FormattedText");
			return ret;
		} catch (Exception e) {
			log.error("Formatted Text with levels not available: " + e);
			return null;
		}
	}

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo() {
		return "Lessons Ajax Server";
	}

	/**
	 * Initialize the servlet.
	 * 
	 * @param config
	 *            The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy() {
		log.info("destroy()");

		super.destroy();
	}

	/**
	 * Respond to Get requests: display main content by redirecting to it and
	 * adding user= euid= site= role= serverurl= time= sign= for privileged
	 * users, add a bar at the top with a link to the setup screen ?Setup
	 * generates the setup screen
	 * 
	 * @param req
	 *            The servlet request.
	 * @param res
	 *            The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */

	public static String getMessage(String code, String localeName) {
		Locale locale = null;

		if (localeName != null && localeName.length() > 0) {
			String langLoc[] = localeName.split("_");
			if (langLoc.length >= 2) {
				if ("en".equals(langLoc[0]) && "ZA".equals(langLoc[1])) {
					locale = new Locale("en", "GB");
				} else {
					locale = new Locale(langLoc[0], langLoc[1]);
				}
			} else {
				locale = new Locale(langLoc[0]);
			}
		}
		if (locale == null)
			locale = Locale.getDefault();

		String value = "";
		try {
			value = messageSource.getMessage(code, new String[0], locale);
		} catch (Exception e) {
		}
		;

		return value;
	}

	// in order to match the logic in ShowPageProducer
	// * youtube has to return something other than html, in this case
	// application/youtube,
	// * image types have to return something with image, using the same test as
	// SimplePageBean.isimage
	// * html has to return an html type even based on extension, matching test
	// in ShowPageProducer

	public String getMimeType(String url) {
		Session s = SessionManager.getCurrentSession();
		if (s == null || s.getUserId() == null) {
			// return "";
		}

		if (SimplePageBean.getYoutubeKeyFromUrl(url) != null)
			return "application/youtube";

		String mimeType = "";
		URLConnection conn = null;
		try {
			conn = new URL(new URL(ServerConfigurationService.getServerUrl()), url).openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			String t = conn.getContentType();
			if (t != null && !t.equals("")) {
				int i = t.indexOf(";");
				if (i >= 0)
					t = t.substring(0, i);
				t = t.trim();
				mimeType = t;
			}
		} catch (Exception e) {
		} finally {
			if (conn != null) {
				try {
					conn.getInputStream().close();
				} catch (Exception e) {
					// log.error("getTypeOfUrl unable to close " + e);
				}
			}
		}

		if (mimeType == null || mimeType.equals("")) {
			String name = url;

			// starts after last /
			int i = name.lastIndexOf("/");
			if (i >= 0)
				name = name.substring(i + 1);

			String extension = null;
			i = name.lastIndexOf(".");
			if (i > 0)
				extension = name.substring(i + 1);

			if (extension == null)
				return "";

			if (SimplePageBean.imageTypes.contains(extension)) {
				return "image/unknown";
			}
			if (extension.equals("html") || extension.equals("htm")) {
				return "text/html";
			} else if (extension.equals("xhtml") || extension.equals("xht")) {
				return "application/xhtml+xml";
			} else {
				return "";
			}

		}

		return mimeType;
	}

	// run antisamy or whatever on a string

	// WARNING: keep in sync with submit in SimplePageBean.

	public static String filterHtml(String contents) {

		StringBuilder error = new StringBuilder();

		final Integer FILTER_DEFAULT = 0;
		final Integer FILTER_HIGH = 1;
		final Integer FILTER_LOW = 2;
		final Integer FILTER_NONE = 3;

		// Sakai currently defaults to high. Unfortunately many
		// embeds won't work with that. Might want to default this
		// to low, or add another config parameter, but currently
		// using same code as for text blocks.

		String html = contents;

		// figure out how to filter
		Integer filter = FILTER_DEFAULT;
		// simplepagebean checks filterHtml property of tool. We can't really do
		// that.

		String filterSpec = filterHtml;
		log.info("filterspec " + filterSpec);
		if (filterSpec == null) // should never be null. unspeciifed should give
								// ""
			filter = FILTER_DEFAULT;
		// old specifications
		else if (filterSpec.equalsIgnoreCase("true"))
			filter = FILTER_HIGH; // old value of true produced the same result
									// as missing
		else if (filterSpec.equalsIgnoreCase("false"))
			filter = FILTER_NONE;
		// new ones
		else if (filterSpec.equalsIgnoreCase("default"))
			filter = FILTER_DEFAULT;
		else if (filterSpec.equalsIgnoreCase("high"))
			filter = FILTER_HIGH;
		else if (filterSpec.equalsIgnoreCase("low"))
			filter = FILTER_LOW;
		else if (filterSpec.equalsIgnoreCase("none"))
			filter = FILTER_NONE;
		// unspecified
		else
			filter = FILTER_DEFAULT;

		if (filter.equals(FILTER_NONE)) {
			html = FormattedText.processHtmlDocument(contents, error);
		} else if (filter.equals(FILTER_DEFAULT)) {
			html = FormattedText.processFormattedText(contents, error);
		} else if (ftInstance != null) {
			try {
				// now filter is set. Implement it. Depends upon whether we have
				// the anti-samy code
				Object level = null;
				if (filter.equals(FILTER_HIGH))
					level = levels[1];
				else
					level = levels[2];

				html = (String) ftMethod.invoke(ftInstance, new Object[] { contents, error, level });
			} catch (Exception e) {
				// this should never happen. If it does, emulate what the
				// anti-samy
				// code does if antisamy is disabled. It always filters
				html = FormattedText.processFormattedText(contents, error);
			}
		} else {
			// don't have antisamy. For LOW, use old instructor behavior, since
			// LOW is the default. For high, it makes sense to filter
			if (filter.equals(FILTER_HIGH))
				html = FormattedText.processFormattedText(contents, error);
			else
				html = FormattedText.processHtmlDocument(contents, error);

		}
		return html;
	}

	// argument is comma separated list, locale, site, group, group ...
	public static String groupErrors(String siteId, String locale, String groupString) {

		locale = locale.trim();
		if (locale.length() == 0)
			locale = null;
		if (siteId == null)
			siteId = "";
		siteId = siteId.trim();

		// currently this is only needed by the instructor
		String ref = "/site/" + siteId;
		if (!SecurityService.unlock(SimplePage.PERMISSION_LESSONBUILDER_UPDATE, ref))
			return getMessage("simplepage.nowrite", locale);

		if (groupString == null)
			groupString = "";
		Map<String, Set<String>> user2groups = new HashMap<String, Set<String>>();
		Set<String> overlapGroups = new HashSet<String>();
		String[] groupIdArray = groupString.trim().split(",");
		List<String> groupIds = new ArrayList<String>();
		for (String g : groupIdArray) {
			g = g.trim();
			if (g.length() > 0)
				groupIds.add(g);
		}

		Site site = null;
		Collection<String> users = null;
		Collection<Group> groups = null;

		try {
			// get all users in site and add entries to user@groups
			// this will have all the groups each user belongs to
			site = siteService.getSite(siteId);
			String siteRef = site.getReference();
			HashSet<String> siteGroup = new HashSet<String>();
			siteGroup.add("/site/" + siteId);
			// users = authzGroupService.getUsersIsAllowed("site.visit",
			// siteGroup);
			// only want students
			List<User> userList = SecurityService.unlockUsers("section.role.student", siteRef);
			for (User user : userList) {
				user2groups.put(user.getId(), null);
			}

			// get list of groups, either specified list or all groups in site
			if (groupIds.size() > 0) {
				groups = new HashSet<Group>();
				for (String groupId : groupIds) {
					groups.add(site.getGroup(groupId));
				}
			} else
				groups = site.getGroups();

			// for each group
			// for each user in group
			// if user already in a different group, there's an overlap
			// otherwise remember this user is in this group
			for (Group group : groups) {
				users = group.getUsers();
				for (String groupUser : users) {
					Set<String> userGroups = user2groups.get(groupUser);
					if (userGroups != null) {
						userGroups.add(group.getId());
						overlapGroups.addAll(userGroups);
					} else {
						userGroups = new HashSet<String>();
						userGroups.add(group.getId());
						user2groups.put(groupUser, userGroups);
					}
				}
			}

			// now output warnings
			String retmessage = null;
			if (overlapGroups.size() > 0) {
				String overlaps = "";
				for (String groupId : overlapGroups) {
					Group group = site.getGroup(groupId);
					overlaps += ", " + group.getTitle();
				}
				retmessage = getMessage("simplepage.groupcheckoverlaps", locale).replace("{}", overlaps.substring(2));
			}
			String missing = "";
			for (Map.Entry<String, Set<String>> entry : user2groups.entrySet()) {
				if (entry.getValue() == null) {
					String eid = UserDirectoryService.getUserEid(entry.getKey());
					missing += ", " + eid;
				}
			}
			if (missing.length() > 1) {
				if (retmessage == null)
					retmessage = "";
				else
					retmessage += "\n\n";
				retmessage += getMessage("simplepage.groupchecknogroups", locale).replace("{}", missing.substring(2));
			}
			if (retmessage != null)
				return "\n\n" + retmessage + "\n\n" + getMessage("simplepage.insist", locale);
			else
				return "ok";

		} catch (Exception e) {
			return getMessage("simplepage.groupcheckfailed", locale).replace("{}", e.toString());
		}
	}

	public static String insertBreakBefore(String itemId, String type, String cols, String csrfToken) {

		if (itemId == null) {
			log.error("Ajax insertBreakBefore passed null itemid");
			return null;
		}

		if (!"section".equals(type) && !"column".equals(type)) {
			log.error("Ajax insertBreakBefore passed illegal type " + type);
			return null;
		}

		simplePageToolDao.setRefreshMode();

		boolean below = false;
		itemId = itemId.trim();
		if (itemId.startsWith("-")) {
			below = true;
			itemId = itemId.substring(1);
		}

		long id = 0;
		if (!itemId.startsWith("p")) {
			try {
				id = Long.parseLong(itemId);
			} catch (Exception e) {
				log.error("Ajax insertBreakBefore passed illegal item id " + itemId);
			}
		}

		// currently this is only needed by the instructor

		SimplePageItem item = null;
		SimplePage page = null;
		String siteId = null;
		try {
			if (itemId.startsWith("p")) {
				page = simplePageToolDao.getPage(Long.parseLong(itemId.substring(1)));
			} else {
				item = simplePageToolDao.findItem(id);
				page = simplePageToolDao.getPage(item.getPageId());
			}
			siteId = page.getSiteId();
		} catch (Exception e) {
			log.error("Ajax insertBreakBefore passed invalid data " + e);
			return null;
		}
		if (siteId == null) {
			log.error("Ajax insertBreakBefore passed null site id");
			return null;
		}

		if (!lessonsAccess.canEditPage(siteId, page) || !checkCsrf(csrfToken)) {
			log.error("Ajax insertBreakBefore passed itemid " + itemId + " but user doesn't have permission");
			return null;
		}

		List<SimplePageItem> items = simplePageToolDao.findItemsOnPage(page.getPageId());

		// this block of code is because pages really should start with a
		// section break at the top, but don't
		// always. When that happens, the UI generates a pseudo-item ID or
		// "pxxxx" where xxxx is the page number
		SimplePageItem firstItem = null;
		if (items != null && items.size() > 0) {
			firstItem = items.get(0);
		}
		if (firstItem == null || firstItem.getType() != SimplePageItem.BREAK) {
			// old format page. Add an initial break
			SimplePageItem breakItem = simplePageToolDao.makeItem(page.getPageId(), 1, SimplePageItem.BREAK, null,
					null);
			breakItem.setFormat("section");
			simplePageToolDao.quickUpdate(breakItem);
			// increment numbers for items after it
			if (items != null) {
				for (SimplePageItem i : items) {
					i.setSequence(i.getSequence() + 1);
					simplePageToolDao.quickUpdate(i);
				}
			}
			// refresh items list
			items = simplePageToolDao.findItemsOnPage(page.getPageId());
		}

		// now we have a new format page, and we can handle it normally
		if (itemId.startsWith("p")) {
			// didn't have initial break when page displayed. We do now, so use
			// it
			item = items.get(0);
			id = item.getId();
		}

		// we have an item id. insert before it
		int nseq = 0; // sequence number of new item
		boolean after = false; // we found the item to insert before
		if (below) {
			// have an item number specified, look for the item to insert after
			long before = id;
			for (SimplePageItem i : items) {
				if (i.getId() == before) {
					// found item to insert after
					// use next sequence and bump all after
					nseq = i.getSequence() + 1;
					after = true;
					continue;
				}
				if (after) {
					i.setSequence(i.getSequence() + 1);
					simplePageToolDao.quickUpdate(i);
				}
			}
		} else {
			// have an item number specified, look for the item to insert before
			long before = item.getId();
			for (SimplePageItem i : items) {
				if (i.getId() == before) {
					// found item to insert before
					// use its sequence and bump up it and all after
					nseq = i.getSequence();
					after = true;
				}
				if (after) {
					i.setSequence(i.getSequence() + 1);
					simplePageToolDao.quickUpdate(i);
				}
			}
		}

		// if after not set, we didn't find the item; either no item specified
		// or it
		if (!after) {
			log.error("Ajax insertBreakBefore passed item not on its page " + id);
			return null;
		}

		SimplePageItem i = simplePageToolDao.makeItem(item.getPageId(), nseq, SimplePageItem.BREAK, null, null);
		i.setFormat(type);

		simplePageToolDao.quickSaveItem(i);
		return "" + i.getId();

	}

	public static String setColumnProperties(String itemId, String width, String split, String color,
			String csrfToken) {

		if (itemId == null || width == null || split == null) {
			log.error("Ajax setColumnProperties passed null argument");
			return null;
		}

		simplePageToolDao.setRefreshMode();

		itemId = itemId.trim();
		// we don't actually use the integers. Just for syntax checking
		int widthi = 0;
		int spliti = 0;
		try {
			widthi = Integer.parseInt(width);
			spliti = Integer.parseInt(split);
		} catch (Exception e) {
			log.error("Ajax setColumnProperties passwd non-numeric width or split");
			return null;
		}

		if (color != null) {
			if (color.equals(""))
				color = null;
			else if (!color.matches("^[a-z]*$")) {
				log.error("Ajax setColumnProperties passwd unreasonable color");
				return null;
			}
		}

		// currently this is only needed by the instructor

		SimplePageItem item = null;
		SimplePage page = null;
		String siteId = null;
		try {
			Long itemNum = Long.parseLong(itemId);
			item = getCorrectBreakItem(itemNum);
			page = simplePageToolDao.getPage(item.getPageId());
			siteId = page.getSiteId();
		} catch (Exception e) {
			log.error("Ajax setcolumnproperties passed invalid data " + e);
			return null;
		}
		if (siteId == null) {
			log.error("Ajax setcolumnproperties passed null site id");
			return null;
		}

		if (!lessonsAccess.canEditPage(siteId, page) || !checkCsrf(csrfToken)) {
			log.error("Ajax setcolumnproperties passed itemid " + itemId + " but user doesn't have permission");
			return null;
		}

		if (width.trim().equals("1"))
			item.removeAttribute("colwidth");
		else
			item.setAttribute("colwidth", width);

		if (split.trim().equals("1"))
			item.removeAttribute("colsplit");
		else
			item.setAttribute("colsplit", split);

		if (color == null)
			item.removeAttribute("colcolor");
		else
			item.setAttribute("colcolor", color);

		simplePageToolDao.quickUpdate(item);
		return "ok";

	}

	public static String setSectionCollapsible(String itemId, String collapsible, String sectionTitle,
			String defaultClosed, String csrfToken) {
		if (itemId == null || collapsible == null || sectionTitle == null) {
			log.error("Ajax setSectionCollapsible passed null argument");
			return null;
		}

		simplePageToolDao.setRefreshMode();

		itemId = itemId.trim();
		// we don't actually use the integers. Just for syntax checking
		int collapsiblei = 0;
		int defaultClosedi = 0;
		try {
			collapsiblei = Integer.parseInt(collapsible);
			defaultClosedi = Integer.parseInt(defaultClosed);
		} catch (Exception e) {
			log.error("Ajax setSectionCollapsible passed non-numeric collapsible or defaultClosed");
			return null;
		}

		// currently this is only needed by the instructor

		SimplePageItem item = null;
		SimplePage page = null;
		String siteId = null;
		try {
			Long itemNum = Long.parseLong(itemId);
			item = getCorrectBreakItem(itemNum);
			page = simplePageToolDao.getPage(item.getPageId());
			siteId = page.getSiteId();
		} catch (Exception e) {
			log.error("Ajax setSectionCollapsible passed invalid data " + e);
			return null;
		}
		if (siteId == null) {
			log.error("Ajax setSectionCollapsible passed null site id");
			return null;
		}

		if (!lessonsAccess.canEditPage(siteId, page) || !checkCsrf(csrfToken)) {
			log.error("Ajax setSectionCollapsible passed itemid " + itemId + " but user doesn't have permission");
			return null;
		}

		if (collapsible.trim().equals("1"))
			item.setAttribute("collapsible", collapsible);
		else
			item.removeAttribute("collapsible");

		if (defaultClosed.trim().equals("1"))
			item.setAttribute("defaultClosed", defaultClosed);
		else
			item.removeAttribute("defaultClosed");

		item.setName(sectionTitle);

		simplePageToolDao.quickUpdate(item);
		return "ok";
	}

	private static SimplePageItem getCorrectBreakItem(Long itemNum) {
		SimplePageItem item = simplePageToolDao.findItem(itemNum);
		if (item.getType() != SimplePageItem.BREAK) {
			// hopefully this is the first item, in an old page that doesn't
			// begin with a page break
			List<SimplePageItem> items = simplePageToolDao.findItemsOnPage(item.getPageId());
			if (items.get(0).getId() == itemNum) {
				// this is first item on page, add a section break before it
				item = simplePageToolDao.makeItem(item.getPageId(), 1, SimplePageItem.BREAK, null, null);
				item.setFormat("section");
				simplePageToolDao.quickSaveItem(item);
				int seq = 2;
				// and bump sequence numbers
				for (SimplePageItem i : items) {
					i.setSequence(seq);
					simplePageToolDao.quickUpdate(i);
					seq++;
				}
			} else if (items.get(0).getType() == SimplePageItem.BREAK && items.get(1).getId() == itemNum) {
				// maybe we just inserted a break before our item.
				// If so, use the break;
				item = items.get(0);
			} else {
				log.error("Ajax setSectionCollapsible passed item not a break: " + itemNum);
			}
		}
		return item;
	}

	public static String deleteItem(String itemId, String csrfToken) {
		if (itemId == null) {
			log.error("Ajax deleteBreak passed null itemid");
			return null;
		}

		simplePageToolDao.setRefreshMode();

		itemId = itemId.trim();

		// currently this is only needed by the instructor

		SimplePageItem item = null;
		SimplePage page = null;
		String siteId = null;
		try {
			item = simplePageToolDao.findItem(Long.parseLong(itemId));
			page = simplePageToolDao.getPage(item.getPageId());
			siteId = page.getSiteId();
		} catch (Exception e) {
			log.error("Ajax deleteBreak passed invalid data " + e);
			return null;
		}
		if (siteId == null) {
			log.error("Ajax deleteBreak passed null site id");
			return null;
		}

		if (!lessonsAccess.canEditPage(siteId, page) || !checkCsrf(csrfToken)) {
			log.error("Ajax deleteBreak passed itemid " + itemId + " but user doesn't have permission");
			return null;
		}

		List<SimplePageItem> items = simplePageToolDao.findItemsOnPage(item.getPageId());

		// we have an item id. adjust sequence for items after it
		boolean after = false; // we found the item to delete
		// have an item number specified, look for it
		long before = item.getId();
		for (SimplePageItem i : items) {
			if (item.getId() == before) {
				after = true;
			} else if (after) {
				item.setSequence(item.getSequence() - 1);
				simplePageToolDao.quickUpdate(item);
			}
		}

		simplePageToolDao.quickDelete(item);

		return "ok";

	}

	public static String isLogged(String itemId) {
		if (itemId == null) {
			log.error("Ajax isLogged passed null itemid");
			return null;
		}

		itemId = itemId.trim();

		// maybe this isn't needed. Just trying to verify that user
		// is actually in the site
		SimplePageItem item = null;
		SimplePage page = null;
		String siteId = null;
		try {
			item = simplePageToolDao.findItem(Long.parseLong(itemId));
			page = simplePageToolDao.getPage(item.getPageId());
			siteId = page.getSiteId();
		} catch (Exception e) {
			log.error("Ajax track passed invalid data " + e);
			return null;
		}
		// user can pass any item id they want. It doesnt' make sense to protect
		// against this, since doing that is
		// more work than just looking at the link. But only allow it for actual
		// links.
		if (siteId == null || item.getType() != SimplePageItem.RESOURCE) {
			log.error("Ajax isLogged passed bad item " + itemId);
			return null;
		}

		String ref = "/site/" + siteId;
		if (!SecurityService.unlock(SimplePage.PERMISSION_LESSONBUILDER_READ, ref)) {
			// user doesn't have permission in site
			// not worth testing whether they have access to the page
			return null;
		}

		// there should be no required items on student pages, so we just pass
		// -1
		SimplePageLogEntry entry = simplePageToolDao.getLogEntry(SessionManager.getCurrentSessionUserId(), item.getId(),
				-1L);
		if (entry != null)
			return "ok";
		else
			return "fail";

	}

	public static boolean checkCsrf(String csrfToken) {
		Object sessionToken = SessionManager.getCurrentSession().getAttribute("sakai.csrf.token");
		if (sessionToken != null && sessionToken.toString().equals(csrfToken)) {
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		// get the Tool
		Placement placement = ToolManager.getCurrentPlacement();
		Properties config = null;
		String placementId = "none";

		if (placement != null) {
			config = placement.getConfig();
			placementId = placement.getId();
		}

		res.setContentType("text/html; charset=utf-8");
		// PrintWriter out = res.getWriter();

		String op = req.getParameter("op");
		if (op.equals("getmessage")) {
			PrintWriter out = res.getWriter();
			String code = req.getParameter("code");
			String locale = req.getParameter("locale");
			out.println(getMessage(code, locale));
		} else if (op.equals("getmimetype")) {
			PrintWriter out = res.getWriter();
			String url = req.getParameter("url");
			out.println(getMimeType(url));
		} else if (op.equals("filterhtml")) {
			PrintWriter out = res.getWriter();
			String html = req.getParameter("html");
			out.print(filterHtml(html));
		} else if (op.equals("getgrouperrors")) {
			PrintWriter out = res.getWriter();
			String siteid = req.getParameter("site");
			String locale = req.getParameter("locale");
			String groups = req.getParameter("groups");
			out.print(groupErrors(siteid, locale, groups));
		} else if (op.equals("insertbreakbefore")) {
			PrintWriter out = res.getWriter();
			String itemId = req.getParameter("itemid");
			String type = req.getParameter("type");
			String cols = req.getParameter("cols");
			String csrfToken = req.getParameter("csrf");
			out.println(insertBreakBefore(itemId, type, cols, csrfToken));
		} else if (op.equals("setcolumnproperties")) {
			PrintWriter out = res.getWriter();
			String itemId = req.getParameter("itemid");
			String width = req.getParameter("width");
			String split = req.getParameter("split");
			String color = req.getParameter("color");
			String csrfToken = req.getParameter("csrf");
			out.println(setColumnProperties(itemId, width, split, color, csrfToken));
		} else if (op.equals("setsectioncollapsible")) {
			PrintWriter out = res.getWriter();
			String itemId = req.getParameter("itemid");
			String collapsible = req.getParameter("collapsible");
			String sectionTitle = req.getParameter("sectiontitle");
			String defaultClosed = req.getParameter("defaultclosed");
			String csrfToken = req.getParameter("csrf");
			out.println(setSectionCollapsible(itemId, collapsible, sectionTitle, defaultClosed, csrfToken));
		} else if (op.equals("deleteitem")) {
			PrintWriter out = res.getWriter();
			String itemId = req.getParameter("itemid");
			String csrfToken = req.getParameter("csrf");
			out.println(deleteItem(itemId, csrfToken));
		} else if (op.equals("islogged")) {
			PrintWriter out = res.getWriter();
			String itemId = req.getParameter("itemid");
			out.println(isLogged(itemId));
		}
		// Thach.Add.Start.Send progress of learned lesson into server
		else if (op.equals("sendprogress")) {
			PrintWriter out = res.getWriter();
			String siteid = req.getParameter("siteid");
			Long itemId = Long.valueOf(req.getParameter("itemid"));
			Double percent = Double.valueOf(req.getParameter("percent"));

			// Call method to update the progress into db
			int nRow = updateProgress(siteid, itemId, percent);
			out.println(nRow);
		} else if (op.equals("exportprogress")) {
			String siteid = req.getParameter("siteid");
			Long itemId = Long.valueOf(req.getParameter("itemid"));

			// Call method to download the progress of the learned video
			exportProgress(siteid, itemId, res);
		} else if (op.equals("getprogress")) {
			// Provide the learned progress video of given user.
			// Return: json {status, data}
			PrintWriter out = res.getWriter();

			String username = req.getParameter("username");
			String courseAlias = req.getParameter("coursealias");

			// Check parameters
			JSonResult result = new JSonResult();

			if (username == null || username.isEmpty()) {
				result.setStatus("error");
				result.setData("Parameter 'username' is required.");

				String json = new Gson().toJson(result);
				out.println(json);

				return;
			}

			if (courseAlias == null || courseAlias.isEmpty()) {
				result.setStatus("error");
				result.setData("Parameter 'coursealias' is required.");

				String json = new Gson().toJson(result);
				out.println(json);
				return;
			}

			String progress = getProgress(username, courseAlias);

			result.setStatus("ok");
			result.setData(progress);
			String json = new Gson().toJson(result);
			out.println(json);
		}
		// Thach.Add.End
	}

	/**
	 * Save or update progress into database.
	 * 
	 * @param itemId
	 * @param percent
	 * @return number of records are updates. Normally, it is one.
	 */
	private int updateProgress(String siteId, Long itemId, Double percent) {
		int nRow;
		TrackProgress progress = null;

		String userId = SessionManager.getCurrentSessionUserId();

		// Try to load lessonId from DB
		progress = simplePageToolDao.findTrackProgressBySiteIdUserIdItemId(siteId, userId, itemId);

		if (log.isDebugEnabled()) {
			log.debug(
					String.format("User '%s' have learned %f item '%s' in site '%s'", userId, percent, itemId, siteId));
		}

		if (progress == null) {
			progress = new TrackProgressImpl();

			progress.setSiteId(siteId);
			progress.setUserId(userId);
			progress.setLastTimeTracked(new Date());
			progress.setItemId(itemId);
			progress.setPercent(percent);

			boolean saveOK = simplePageToolDao.quickSaveItem(progress);
			nRow = (saveOK) ? 1 : 0;
		} else {
			progress.setPercent(percent);
			progress.setLastTimeTracked(new Date());

			// simplePageToolDao.quickUpdate(progress);
			// This statement occurred error:
			// org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException:
			// Object of class
			// [org.sakaiproject.lessonbuildertool.TrackProgressImpl] with
			// identifier [31]: optimistic locking failed; nested exception is
			// org.hibernate.StaleObjectStateException: Row was updated or
			// deleted by another transaction (or unsaved-value mapping was
			// incorrect):
			nRow = simplePageToolDao.update(progress);

			if (nRow != 1) {
				log.warn(String.format("Invalid updated record(siteId, userId, itemId) = (%s, %s, %d)",
						progress.getSiteId(), progress.getUserId(), progress.getItemId()));
			}
		}
		return nRow;
	}
	
    private String getProgress(String username, String courseAlias) {
        String progress = "Inactive";
        log.debug(String.format("Get learned progress of user '%s' in in course '%s'", username, courseAlias));
        
        String userId = simplePageToolDao.findUserIdByUsername(username);

        // Find TARGET from ALIAS_ID in table sakai_alias
        // Ex target: /site/66fb75fb-dde9-4212-a7da-681f121d7449
        // Remove "/site/" to get site id: 66fb75fb-dde9-4212-a7da-681f121d7449
        // Sample query: select TARGET from sakai_alias WHERE ALIAS_ID = 'biz-eng';
        
        String siteId = simplePageToolDao.findSiteIdByAlias(courseAlias);
        
        // Get pages of the site by query: SELECT pageId FROM lesson_builder_pages WHERE siteId = '66fb75fb-dde9-4212-a7da-681f121d7449';
       
        // Get lesson items (uploaded videos or embedded videos from Youtube) by siteId
        /*         
         SELECT id
         FROM lesson_builder_items
         WHERE pageId IN
         (SELECT pageId FROM lesson_builder_pages WHERE siteId = '66fb75fb-dde9-4212-a7da-681f121d7449')
         AND TYPE = 7;
         */
        
        List<Double> learnedPercents = simplePageToolDao.findPercent(userId, siteId);
        
        int len = (learnedPercents != null) ? learnedPercents.size() : 0;

        if (len == 0) {
            log.warn(String.format("User '%s' in course '%s' has no video items.", username, courseAlias));
        } else {
            int nNoLearn = 0; // Number of video item which has not bean learned by the user.
            for (Double percent : learnedPercents) {
                if (percent != null && percent < 80) {
                    return "In-progress";
                } else if (percent == null) {
                    nNoLearn++;
                } else {
                    // Learned > 80%
                }
            }
            
            if (nNoLearn < len) {
                return "Completed";
            }
        }
        
        return progress;
    }


    /**
     * Export the learning progress of the video into Excel.
     * @param siteId
     * @param itemId
     * @param res
     */
    private void exportProgress(String siteId, Long itemId, HttpServletResponse res) {
        OutputStream out = null;
        try {
            String itemName = getItemName(itemId);
            HSSFWorkbook XSSFWorkbook = generateProgressExcel(siteId, itemId, itemName);
            res.setContentType("application/vnd.ms-excel");
            
            String currentTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            
            String filename = itemName + "_learning-progress_" + currentTime + ".xls";
            res.setHeader("Content-disposition", "attachment; filename=" + filename);

            out = res.getOutputStream();
            XSSFWorkbook.write(out);

            out.close();
           
        } catch (IOException ex) {
            log.error("Could not generate results export: ", ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    // Skip this exception
                    log.warn("Could not close the output stream while exporting learning progress.");
                }
            }
        }
    }

    /**
     * Get video name.
     * @param itemId
     * @return
     */
    private String getItemName(Long itemId) {
        String itemName;
        SimplePageItem pageItem = this.simplePageToolDao.findItem(itemId);
        
        itemName = (pageItem != null) ? pageItem.getName() : "Noname";
        
        return itemName;
    }

    private HSSFWorkbook generateProgressExcel(String siteId, Long itemId, String videoName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        final int FIRST_SHEET = 0;
        final String NO_GROUP = null;

        String[] headerUserSheet = {"Video", "Leaner Id", "Learner Display Name", "Learned progress(%)", "Status", "Last learned Time"};
        String[] headerGroupSheet = {"Video", "Group", "Leaner Id", "Learner Display Name", "Learned progress(%)", "Status", "Last learned Time"};

        if (log.isDebugEnabled()) {
            log.debug(String.format("Genereate learned progress for siteId=%s, itemId=%s", siteId, itemId));
        }

        HSSFSheet spreadsheetUser = workbook.createSheet("User");
        HSSFSheet spreadsheetGroup = workbook.createSheet("Group");
        
        // Write header
        HSSFRow rowUser = spreadsheetUser.createRow(FIRST_SHEET);
        HSSFRow rowGroup = spreadsheetGroup.createRow(FIRST_SHEET);
        
        // Write header
        createHeader(rowUser, headerUserSheet);
        createHeader(rowGroup, headerGroupSheet);
        
        String userId = SessionManager.getCurrentSessionUserId();
        int idxRow = 0;
        // Get Groups and user
        try {
            Site site = siteService.getSite(siteId);
            Collection<Group> groups = site.getGroups();
            
            if (groups != null) {
                Set<Member> members;
                
                for (Group group : groups) {
                    members = group.getMembers();
                    if (members != null) {
                        for (Member member : members) {
                            idxRow++;
                            rowGroup = spreadsheetGroup.createRow(idxRow);
    
                            writeReport(siteId, itemId, rowGroup, userId, member, group.getTitle(), videoName);
                        }
                    }
                }
            }
            
            // Get all users
            Set<Member> members = site.getMembers();
            idxRow = 0;
            if (members != null) {
                for (Member member : members) {
                    idxRow++;
                    rowUser = spreadsheetUser.createRow(idxRow);
                    
                    writeReport(siteId, itemId, rowUser, userId, member, NO_GROUP, videoName);
                }
            }
            
        } catch (IdUnusedException ex) {
            log.error("Could not get site: " + siteId, ex);
        }
        
        // Set auto column with
        autoColumnSize(spreadsheetUser, headerUserSheet.length);
        autoColumnSize(spreadsheetGroup, headerGroupSheet.length);

        return workbook;
    }

    private void autoColumnSize(HSSFSheet sheet, int nCol) {
        for (int i = 0; i < nCol; i++) {
            sheet.autoSizeColumn(i);
        }   
    }

    /**
     * Write data depending on group.
     * @param siteId
     * @param itemId
     * @param rowUser
     * @param userId
     * @param member
     * @param groupName
     * @param videoName 
     */
    private void writeReport(String siteId, Long itemId, HSSFRow rowUser, String userId, Member member, String groupName, String videoName) {
        Double percent = null;
        Date lastTimeTracked = null;
        String displayProgress = null;

        // Get learned progress from DB: Video name, Learned progress
        Object[] result = simplePageToolDao.getReportProgress(itemId, siteId, member.getUserId());

        if (result != null) {
            percent = (Double) result[0];
            lastTimeTracked = (Date) result[1];
            
            if (percent != null) {
                displayProgress = (percent > 80) ? "Completed" : ((percent > 0.0) ? "On-Going" : "None");
            } else {
                displayProgress = null;
                if (log.isDebugEnabled()) {
                    log.warn(String.format("No found learned progress (itemId=%s, siteId=%s, userId=%s)", itemId, siteId, member.getUserId()));
                }
            }
        }
        
        if (groupName == null) {
            writeRow(rowUser, videoName , member.getUserEid(), member.getUserDisplayId(), percent, displayProgress, lastTimeTracked);
        } else {
            writeRow(rowUser, videoName , groupName, member.getUserEid(), member.getUserDisplayId(), percent, displayProgress, lastTimeTracked);
        }
    }

    /**
     * Write data into row of the Excel file.
     * @param row Row instance of the Excel file.
     * @param columnData data of columns
     */
    private void writeRow(HSSFRow row, Object ...columnData) {
        HSSFCellStyle dateCellStyle;
        HSSFCellStyle percentCellStyle;
        
        
        HSSFCell cell;
        int idxCol = 0;
        if (columnData != null) {
            
            HSSFWorkbook workbook = row.getSheet().getWorkbook();
            
            // Create format for Date cell
            dateCellStyle = workbook.createCellStyle();
            short dateFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");
            dateCellStyle.setDataFormat(dateFormat);
            
            
            // Create format for Learned progress cell
            percentCellStyle = workbook.createCellStyle();
            short percentFormat = workbook.createDataFormat().getFormat("0");
            percentCellStyle.setDataFormat(percentFormat);
            
            for (Object colData : columnData) {
                cell = row.createCell(idxCol);
                
    
                if (colData instanceof String) {

                    cell.setCellValue((String) colData);
                } else if (colData instanceof Date) {

                    cell.setCellValue((Date) colData);
                    cell.setCellStyle(dateCellStyle);

                } else if (colData instanceof Double) {

                    cell.setCellValue((Double) colData);                   
                    cell.setCellStyle(percentCellStyle);

                } else if (colData != null) {
                    cell.setCellValue((String) colData);
                }
                
                idxCol++;
            }
        }
    }

    /**
     * Create header for Excel.
     * @param row Row to be set value.
     * @param headers String values of headers.
     */
    private void createHeader(HSSFRow row, String[] headers) {
        int len = (headers != null) ? headers.length : 0;
        
        HSSFCell cell;
        for (int i = 0; i < len; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
    }

	public void setMessageSource(MessageSource s) {
		messageSource = s;
	}

	public void setSiteService(SiteService s) {
		siteService = s;
	}

	public void setAuthzGroupService(AuthzGroupService s) {
		authzGroupService = s;
	}

	public void setLessonsAccess(LessonsAccess s) {
		lessonsAccess = s;
	}

	public void setLessonBuilderAccessService(LessonBuilderAccessService s) {
		lessonBuilderAccessService = s;
	}

}
