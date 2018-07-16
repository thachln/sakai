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
package org.sakaiproject.scorm.ui.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Interaction;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.BasicResultPerContentP;
import org.sakaiproject.scorm.ui.NameValuePair;
import org.sakaiproject.scorm.ui.SummaryRecord;
import org.sakaiproject.scorm.ui.console.pages.SummaryResultPage;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.scorm.ui.reporting.pages.AttemptDataProvider;

/**
 * @author HT
 *
 */
public class ReportJob implements Job {
    
    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormResultService")
    ScormResultService resultService;
    
    @SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
    ScormContentService contentService;
    
    private static final Log LOG = LogFactory.getLog(ReportJob.class);
    
    /**
     * [Explain the description for this method here].
     * @param arg0
     * @throws JobExecutionException
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        File dir = new File("/reports");
        
        boolean created = false;
        if (!dir.exists()) {
            created = dir.mkdirs(); 
        }
        
            File file = new File(dir, "report1.xlsx");
        try {
            generateExportExcel(file,"cae66e97-fdce-4a07-9702-c8839b0628db");
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        //note
        String val =  "groupId = 403b5502-4f40-42e6-82f7-d0f865b3502b siteId = cae66e97-fdce-4a07-9702-c8839b0628db";
    }

    private SummaryRecord createSecondHeader(SummaryRecord firstRec){
        if(null != firstRec){
            int numberOfContent = firstRec.getLstResult().size();
            List<BasicResultPerContentP> lstResult = new ArrayList<>();
            for(int i = 0; i < numberOfContent; i++){
                BasicResultPerContentP item = new BasicResultPerContentP();
                item.setCompletedStatus("Status");
                item.setDuration("Duration");
                item.setScoreStr("Score");
                item.setStartDateStr("Start Date");
                lstResult.add(item);
            }
            
            SummaryRecord result = new SummaryRecord("", lstResult);
            return result;
        } else {
            return null;
        }
        
    }
    
    
    /**
     * convert Map to List
     * @param lstSummary
     * @return
     */
    private List<SummaryRecord> convertMapToSummaryList(Map<NameValuePair,SummaryRecord> lstSummary) {
        List<SummaryRecord> result = new ArrayList<>();
        for (Map.Entry<NameValuePair, SummaryRecord> entry : lstSummary.entrySet()){
            SummaryRecord tmpRecord= entry.getValue();
            
            result.add(tmpRecord);
        }
        return result;
    }
    
    /**
     * Create data Content Package List for list View Component
     * @return
     */
    private List<NameValuePair> createContentPackageData(){
        List<ContentPackage> lst = contentService.getContentPackages();
        LOG.debug("VinhVc.Debug.createContentPackageData: " + lst);
        List<NameValuePair> lstListViewData = new ArrayList<>();
        if(null != lst) {
            for(ContentPackage tmp: lst) {
                NameValuePair value = new NameValuePair(tmp.getContentPackageId().toString().toString(), tmp.getTitle());
                lstListViewData.add(value);
            }
        }
        return lstListViewData;
    }
    
    /**
     * Create List content Package List View Component
     * @return
     */
    private ListView createListViewComponent() {
        List<NameValuePair> lstListViewData = createContentPackageData();
        @SuppressWarnings("unchecked")
        ListView listView = new ListView("listContentPackages", lstListViewData)
        {
            protected void populateItem(ListItem item)
            {
                 NameValuePair wrapper = (NameValuePair)item.getModelObject();
                 item.add(new CheckBox("check", new PropertyModel(wrapper, "selected")){
                     protected boolean wantOnSelectionChangedNotifications() {
                            return true;
                        }
                 });
                 item.add(new Label("name", wrapper.getName()));            
            }
             
        };
        
        listView.setReuseItems(true);
        return listView;
        
    }
    
    private List<NameValuePair> getLstSelectedItem(List<NameValuePair> selectedList){
        List<NameValuePair> result = new ArrayList<>();
        int sizeContent = selectedList.size();
        for(int i = 0; i < sizeContent; i++){
            if(((NameValuePair)selectedList.get(i)).getSelected()){
                result.add(selectedList.get(i));
            }
        }
        return result;
    }
    
