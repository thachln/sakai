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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentEntity;

public abstract class ZipReader {
	private static Log log = LogFactory.getLog(ZipReader.class);

	private ZipInputStream zipStream;

	private int count = 0;

	public ZipReader(InputStream contentStream) {
		this.zipStream = new ZipInputStream(contentStream);
	}

	public int getCount() {
		return count;
	}

	protected abstract boolean includeContent(boolean isDirectory);

	protected abstract boolean isValid(String entryPath);

	protected abstract ContentEntity processEntry(String entryPath, ByteArrayOutputStream outStream, boolean isDirectory);

	public List<ContentEntity> read() {
		List<ContentEntity> list = new LinkedList<ContentEntity>();
		ZipEntry entry;
		ByteArrayOutputStream outStream = null;
		byte[] buffer = new byte[1024];
		int length;
		try {
			count = 0;
			entry = zipStream.getNextEntry();
			while (entry != null) {
				if (isValid(entry.getName())) {

					if (includeContent(entry.isDirectory())) {
						outStream = new ByteArrayOutputStream();
						while ((length = zipStream.read(buffer)) > 0) {
							outStream.write(buffer, 0, length);
						}

						if (null != outStream) {
							outStream.close();
						}
					}

					ContentEntity o = processEntry(entry.getName(), outStream, entry.isDirectory());

					if (null != o) {
						list.add(o);
					}
					count++;
				}
				entry = zipStream.getNextEntry();
			}
		} catch (IOException ioe) {
			log.error("Caught an io exception reading from zip stream", ioe);
		} finally {
			try {
				if (null != zipStream) {
					zipStream.close();
				}
				if (null != outStream) {
					outStream.close();
				}
			} catch (IOException noie) {
				log.info("Caught an io exception closing streams!");
			}
		}

		return list;
	}

	public Object readFirst() {
		ZipEntry entry;
		ByteArrayOutputStream outStream = null;
		byte[] buffer = new byte[1024];
		int length;
		try {
			entry = zipStream.getNextEntry();
			while (entry != null) {
				if (isValid(entry.getName())) {
					if (includeContent(entry.isDirectory())) {
						outStream = new ByteArrayOutputStream();
						while ((length = zipStream.read(buffer)) > 0) {
							outStream.write(buffer, 0, length);
						}

						outStream.close();
					}

					Object o = processEntry(entry.getName(), outStream, entry.isDirectory());

					zipStream.close();
					if (null != outStream) {
						outStream.close();
					}
					return o;
				}
				entry = zipStream.getNextEntry();
			}
		} catch (IOException ioe) {
			log.error("Caught an io exception reading from zip stream", ioe);
		} finally {
			try {
				if (null != zipStream) {
					zipStream.close();
				}
				if (null != outStream) {
					outStream.close();
				}
			} catch (IOException noie) {
				log.info("Caught an io exception closing streams!");
			}
		}

		return null;
	}

}
