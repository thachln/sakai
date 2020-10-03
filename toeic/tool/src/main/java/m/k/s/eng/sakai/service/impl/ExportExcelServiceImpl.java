package m.k.s.eng.sakai.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import m.k.s.eng.sakai.model.ItemResult;
import m.k.s.eng.sakai.model.ToeicFeedback;
import m.k.s.eng.sakai.service.AssessmentToeicService;
import m.k.s.eng.sakai.service.ExportExcelService;
import mksgroup.java.common.CommonUtil;
import mksgroup.java.poi.PoiUtil;

@Service("exportExcelService")
public class ExportExcelServiceImpl implements ExportExcelService {
    final static protected Log LOG = LogFactory.getLog(ExportExcelServiceImpl.class);
    private static final String FMT_DT_DISPLAY = "yyyy-MM-dd HH:mm:ss";

    // private static final List<String> assessmentHeaders = Arrays.asList("ASSESSMENTGRADINGID", "TITLE",
    // "TIME ELAPSED (Minutes)", "SUBMITTED DATE", "FINAL SCORE");

    // private static final List<String> itemHeaders = Arrays.asList("QUESTION", "PART", "ANSWER", "USER'S ANSWER",
    // "ISCORRECT");
    // private static final List<String> itemHeaders = Arrays.asList("QUESTION", "ANSWER", "USER'S ANSWER",
    // "ISCORRECT");
    private GradingService gradingService = new GradingService();

