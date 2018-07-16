/*
 * #%L
 * SCORM Content Impl
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
package org.sakaiproject.scorm.content.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.exception.ServerOverloadException;

public class FastZipCHH extends ZipCHH {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2607530990601902212L;

	protected static final String VIRTUAL_FS_CACHE_KEY = "scormCHHVirtualFileSystem@";

	private static Log log = LogFactory.getLog(FastZipCHH.class);

	protected transient Cache cache = null;

	protected boolean isCacheDirty = false;

	@Override
	public void add(File file, String id) {
		super.add(file, id);
		isCacheDirty = true;
	}

	protected VirtualFileSystem buildVirtualFileSystem(ContentEntity parent, ContentResource realParent) {
		if (realParent == null)
			return null;
		final VirtualFileSystem fs = new VirtualFileSystem(realParent.getId());

		byte[] archive = null;

		try {
			archive = realParent.getContent();
		} catch (ServerOverloadException soe) {
			log.error("Caught a server overload exception trying to grab real parent's content", soe);
		}

		if (archive == null || archive.length <= 0)
			return null;

		getRelativePath(realParent.getId(), parent.getId());

		ZipReader reader = new ZipReader(new ByteArrayInputStream(archive)) {

			@Override
			protected boolean includeContent(boolean isDirectory) {
				return false;
			}

			@Override
			protected boolean isValid(String entryPath) {
				/*if (entryPath.endsWith(Entity.SEPARATOR) && entryPath.length() > 1)
					entryPath = entryPath.substring(0, entryPath.length() - 1);
				
				return isDeepEnough(entryPath, relativePath, 1);*/
				return true;
			}

			@Override
			protected ContentEntity processEntry(String entryPath, ByteArrayOutputStream outStream, boolean isDirectory) {
				if (isDirectory && !entryPath.endsWith(Entity.SEPARATOR)) {
					entryPath += Entity.SEPARATOR;
				}
				fs.addPath(entryPath);
				return null;
			}

		};

		reader.read();

		return fs;
	}

	protected void cacheVirtualFileSystem(String key, VirtualFileSystem fs) {
		if (null != fs && cache != null) {
			Element e = new Element(VIRTUAL_FS_CACHE_KEY + key, fs);
			cache.put(e);
		}
	}

	@Override
	public void commitDeleted(ContentResourceEdit edit, String uuid) {
		super.commitDeleted(edit, uuid);
		isCacheDirty = true;
	}

	@Override
	protected int countChildren(ContentEntity parent, int depth) {
		ContentResource realParent = (ContentResource) getRealParent(parent.getId());
		String relativePath = getRelativePath(realParent.getId(), parent.getId());

		VirtualFileSystem fs = getVirtualFileSystem(realParent);

		return fs.getCount(relativePath);
	}

	@Override
	protected List<ContentEntity> extractChildren(ContentEntity parent, int depth) {
		ContentResource realParent = (ContentResource) getRealParent(parent.getId());
		String relativePath = getRelativePath(realParent.getId(), parent.getId());

		VirtualFileSystem fs = getVirtualFileSystem(realParent);

		List<ContentEntity> list = new LinkedList<ContentEntity>();

		List<String> entityNames = fs.getChildren(relativePath);

		for (String name : entityNames) {
			if (name.endsWith(Entity.SEPARATOR)) {
				list.add(makeCollection(realParent, newId(relativePath, name), name));
			} else {
				list.add(makeResource(realParent, newId(relativePath, name), name, null));
			}
		}

		return list;
	}

	/**
	 * @return the cache
	 */
	public Cache getCache() {
		return cache;
	}

	@Override
	public String getContentHostingHandlerName() {
		return "org.sakaiproject.scorm.content.api.ZipCHH";
	}

	public Collection<String> getMemberCollectionIds(ContentEntity ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getMemberResourceIds(ContentEntity ce) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getUuid(String id) {
		// TODO Auto-generated method stub

	}

	protected VirtualFileSystem getVirtualFileSystem(ContentEntity parent) {
		ContentResource realParent = (ContentResource) getRealParent(parent.getId());
		String realParentId = realParent.getId();

		VirtualFileSystem fs = null;
		if (!isCacheDirty) {
			fs = uncacheVirtualFileSystem(realParentId);
		}
		if (fs == null) {
			fs = buildVirtualFileSystem(parent, realParent);
			if (fs != null) {
				cacheVirtualFileSystem(realParentId, fs);
			}
		}
		return fs;
	}

	@Override
	public void init() {
		//log.info("Registering ZipCollectionType, CompressedResourceType");
		//resourceTypeRegistry.register(new ZipCollectionType());
		//resourceTypeRegistry.register(new CompressedResourceType());
	}

	public String moveCollection(ContentCollectionEdit thisCollection, String new_folder_id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String moveResource(ContentResourceEdit thisResource, String new_id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setResourceUuid(String resourceId, String uuid) {
		// TODO Auto-generated method stub

	}

	protected VirtualFileSystem uncacheVirtualFileSystem(String key) {
		VirtualFileSystem fs = null;
		try {
			Element element = (cache != null ? cache.get(VIRTUAL_FS_CACHE_KEY + key) : null);
			if (element != null) {
				fs = (VirtualFileSystem) element.getValue();
			}
		} catch (ClassCastException cce) {
			log.error("Caught a class cast exception finding virtual file system with key " + key, cce);
		} catch (Exception e) {
			log.error("Caught an exception uncaching virtual file system with key " + key, e);
		}

		return fs;
	}
}
