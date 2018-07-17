/**
 * Licensed to FA Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * FA licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.sakaiproject.scorm.ui.console.pages;

import java.io.File;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WicketURLEncoder;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extend to customer filename when download
 * @author ThachLN
 */
public class ResultDownloadLink extends DownloadLink {
    /** . */
    private static final long serialVersionUID = 1L;

    /**
     * Our log (commons).
     */
    private static Logger log = LoggerFactory.getLogger(ResultDownloadLink.class);

    /**
     * File name to stream
     */
    private String fileName;
    private IGetSelectGroupName fGetSelectGroupName;

    private boolean deleteAfter;

    public ResultDownloadLink(String id, File file) {
        super(id, file);
    }

    /**
     * Constructor. File name used will be the result of <code>file.getName()</code>
     * @param id component id
     * @param model model that contains the file object
     * @param fileName name of the file
     */
    public ResultDownloadLink(String id, IModel<File> model, String fileName, IGetSelectGroupName fGetSelectGroupName) {
        super(id, model);
        this.fileName = fileName;
        this.fGetSelectGroupName = fGetSelectGroupName;
    }

    /**
     * @see org.apache.wicket.markup.html.link.Link#onClick()
     */
    @Override
    public void onClick() {
        log.debug("Thach.Processing download...");

        final File file = getModelObject();
        if (file == null) {
            throw new IllegalStateException(getClass().getName() + " failed to retrieve a File object from model");
        }

        String fileName1 = (fileName != null) ? fileName : file.getName();

        // Add Group as prefix for file name.
        fileName1 = fGetSelectGroupName.getGroupName() + fileName1;

        // fn: filename to be set for downloading.
        final String fn = WicketURLEncoder.QUERY_INSTANCE.encode(fileName1);

        IResourceStream resourceStream = new FileResourceStream(new org.apache.wicket.util.file.File(file));
        getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {
            @Override
            public String getFileName() {
                return fn;
            }

            @Override
            public void respond(RequestCycle requestCycle) {
                super.respond(requestCycle);

                if (deleteAfter) {
                    file.delete();
                }
            }
        });
    }
}
