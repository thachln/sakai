/*
 * #%L
 * SCORM API
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
package org.sakaiproject.scorm.model.api.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.lang.ObjectUtils;
import org.sakaiproject.scorm.model.api.LearnerExperience;

/**
 * Custom comparator for LearnerExperience objects; used for sorting data tables.
 * @author bjones86
 */
public class LearnerExperienceComparator implements Comparator<LearnerExperience>, Serializable
{
    /**
     * The available comparison types for LearnerExperience objects
     */
    public static enum CompType
    {
        Learner, AttemptDate, Status, NumberOfAttempts, progress, score, completionStatus
    }

    public static final CompType DEFAULT_COMP = CompType.Learner;
    private CompType compType = DEFAULT_COMP;

    /**
     * Sets the comparison type the comparator will use. This determines which field of LearnerExperience is used for comparisons.
     * @param value the comparison type
     */
    public void setCompType( CompType value )
    {
        if( value != null )
        {
            compType = value;
        }
    }

    @Override
    public int compare( LearnerExperience le1, LearnerExperience le2 )
    {
        // Perform different comparison depending on which comparison type has been selected
        switch( compType )
        {
            case Learner:
            {
                return le1.getLearnerName().compareTo( le2.getLearnerName() );
            }
            case AttemptDate:
            {
                return ObjectUtils.compare( le1.getLastAttemptDate(), le2.getLastAttemptDate() );
            }
            case Status:
            {
                return Integer.compare( le1.getStatus(), le2.getStatus() );
            }
            case NumberOfAttempts:
            {
                return Integer.compare( le1.getNumberOfAttempts(), le2.getNumberOfAttempts() );
            }
            case score:
            {          		
            	if(null == le1.getScoreNumber()){
            		le1.setScoreNumber(new Long(-1));
            		
            	}
            	if(null == le2.getScoreNumber()){
            		le2.setScoreNumber(new Long(-1));
            		
            	}
            	return Long.compare(le1.getScoreNumber(), le2.getScoreNumber());          	
            }
            case completionStatus:
            {
            	if(null == le1.getCompletedStatus()){
            		le1.setCompletedStatus("");
            	}
            	if(null == le2.getCompletedStatus()){
            		le2.setCompletedStatus("");
            	}
            	return le1.getCompletedStatus().compareTo( le2.getCompletedStatus());            	
            }
            case progress:
            {     
            	if(null == le1.getProgressNumber()){
            		le1.setProgressNumber(new Long(-1));
            		
            	}
            	if(null == le2.getProgressNumber()){
            		le2.setProgressNumber(new Long(-1));
            		
            	}
            	return Long.compare(le1.getProgressNumber(), le2.getProgressNumber());                	
            }
        }

        return 0;
    }
}
