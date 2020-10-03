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
package m.k.s.eng.sakai.service;

import java.util.ArrayList;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;

/**
 * @author ThachLN
 *
 */
public class EnglishService {
    public ArrayList getPublishedAssessment() {
        // Refer: org.sakaiproject.component.cover.ComponentManager, method getInstance()
        ComponentManager.testingMode = true;

        // Learn how to use AssessmentService at org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener
//        AssessmentService as = new AssessmentService();
        PublishedAssessmentService publishedAssessmentService = new PublishedAssessmentService();

     
        ArrayList listAs = publishedAssessmentService.getAllAssessments(null);
        
        return listAs;
    }
}
