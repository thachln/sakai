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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.impl.SpringCompMgr;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueriesAPI;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;

import junit.framework.Assert;

/**
 * @author ThachLN
 *
 */
public class EnglishServiceTest {
    private static boolean lateRefresh = false;
    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * [Give the description for method].
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testPublishedAssessmentService() {
        PublishedAssessmentService pas = new PublishedAssessmentService();
        
        Assert.assertNotNull(pas);
        
        // ----------------- prepare Takeable assessment list -------------
        // 1a. get total no. of submission (for grade) per assessment by the given agent in current site
        String agentString = AgentFacade.getAgentString();
        Assert.assertNotNull(agentString);
        String currentSiteId = AgentFacade.getCurrentSiteId();
        Assert.assertNotNull(currentSiteId);
        HashMap h = pas.getTotalSubmissionPerAssessment(agentString, currentSiteId);
        
        Assert.assertNotNull(h);
    }
    
    @Test
    public void testSakaiComponentManager() {
        org.sakaiproject.component.api.ComponentManager m_componentManager = new SpringCompMgr(null);
        ((SpringCompMgr) m_componentManager).init(lateRefresh);
        
        
        
        
        
        
        PersistenceService ps = (PersistenceService)org.sakaiproject.component.cover.ComponentManager.get("PersistenceService");
        Assert.assertNotNull(ps);
//        org.sakaiproject.component.cover.ComponentManager cm = m_componentManager.get("")
    }

    @Test
    public void test() {
        //EnglishService service = new EnglishService();
        

        
        PersistenceService ps = PersistenceService.getInstance();
        Assert.assertNotNull(ps);
        
        AssessmentFacadeQueriesAPI af = ps.getAssessmentFacadeQueries();
        Assert.assertNotNull(af);
        
        String orderBy = "Title";
        ArrayList pas = af.getAllAssessments(orderBy );
        
        Assert.assertNotNull(pas);
    }

}
