package mksgroup.sakai.app.toeicbuilder.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class ExamBuilderServiceTest {

    @Test
    void testParseExam1() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 1-20180918T130656Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%201-20180918T130656Z-001-Processed";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam2() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 2-20180918T131630Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%202-20180918T131630Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam3() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 3-20180918T132714Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%203-20180918T132714Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam4() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 4-20180918T133655Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%204-20180918T133655Z-001%20-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam5() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 5-20180918T134523Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%205-20180918T134523Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    @Test
    void testParseExam6() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 6-20180918T134937Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%206-20180918T134937Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam7() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 7-20180918T141039Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%207-20180918T141039Z-001_Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    @Test
    void testParseExam8() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 8-20180920T024459Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%208-20180920T024459Z-001-Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam9() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 9-20180920T024626Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%209-20180920T024626Z-001%20-%20Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    void testParseExam10() {
        String folder = "E:\\MyProjects\\cl.fsoft.com.vn-gitlab\\toeic-data\\gitlab\\toeic\\toeic-data\\Test 10-20180920T024657Z-001-Processed";
        String rootUrl = "http://localhost:8080/access/content/group/0b684f71-10a3-453d-9133-5cb401afe92e/TOEIC/Toeic-Exam/Test%2010-20180920T024657Z-001_Processed/";
        String outFolder = folder + File.separatorChar + "output";
        ExamBuilderService ebService = new ExamBuilderService(folder, rootUrl, outFolder);

        try {
            String outputExcelFilePath = ebService.parse();
            assertNotNull(outputExcelFilePath);
            assertNotSame("", outputExcelFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
