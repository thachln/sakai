package m.k.s.eng.sakai.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ToeicPicture;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import m.k.s.eng.sakai.model.FaceRecognitionModel;
import m.k.s.eng.sakai.model.PictureModel;
import m.k.s.eng.sakai.util.AppUtil;

@Controller
public class PictureController {

    private GradingService gradingService = new GradingService();

    @Value("${hawk.recognize}")
    String hawkRecognize;

    @Value("${hawk.create}")
    String hawkCreate;

    @Value("${hawk.add}")
    String hawkAdd;

    @Value("${hawk.listStaff}")
    String hawkListStaff;

    @PostMapping("picture/find-by-assessmentGradingId")
    @ResponseBody
    public List<PictureModel> findByAssessmentGradingId(@RequestParam("idG") Long idG) {
        String siteId = AgentFacade.getCurrentSiteId();
        String role = AgentFacade.getRoleForAgentAndSite(AgentFacade.getAgentString(), siteId);
        List<PictureModel> result = new ArrayList<PictureModel>();
        PictureModel model = new PictureModel();

        if (role.equals("maintain") || role.equals("Teaching Assistant") || role.equals("Instructor")) {
            AssessmentGradingData agd = gradingService.loadAssessmentGradingDataOnly(idG);
            List<ToeicPicture> pics = PersistenceService.getInstance().getAssessmentGradingFacadeQueries()
                    .getPictureByAgentId(agd.getAgentId(), idG);
            for (ToeicPicture p : pics) {
                model = new PictureModel();
                model.setId(p.getId());
                model.setType(p.getContentType());
                result.add(model);
            }
        }

        return result;
    }

    @GetMapping("picture/{id}")
    public void showImage(@PathVariable("id") Long id, HttpServletResponse response, HttpServletRequest request)
            throws ServletException, IOException {

        ToeicPicture p = gradingService.loadToeicPicture(id);
        response.setContentType(p.getContentType());
        response.getOutputStream().write(p.getContent());

        response.getOutputStream().close();
    }

    @PostMapping(value = "get-face-regconition")
    @ResponseBody
    public FaceRecognitionModel getFaceRecognition(@RequestParam("idA") Long id) {
        String desc = gradingService.getPublishedAssessmentDescription(id);
        FaceRecognitionModel model = AppUtil.getFaceRecognition(desc);

        return model;
    }

    @PostMapping(value = "picture/save")
    @ResponseBody
    public String saveImages(@RequestParam("idG") Long idG, @RequestParam("image") MultipartFile[] images) {
        ToeicPicture p;
        List<ToeicPicture> pics = new ArrayList<ToeicPicture>();
        for (MultipartFile i : images) {
            try {
                p = new ToeicPicture();
                p.setAssessmentGradingId(idG);
                p.setCreatedDate(new Date());
                p.setAgentId(AgentFacade.getAgentString());
                p.setContent(i.getBytes());
                p.setContentType(i.getContentType());

                pics.add(p);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        gradingService.saveOrUpdateToeicPicture(pics);

        return "{\"result\":\"success\"}";
    }

    @PostMapping(value = "hawk-api/staff")
    @ResponseBody
    private String getListStaffForClient() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(hawkListStaff, String.class);

        return result;
    }

    @PostMapping(value = "hawk-api/recognize")
    @ResponseBody
    private String recognize(@RequestParam("image") MultipartFile image) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        final String filename = "somefile.png";
        // map.add("name", filename);
        // map.add("filename", filename);
        ByteArrayResource contentsAsResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        map.add("image", contentsAsResource);
        String result = restTemplate.postForObject(hawkRecognize, map, String.class);

        return result;
    }

    @PostMapping(value = "hawk-api/add")
    @ResponseBody
    private String add(@RequestParam("id") Integer id, @RequestParam("images") MultipartFile[] images)
            throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        final String filename = "somefile.png";
        map.add("id", id);
        for (MultipartFile i : images) {
            ByteArrayResource contentsAsResource = new ByteArrayResource(i.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            map.add("images", contentsAsResource);
        }
        String result = restTemplate.postForObject(hawkAdd, map, String.class);

        return result;
    }

    @PostMapping(value = "hawk-api/create")
    @ResponseBody
    private String create(@RequestParam("images") MultipartFile[] images) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        final String filename = "somefile.png";
        String acc = AgentFacade.getAgentString();
        // String name = AgentFacade.getEid();
        map.add("name", acc);
        map.add("department", "TOEIC");
        // map.add("acc", acc);
        for (MultipartFile i : images) {
            ByteArrayResource contentsAsResource = new ByteArrayResource(i.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            map.add("images", contentsAsResource);
        }
        String result = restTemplate.postForObject(hawkCreate, map, String.class);

        return result;
    }

}
