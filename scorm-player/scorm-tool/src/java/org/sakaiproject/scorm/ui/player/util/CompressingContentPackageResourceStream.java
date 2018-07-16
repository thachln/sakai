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
/*
 * This code borrows substantially from the Apache Wicket nested class
 *  	org.apache.wicket.markup.html.CompressedPackageResource$CompressingResourceStream
 * authored by Janne Hietam&auml;ki
 * 
 * The original license for that class is pasted below:
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.scorm.ui.player.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPOutputStream;

import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.sakaiproject.scorm.model.api.ContentPackageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompressingContentPackageResourceStream extends ContentPackageResourceStream {

	private static final long serialVersionUID = 1L;

	   private static final Logger log = LoggerFactory.getLogger(CompressingContentPackageResourceStream.class);
	   
	/** Cache for compressed data */
	private SoftReference cache = new SoftReference(null);

	/** Timestamp of the cache */
	private Time timeStamp = null;

	public CompressingContentPackageResourceStream(ContentPackageResource resource) {
		super(resource);
	}

	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		return new ByteArrayInputStream(getCompressedContent());
	}

	private byte[] getCompressedContent() throws ResourceStreamNotFoundException {
        if (log.isDebugEnabled()) {
            log.debug("BaoNQ.Debug.getCompressedContent in CompressingContentPackageResourceStream");
        }
		InputStream stream = super.getInputStream();
		try {
			byte ret[] = (byte[]) cache.get();
			if (ret != null && timeStamp != null)
			{
				if (timeStamp.equals(lastModifiedTime()))
				{
					return ret;
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream zout = new GZIPOutputStream(out);
			Streams.copy(stream, zout);
			zout.close();
			stream.close();
			ret = out.toByteArray();
			timeStamp = lastModifiedTime();
			cache = new SoftReference(ret);
			return ret;
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	@Override
	public long length() 
	{
		try {
	        return getCompressedContent().length;
        } catch (ResourceStreamNotFoundException e) {
        	// No content, return null
	        return 0;
        }
	}

}