    @Autowired
    @Qualifier("assessmentToeicService")
    private AssessmentToeicService assessmentToeicService;
    /**
     * Export for only one chose assessment; 59: load AssessmentGradingData and its itemGradingSet from database; 60:
     * load PublishedAssessmentData's title from database; 64-65: create fileName and set file's content type; 69-83:
     * set response header, create workbook, create a excel cell's style; 86-88: create a sheet from workbook and call
     * function writeSheet() (code line: 329); 90-120: write file;
     * @param assessmentGradingId
     * @param response
     * @throws IOException
     * @see m.k.s.eng.sakai.service.ExportExcelService#exportAssessmentResult(java.lang.String,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void exportAssessmentResult(String assessmentGradingId, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Try to new algorithm
        AssessmentGradingData assessment = gradingService
                .loadAssessmentGradingDataOnly(Long.valueOf(assessmentGradingId));
        // AssessmentGradingData assessment = gradingService.load(assessmentGradingId);
        String title = gradingService.getPublishedAssessmentTitle(Long.valueOf(assessmentGradingId));
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String fileName = AgentFacade.getDisplayNameByAgentId(assessment.getAgentId()) + "_" + title + "_" + timeStamp;
        response.setContentType("application/vnd.ms-excel");

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

        // XSSFWorkbook workbook = new XSSFWorkbook();
        Workbook workbook = PoiUtil.loadWorkbookByResource("/templates/assessment_result_template.xlsx");

        // XSSFSheet sheet = workbook.createSheet("Result");
        // Get the first sheet
        Sheet sheet = workbook.getSheetAt(0);

        DeliveryBean deliveryBean = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request,
                response);

        deliveryBean.setAssessmentGrading(assessment);
        deliveryBean.setAssessmentTitle(title);

        writeSheet1(sheet, deliveryBean);

        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(response.getOutputStream());
            workbook.write(outStream);
        } catch (IOException ex) {
            LOG.error("Could not getOutputStream...", ex);
            throw ex;
        } finally {
            try {
                if (outStream != null) {
                    outStream.flush();
                }
            } catch (IOException ex) {
                LOG.error("Could not flush....", ex);
                throw ex;
            }
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException ex) {
                LOG.error("Could not close....", ex);
                throw ex;
            }
            try {
                workbook.close();
            } catch (IOException ex) {
                LOG.error("Could not close workbook...", ex);
                throw ex;
            }
        }

    }

    /**
     * Export all AssessmentGradingData were graded. get PublishedAssessmentData's title from database; create file
     * name, workbook, cell's style and set response header; get List<AssessmentGradingData> assessments from database
     * with publishedAssessmentId; create List<List<AssessmentGradingData>> listAgByName - list of All
     * AssessmentGradingData of one user; Map<String, Integer> listAgMap; group AssessmentGradingData by user name then
     * write each of them to sheetSummary and detail.
     * @param publishedAssessmentId
     * @param response
     * @throws IOException
     * @see m.k.s.eng.sakai.service.ExportExcelService#exportAllAssessmentResult(java.lang.Long,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void exportAllAssessmentResult(Long publishedAssessmentId, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // XSSFWorkbook workbook = new XSSFWorkbook();
        Workbook workbook = PoiUtil.loadWorkbookByResource("/templates/assessment_result_all_template.xlsx");

        // Try new algorithm
        List<AssessmentGradingData> assessments = gradingService.getAllAssessmentGradingDataOnly(publishedAssessmentId);
        // List<AssessmentGradingData> assessments = gradingService.getAllAssessmentGradingData(publishedAssessmentId);
        List<List<AssessmentGradingData>> listAgByName = new ArrayList<List<AssessmentGradingData>>();
        Map<String, Integer> listAgMap = getListAssessmentGradingMap(assessments);
        listAgMap.entrySet().stream().sorted(Map.Entry.comparingByValue());

        String title = gradingService
                .getPublishedAssessmentTitle(Long.valueOf(assessments.get(0).getAssessmentGradingId()));
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = title + "_" + timeStamp;
        response.setContentType("application/vnd.ms-excel");

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xlsx");

        int length;
        int index;
        List<AssessmentGradingData> temps;

        // group AssessmentData by user name.
        for (AssessmentGradingData ag : assessments) {
            if (ag.getForGrade()) {
                index = listAgMap.get(ag.getAgentId());
                length = listAgByName.size();

                // if listAgByName is null, create a new list and add to listAgByName
                if (length < 1) {
                    temps = new ArrayList<AssessmentGradingData>();
                    temps.add(ag);
                    listAgByName.add(temps);

                    // if list Assessment of user name exists get that list and add AssessmentGradingData to that list
                } else if (index < length) {
                    temps = listAgByName.get(index);
                    temps.add(ag);
                    listAgByName.set(index, temps);

                    // if not create a new list and add to listAgByName
                } else if (index >= length) {
                    temps = new ArrayList<AssessmentGradingData>();
                    temps.add(ag);
                    listAgByName.add(temps);
                }
            }
        }

        // String sheetName; // Sheet name has format: user_yyyyMMdd-HHmm"
        // AssessmentGradingData assessmentGradingData;
        // Sheet sheet;

        // Write the title in sheet "Summary"
        PoiUtil.setContent(workbook.getSheetAt(0), "B1", title);

        DeliveryBean deliveryBean = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet("delivery", request,
                response);

        // Write detailed data for sheet "Summary" and others.
        for (List<AssessmentGradingData> ags : listAgByName) {
            // assessmentGradingData = ags.get(0);

            // sheetName = AgentFacade.getDisplayNameByAgentId(assessmentGradingData.getAgentId()) +
            // CommonUtil.formatDate(assessmentGradingData.getSubmittedDate(), "_yyyyMMdd-HHmm");
            // sheet = workbook.createSheet(sheetName);
            // Clone the first sheet which is used as template.
            // sheet = workbook.cloneSheet(1);

            writeSheet1(workbook, ags, title, deliveryBean);
        }

        // Delete sheet no 1 "TEMPATE_Deatils"
        workbook.removeSheetAt(1);

        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(response.getOutputStream());
            workbook.write(outStream);
        } catch (IOException ex) {
            LOG.error("Could not getOutputStream...", ex);
            throw ex;
        } finally {
            try {
                if (outStream != null) {
                    outStream.flush();
                }
            } catch (IOException ex) {
                LOG.error("Could not flush....", ex);
                throw ex;
            }
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException ex) {
                LOG.error("Could not close....", ex);
                throw ex;
            }
            try {
                workbook.close();
            } catch (IOException ex) {
                LOG.error("Could not close workbook...", ex);
                throw ex;
            }
        }
    }

    // Old code
    private void writeSheet1(Sheet sheet, DeliveryBean deliveryBean) {
        // Write title
        PoiUtil.setContent(sheet, "B1", deliveryBean.getAssessmentTitle());

        String timeElapsed = getDisplayTimeElapsed(deliveryBean.getAssessmentGrading().getTimeElapsed());

        // Write timeElapsed
        PoiUtil.setContent(sheet, "B2", timeElapsed);

        String strSubmittedDate;
        Date submittedDate = deliveryBean.getAssessmentGrading().getSubmittedDate();

        strSubmittedDate = (submittedDate != null) ? CommonUtil.formatDate(submittedDate, FMT_DT_DISPLAY) : "";

        // Write submitted date
        PoiUtil.setContent(sheet, "D2", strSubmittedDate);

        // Write listening and reading scores.
        ToeicFeedback scores = assessmentToeicService
                .calculateScores(deliveryBean.getAssessmentGrading().getAssessmentGradingId());
        PoiUtil.setContent(sheet, "B3", scores.getListeningScore());
        PoiUtil.setContent(sheet, "D3", scores.getReadingScore());

        // Write final core
        PoiUtil.setContent(sheet, "B4", deliveryBean.getAssessmentGrading().getFinalScore());

        // sort question by question's number.
        // Ignore issue in case .getAnswerText is null
        // This ignore to demo in case the save data has error
        // List<ItemGradingData> sortedItems = assessmentGradingdata.getItemGradingSet().stream().sorted((item1,
        // // item2) -> Integer.valueOf(item1.getAnswerText()).compareTo(Integer.valueOf(item2.getAnswerText())))
        // item2) -> Integer.valueOf(item1.getAnswerText() != null ? item1.getAnswerText() : "0")
        // .compareTo(Integer.valueOf(item2.getAnswerText() != null ? item2.getAnswerText() : "0")))
        // .collect(Collectors.toList());

        // Write details of question after header row.
        writeAssessment(sheet, 6, deliveryBean);

        // return sheet;
    }

    /**
     * [Give the description for method].
     * @param assessmentGradingdata
     * @return
     */
    private String getDisplayTimeElapsed(Integer timeElapsed) {
        if (timeElapsed == null) {
            return "";
        }

        String firstParam = ((int) (timeElapsed / 60) < 10)
                ? ("0" + (int) (timeElapsed / 60))
                : String.valueOf((int) (timeElapsed / 60));

        String secondParam = ((int) (timeElapsed % 60) < 10)
                ? ("0" + (int) (timeElapsed % 60))
                : String.valueOf((int) (timeElapsed % 60));

        String strTimeElapsed = firstParam + ":" + secondParam;

        return strTimeElapsed;
    }

