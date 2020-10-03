package m.k.s.eng.sakai.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExportExcelService {

    public void exportAssessmentResult(String assessmentGradingId, HttpServletRequest request,
            HttpServletResponse response) throws IOException;

    public void exportAllAssessmentResult(Long publishedAssessmentId, HttpServletRequest request,
            HttpServletResponse response) throws IOException;

}
