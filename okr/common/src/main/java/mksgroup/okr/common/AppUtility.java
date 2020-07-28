/**
 * Licensed to MKS Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
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

package mksgroup.okr.common;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import mksgroup.okr.common.AppUtility;

/**
 * An example Utility.
 * 
 * @author Thach Ngoc Le (ThachLN@mks.com.vn)
 *
 */
public class AppUtility {
    /** For logging. */
    private static final Logger LOG = Logger.getLogger(AppUtility.class);

    /**
     * Parse a XML File into a document model.
     * @param xmlFile file of xml.
     * @return Document model if no error.
     */
    public static Document parseXML(File xmlFile) {
        Document xmlDoc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
            xmlDoc = db.parse(xmlFile);
        } catch (ParserConfigurationException ex) {
            LOG.error("Could not parse the file.", ex);
        } catch (SAXException ex) {
            LOG.error("Error in XML file.", ex);
        } catch (IOException ex) {
            LOG.error("Could not open file.", ex);
        }
        
        return xmlDoc;
    }
}
