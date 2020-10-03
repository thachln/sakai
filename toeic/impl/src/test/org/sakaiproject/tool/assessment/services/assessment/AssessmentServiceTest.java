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
package org.sakaiproject.tool.assessment.services.assessment;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;

/**
 * @author ThachLN
 * @see org.sakaiproject.tool.assessment.services.qti.QTIServiceTest
 */
public class AssessmentServiceTest {
    @Test
    public void testAssessmentService() {
        // Refer: org.sakaiproject.component.cover.ComponentManager, method getInstance()
        ComponentManager.testingMode = true;

        // Learn how to use AssessmentService at org.sakaiproject.tool.assessment.ui.listener.author.AuthorActionListener
        AssessmentService as = new AssessmentService();
        PublishedAssessmentService publishedAssessmentService = new PublishedAssessmentService();
//        GradingService gradingService = new GradingService();

        Assert.assertNotNull(as);
        Assert.assertNotNull(publishedAssessmentService);
//        Assert.assertNotNull(gradingService);
     
        List<AssessmentFacade> listAs = publishedAssessmentService.getAllAssessments(null);
        
        Assert.assertNotNull(listAs);
    }
}