    /**
     * Export Excel
     * @param tempFile
     * @param context
     * @throws IOException
     */
    public void generateExportExcel(File tempFile, String context) throws IOException {
        FileOutputStream os = new FileOutputStream(tempFile);
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a blank spreadsheet
        XSSFSheet spreadsheet = workbook.createSheet("Results");        
        // Create row object
        XSSFRow row;
        List<Object[]> lstData = new ArrayList<Object[]>();
        
        List<NameValuePair> selectedList = createContentPackageData();
        // This Header needs to be written (Object[])
        Object[] header = buildHeaderExportExcel(selectedList);
        lstData.add(header);
        lstData.addAll(createData("403b5502-4f40-42e6-82f7-d0f865b3502b", context,selectedList));
        
        // export
        int rowid = 0;
        for (Object[] objLst : lstData) {
            row = spreadsheet.createRow(rowid++);
            int cellid = 0;
            for (Object obj : objLst) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);                                
            }
        }
        
        int size = header.length;
        spreadsheet.autoSizeColumn(0);
        int sizeContent = selectedList.size();
        int indx = 1;
        for(int i = 0; i < sizeContent; i++){
            if(((NameValuePair)selectedList.get(i)).getSelected()){
                spreadsheet.autoSizeColumn(indx++);
                indx++;
            }
        }
        
        // merge header
        List<CellRangeAddress> cellRangeLst = createMergeCell(selectedList);
        for(CellRangeAddress tmp: cellRangeLst){
            spreadsheet.addMergedRegion(tmp);
        }
        
        workbook.write(os);
        
        os.close();
    }
    
    /**
     * create list cell merge for excel export
     * @param lstContentPackage
     * @return
     */
    private List<CellRangeAddress> createMergeCell(List<NameValuePair> lstContentPackage){
        List<CellRangeAddress> result = new ArrayList<>();
        int sizeContent = lstContentPackage.size();
        int indx = 0;
        for(int i = 0; i < sizeContent; i++){
            if(((NameValuePair)lstContentPackage.get(i)).getSelected()){
                int startCol = i*4 + 1;
                int lastCol = startCol + 3;
                CellRangeAddress cellRange = new CellRangeAddress(0, 0, startCol, lastCol);
                result.add(cellRange);
            }
        }
        return result;
    }
    
    /**
     * buid header excel
     * @param lstContentPackage
     * @return
     */
    private Object[] buildHeaderExportExcel(List<NameValuePair> lstContentPackage){     
        int sizeContent = lstContentPackage.size();
        int size = sizeContent*4 + 1;
        Object[] tmpObj = new Object[size];
        tmpObj[0] = "Learner Name";
        int indx = 1;
        for(int i = 0; i < sizeContent; i++){
            if(((NameValuePair)lstContentPackage.get(i)).getSelected()){
                tmpObj[indx++] =  ((NameValuePair)lstContentPackage.get(i)).getName();
                tmpObj[indx++] =  "";
                tmpObj[indx++] =  "";
                tmpObj[indx++] =  "";
            }           
        }
        return tmpObj;
    }
    
    /**
     * create data for export excel
     * @param lstMember
     * @param lstContentPackage
     * @return
     */
    private List<Object[]> createData(String groupId, String context, List<NameValuePair> lstContentPackage) {
        Map<NameValuePair,SummaryRecord> data = createMapSummaryRecord(groupId,context,lstContentPackage);
        SummaryRecord firstRec = null;
        for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()) {
            firstRec = entry.getValue();
            break;
        }
        List<Object[]> result = new ArrayList<>();
        SummaryRecord itemHeader = createSecondHeader(firstRec);
        NameValuePair firstValue = new NameValuePair("", "");
        Map<NameValuePair, SummaryRecord> fisrtItemMap = new HashMap<>();
        fisrtItemMap.put(firstValue, itemHeader);
        createObjectsFromMap(fisrtItemMap, result);
        createObjectsFromMap(data, result);
        
        return result;      
    }
    
    private void createObjectsFromMap(Map<NameValuePair,SummaryRecord> data, List<Object[]> result){
        for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()){
            SummaryRecord tmpRecord= entry.getValue();
            
            if (tmpRecord == null) {
                return;
            }

            List<BasicResultPerContentP> lstResult = tmpRecord.getLstResult();
            
            if (lstResult == null) {
                return;
            }

            int sizeContent = lstResult.size();
            int size = sizeContent*4 + 1;
            Object[] tmpObj = new Object[size];
            tmpObj[0] = tmpRecord.getUserName();
            int indx = 1;
            BasicResultPerContentP rowResult;
            for (int i = 0; i < sizeContent; i++) {
                rowResult = lstResult.get(i);
                tmpObj[indx++] = rowResult.getStartDateStr();
                tmpObj[indx++] = rowResult.getDuration();
                tmpObj[indx++] = rowResult.getScoreStr();
                tmpObj[indx++] = rowResult.getCompletedStatus();
            }

            result.add(tmpObj);
        }
    }
    
    /**
     * Create Map data
     * @param groupId
     * @param context
     * @param lstContentPackage
     * @return
     */
    private Map<NameValuePair,SummaryRecord> createMapSummaryRecord(String groupId, 
                                                                    String context, 
                                                                    List<NameValuePair> lstContentPackage){
        //1. create list ID
        List<String> lstMemberId = new ArrayList<>();
        List<Member> lstMember = resultService.getAllMemberInGroup(groupId,context);
        if(lstMember != null){
            for(Member tmp: lstMember){
                lstMemberId.add(tmp.getUserDisplayId());
            }
        }
        // 2. create list ContentPackage
        List<Long> lstContentPackageId = new ArrayList<>();
        if(lstContentPackage != null){
            for(NameValuePair p: lstContentPackage){                
                if(p.getSelected()){
                    lstContentPackageId.add(new Long(p.getValue()));
                }
            }
        }
        
        Map<NameValuePair, SummaryRecord> data = new HashMap<>();
        
        for(Long contentId: lstContentPackageId){
            ContentPackage contentPackage = contentService.getContentPackage(contentId.longValue());
            AttemptDataProvider dataProvider = new AttemptDataProvider(contentId.longValue(), null,
                                                    contentPackage,null,null, lstMemberId, true,resultService);
            Map<NameValuePair,BasicResultPerContentP> oneContent = createResultPerContentPackage(dataProvider, contentPackage);
            appendData(data,oneContent);
        }
        return data;
    }
    
    /**
     * For export Detail
     * @param groupId
     * @param context
     * @param lstContentPackage
     * @return
     */
    private List<AttemptDataProvider> createAttemptDataProviders(String groupId, String context,
                                                                List<NameValuePair> lstContentPackage) {
        List<AttemptDataProvider> result = new ArrayList<>();
        // 1. create list ID
        List<String> lstMemberId = new ArrayList<>();
        List<Member> lstMember = resultService.getAllMemberInGroup(groupId, context);
        if (lstMember != null) {
            for (Member tmp : lstMember) {
                lstMemberId.add(tmp.getUserDisplayId());
            }
        }
        // 2. create list ContentPackage
        List<Long> lstContentPackageId = new ArrayList<>();
        if (lstContentPackage != null) {
            for (NameValuePair p : lstContentPackage) {
                if (p.getSelected()) {
                    lstContentPackageId.add(new Long(p.getValue()));
                }
            }
        }

        Map<NameValuePair, SummaryRecord> data = new HashMap<>();

        for (Long contentId : lstContentPackageId) {
            ContentPackage contentPackage = contentService.getContentPackage(contentId.longValue());
            AttemptDataProvider dataProvider = new AttemptDataProvider(contentId.longValue(), null, contentPackage,
                    null, null, lstMemberId, true, resultService);
            result.add(dataProvider);
        }
        return result;
    }
    
    /**
     * Append data on lesson (content package) to list
     * @param data
     * @param oneContent
     */
    private void appendData(Map<NameValuePair, SummaryRecord> data,Map<NameValuePair,BasicResultPerContentP> oneContent) {
        if(!data.isEmpty()){
            for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()){
                if(oneContent.containsKey(entry.getKey())){
                    BasicResultPerContentP tmp = oneContent.get(entry.getKey());
                    data.get(entry.getKey()).setUserName(entry.getKey().getName());
                    data.get(entry.getKey()).getLstResult().add(tmp);
                }
            }
        } else {
            for (Map.Entry<NameValuePair, BasicResultPerContentP> entry : oneContent.entrySet()){
                List<BasicResultPerContentP> lsBasic = new ArrayList<>();
                lsBasic.add(entry.getValue());
                SummaryRecord sRecord = new SummaryRecord(entry.getKey().getName(),lsBasic);
                data.put(entry.getKey(), sRecord);
            }
        }
        
    }
    
    /**
     * create Map User and result of One ContentPackage
     * @param attemptProvider
     * @return
     */
    @SuppressWarnings("unused")
    private Map<NameValuePair, BasicResultPerContentP> createResultPerContentPackage( AttemptDataProvider attemptProvider,
                                                                                    ContentPackage contentPackage)
    {
        // Create the column headers
        Map<NameValuePair, BasicResultPerContentP> resultBasic= new HashMap<>();
        
        Iterator<LearnerExperience> itr = attemptProvider.iterator( 0, attemptProvider.size() );
        while( itr.hasNext() )
        {
            // Learner info         
            LearnerExperience learner = itr.next();
            BasicResultPerContentP basic = null;
                        
            // Get the summaries for all attempts for the current user
            List<ActivitySummary> summaries = new ArrayList<>();
            for( int i = 1; i <= learner.getNumberOfAttempts(); i++ )
            {
                summaries.addAll( resultService.getActivitySummaries( learner.getContentPackageId(), learner.getLearnerId(), i ) );
            }
            NameValuePair learnerInfo = new NameValuePair(learner.getLearnerId(),learner.getLearnerName());
            if( summaries.isEmpty() )
            {
                if(!resultBasic.containsKey(learner.getLearnerId())){
                    basic = new BasicResultPerContentP(learner.getContentPackageId(), 
                                                        "","","","");
                    resultBasic.put(learnerInfo, basic);
                }
            } else {
                // Summary info
                ActivitySummary summary = null;
                ActivityReport report = null;
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                if(null != contentPackage.getGetHighestResult() 
                        && contentPackage.getGetHighestResult()){
                    summary = getMaxHighResultInListAttempt(summaries);
                    report = resultService.getActivityReport(summary.getContentPackageId(), summary.getLearnerId(), summary.getAttemptNumber(), summary.getScoId());
                } else {
                    summary = summaries.get(summaries.size() - 1);
                    report = resultService.getActivityReport(summary.getContentPackageId(), summary.getLearnerId(), summary.getAttemptNumber(), summary.getScoId());
                }
                
                List<Interaction> interactions = report.getInteractions();
                String submitTime = "";
                
                if(!resultBasic.containsKey(learner.getLearnerId())){
                    basic = new BasicResultPerContentP(summary.getContentPackageId(), 
                                                        summary.getCompletionStatus(), 
                                                        Utils.getNumberPercententString(summary.getScaled()),
                                                        sdf.format(learner.getLastAttemptDate()),
                                                        submitTime);
                    basic.setDuration(summary.getTotalSessionSecondsDisplay());
                    basic.setStartDate(summary.getStartDate());
                    basic.setStartDateStr(sdf.format(summary.getStartDate()));
                    resultBasic.put(learnerInfo, basic);
                }
            }       

        }
        return resultBasic;
    }
    
    
    /**
     * get In of Object has Max Result In List Attemp
     * @param summaries
     * @return
     */
    private ActivitySummary getMaxHighResultInListAttempt(List<ActivitySummary> summaries){     
        int size = summaries.size();
        ActivitySummary maxActivity = summaries.get(0);
        for(int i = 1; i < size; i++){
            ActivitySummary curActivity = summaries.get(i);
            if(maxActivity.compareActivitySummary(curActivity) < 0){
                maxActivity = curActivity;
            }
        }
        return maxActivity;
    }
}
