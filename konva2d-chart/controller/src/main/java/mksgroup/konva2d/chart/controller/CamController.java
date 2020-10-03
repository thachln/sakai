package mksgroup.konva2d.chart.controller;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import mksgroup.konva2d.chart.biz.CamBiz;
import mksgroup.konva2d.chart.controller.editor.MotionRuleEditor;
import mksgroup.konva2d.chart.logic.SakaiProxy;
import mksgroup.konva2d.chart.model.CamModel;
import mksgroup.konva2d.chart.model.ItemKine;
import mksgroup.konva2d.chart.model.ResultInfo;
import mksgroup.konva2d.chart.model.out.AxisModel;
import mksgroup.konva2d.chart.model.out.CalDataModel;
import mksgroup.konva2d.chart.model.out.OutCamModel;

/**
 * Handles requests for the application home page.
 */
@Controller
@SessionAttributes({"listX", "listY"})
public class CamController {
    final static protected Log LOG = LogFactory.getLog(CamController.class);
//	private static final Logger LOG = Logger.getLogger(CamController.class);

    @Setter
    @Getter
    private SakaiProxy sakaiProxy = null;
	   
	   /**
     * This method is called when binding the HTTP parameter to bean (or model).
     * 
     * @param binder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        //LOG.info("initBinder");

        Class<List<ItemKine>> collectionType = (Class<List<ItemKine>>)(Class<?>)List.class;
        PropertyEditor orderNoteEditor = new MotionRuleEditor(collectionType);
        binder.registerCustomEditor((Class<List<ItemKine>>)(Class<?>)List.class, orderNoteEditor);

    }
    
	/**
	 * Simply selects the home view to render by returning its name.
     * @return 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
	    //LOG.info("Display Cam...");
		
		return "cam";
	}

   @RequestMapping(value = "/cam", method = RequestMethod.GET)
    public String displayCam() {
        LOG.info("Display Cam...");
        
        return "cam";
    }
//	@RequestMapping(value = "processCam", params = "event=Apply", method = RequestMethod.POST)
//    public @ResponseBody String processApply(@ModelAttribute("model") CamModel model, BindingResult bindingResult) {
//        //LOG.info("process event: Apply.");
//        ResultInfo result = new ResultInfo();
//        
//        if (bindingResult.hasErrors()) {
//            //LOG.error("Binding result; hasError=" + bindingResult.hasErrors());
//
//            // Log errors
//            for (ObjectError objErr : bindingResult.getAllErrors()) {
//                //LOG.error("Error=" + objErr.getCode() + ";" + objErr.getDefaultMessage());
//            }
//
//        } else {
//            // Debug
//            //LOG.debug("Thông số:" + new Gson().toJson(model));
//            
//            result.setStatus("OK");
//        }
//        
//        return new Gson().toJson(result);
//    }
	
    @RequestMapping(value = "/processCam", params = "event=Run", method = RequestMethod.POST)
    public @ResponseBody String processRun(@ModelAttribute("model") CamModel model, BindingResult bindingResult, HttpServletRequest request) {
       ResultInfo result = new ResultInfo();
       OutCamModel outCamModel = new OutCamModel(model); 
       LOG.info("process event: Run.");
        
        
        if (bindingResult.hasErrors()) {
            LOG.error("Binding result; hasError=" + bindingResult.hasErrors());

            // Log errors
            for (ObjectError objErr : bindingResult.getAllErrors()) {
                LOG.error("Error=" + objErr.getCode() + ";" + objErr.getDefaultMessage());
            }

        } else {
            // Debug
            //LOG.debug("Thông số:" + new Gson().toJson(model));
            
            // Call Business layer to calculate data for chart
            CalDataModel calData = CamBiz.calData(model.getE(), model.getAlpha(), model.getListItemKine());
            
            // Cập nhật dữ liệu tính toán để chuẩn bị xuất ra màn hình 
            outCamModel.setCalData(calData);
            
            // Thiết lập dữ liệu chuẩn vị
            List<Double> transposeData = calData.getListS();
            AxisModel transposeAxis = CamBiz.calAxisFromList(transposeData);
            outCamModel.setTransposeAxis(transposeAxis);
            
            // Thiết lập dữ liệu Vận tốc
            List<Double> speedData = calData.getListV();
            AxisModel speedAxis = CamBiz.calAxisFromList(speedData);
            outCamModel.setSpeedAxis(speedAxis);
            
            // Thiết lập dữ liệu Gia tốc
            List<Double> accelerationData = calData.getListA();
            AxisModel accelerationAxis = CamBiz.calAxisFromList(accelerationData);
            outCamModel.setAccelerationAxis(accelerationAxis);
            
            // Thiết lập dữ liệu Cam
            List<Double> listX = calData.getListX();
            List<Double> listY = calData.getListY();
            // Save listX, listY into the session
            HttpSession session = request.getSession();
            session.setAttribute("listX", listX);
            session.setAttribute("listY", listY);
            
            AxisModel camAxis = CamBiz.calAxisFromXY(listX, listY);
                        
            outCamModel.setCamAxis(camAxis);

            //LOG.debug("camAxis=" + new Gson().toJson(camAxis));
            //LOG.debug("listX=" + new Gson().toJson(listX));
            //LOG.debug("listY=" + new Gson().toJson(listY));
            
            result.setData(outCamModel);
            
            result.setStatus("OK");
        }
        
        String strResult = new Gson().toJson(result);
        // //LOG.debug("strResult=" + strResult);
        
        return strResult;
    }
   
   @RequestMapping(value = "/download-cal-data", method = RequestMethod.GET)
   @ResponseBody public String  processExportCalData2Excel(HttpServletRequest request, HttpServletResponse response) {
       
       ResultInfo result = new ResultInfo();
       final String mimeType = "application/csv"; // "application/octet-stream";
       final String headerKey = "Content-Disposition";
       final String fileName = "CAM_Dữ-liệu-tính-toán.csv";
       
       LOG.info("processExportCalData2Excel...");
       // Get data from session
       HttpSession session = request.getSession();
       List<Double> listX = (List<Double>) session.getAttribute("listX");
       List<Double> listY = (List<Double>) session.getAttribute("listY");

        if ((listX == null) || (listY == null)) {
            result.setStatus("NG");
        } else {

            // set content attributes for the response
            response.setContentType(mimeType);

            // set headers for the response
            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setHeader(headerKey, headerValue);
            
            // Write listX, listY into buffer
            int len = Math.min(listX.size(), listY.size());
            LOG.info("Number of X,Y:" + len);
            String line;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                sb.append(listX.get(i) + "\t" + listY.get(i) + "\n");
            }
            
            response.setContentLength(sb.length());
            
            // get output stream of the response
            OutputStream outStream = null;
            try {
                outStream = response.getOutputStream();
                outStream.write(sb.toString().getBytes());

                outStream.flush();
            } catch (IOException ex) {
                LOG.error("Could not read the attachment content.", ex);
            } 
        }
       
       String strResult = new Gson().toJson(result);
       return strResult;
   }
}