    /**
     * write assessment detail result with new method.
     * @param sheet
     * @param rowCount
     * @param sortedItems
     */
    private void writeAssessment(Sheet sheet, int rowCount, DeliveryBean deliveryBean) {
        int columnCount;
        Cell cell;
        Row row;

        List<ItemResult> items = assessmentToeicService.getItemResults(
                deliveryBean.getAssessmentGrading().getAssessmentGradingId(),
                deliveryBean.getAssessmentGrading().getPublishedAssessmentId(), deliveryBean);

        List<ItemResult> sorted = items.stream().sorted(
                (item1, item2) -> Integer.valueOf(item1.getQuestion()).compareTo(Integer.valueOf(item2.getQuestion())))
                .collect(Collectors.toList());

        for (ItemResult i : sorted) {
            // reset column index.
            columnCount = 0;
            row = sheet.createRow(rowCount++);

            cell = row.createCell(columnCount++);
            cell.setCellValue(i.getQuestion());

            cell = row.createCell(columnCount++);
            cell.setCellValue(i.getAnswer());

            cell = row.createCell(columnCount++);
            cell.setCellValue(i.getUserAnswer());

            cell = row.createCell(columnCount++);
            cell.setCellValue(i.getIsCorrect());
        }
        // List<Object[]> irs = Lists.newArrayList(gradingService.getItemResults(assessmentGradingId));
        //
        // List<Object[]> sorted = irs.stream()
        // .sorted((item1, item2) -> Integer
        // .valueOf((!item1[0].equals(null)) ? item1[0].toString().split("-")[0] : "0")
        // .compareTo(Integer.valueOf((!item2[0].equals(null)) ? item2[0].toString().split("-")[0] : "0")))
        // .collect(Collectors.toList());
        // String qNumAndAnswerText[];
        //
        // for (Object[] objectRow : sorted) {
        //
        // // reset column index.
        // columnCount = 0;
        // row = sheet.createRow(rowCount++);
        //
        // qNumAndAnswerText = objectRow[0].toString().split("-"); // ex: answerText="190-A". Work around for export
        // // with random answers
        //
        // cell = row.createCell(columnCount++);
        // // cell.setCellValue(objectRow[0].toString());
        // cell.setCellValue(qNumAndAnswerText[0].toString()); // work around for export with random answers
        //
        // cell = row.createCell(columnCount++);
        // // cell.setCellValue((objectRow[2].toString() != null) ? objectRow[2].toString() : "");
        // cell.setCellValue(""); // not support answer column yet.
        //
        // cell = row.createCell(columnCount++);
        // // cell.setCellValue((objectRow[3].toString() != null) ? objectRow[3].toString() : "");
        // cell.setCellValue(qNumAndAnswerText[1].toString()); // work around for export with random answers
        //
        // cell = row.createCell(columnCount++);
        // cell.setCellValue(objectRow[4].toString());
        // }
    }

    /**
     * Old Write summary score into the Summary sheet and details in other ones.
     * @param workbook
     * @param assessmentGradingdataList
     * @param headerStyle
     * @param title
     */
    private void writeSheet1(Workbook workbook, List<AssessmentGradingData> assessmentGradingdataList, String title,
            DeliveryBean deliveryBean) {
        Sheet sheet;
        String sheetName;

        // Get Sheet Summary
        Sheet sheetSummary = workbook.getSheetAt(0);

        // sort list by attempt date.
        assessmentGradingdataList.stream().sorted((a, b) -> a.getAttemptDate().compareTo(b.getAttemptDate()));

        for (AssessmentGradingData assessmentGradingdata : assessmentGradingdataList) {

            // Write summary score into the first sheet
            writeSummaryScore(sheetSummary, assessmentGradingdata);

            // Clone the first sheet which is used as template.
            sheet = workbook.cloneSheet(1);
            int lastRowNum = sheetSummary.getLastRowNum();

            sheetName = String.valueOf((lastRowNum - 2));
            workbook.setSheetName(workbook.getSheetIndex(sheet), sheetName);

            deliveryBean.setAssessmentGrading(assessmentGradingdata);
            deliveryBean.setAssessmentTitle(title);

            writeSheet1(sheet, deliveryBean);
        }
    }

    /**
     * Write final score into the sheet Summary.
     * @param sheetSummary
     * @param assessmentGradingdata
     */
    private void writeSummaryScore(Sheet sheetSummary, AssessmentGradingData assessmentGradingdata) {
        String displayName = AgentFacade.getDisplayNameByAgentId(assessmentGradingdata.getAgentId());

        int lastRowNum = sheetSummary.getLastRowNum();

        Row row = sheetSummary.createRow(lastRowNum + 1);

        int colIdx = 0;

        // Belong to the template, the last row is header at row 3 (start from 0). So the sequence no starts at
        // lastRowNum - 1
        PoiUtil.setContent(row, colIdx++, lastRowNum - 1);

        AgentFacade agent = new AgentFacade(assessmentGradingdata.getAgentId());
        String username = agent.getEidString();
        PoiUtil.setContent(row, colIdx++, username);
        PoiUtil.setContent(row, colIdx++, displayName);
        String strSubmittedDate = CommonUtil.formatDate(assessmentGradingdata.getSubmittedDate(), FMT_DT_DISPLAY);
        PoiUtil.setContent(row, colIdx++, strSubmittedDate);

        String timeElapsed = getDisplayTimeElapsed(assessmentGradingdata.getTimeElapsed());
        PoiUtil.setContent(row, colIdx++, timeElapsed);
        PoiUtil.setContent(row, colIdx++, null);
        PoiUtil.setContent(row, colIdx++, null);
        PoiUtil.setContent(row, colIdx++, assessmentGradingdata.getFinalScore());

    }

    /**
     * Create a Map with key is userEid and value is index of list
     * @param assessmentGs
     * @return
     */
    private Map<String, Integer> getListAssessmentGradingMap(List<AssessmentGradingData> assessmentGs) {
        Map<String, Integer> listAgMap = new HashMap<String, Integer>();
        int index = 0;

        for (AssessmentGradingData a : assessmentGs) {
            if (listAgMap.containsKey(a.getAgentId())) {
                continue;
            } else {
                listAgMap.put(a.getAgentId(), index);
                index++;
            }
        }

        return listAgMap;
    }
}
