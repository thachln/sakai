/**
 *
 */
package mksgroup.konva2d.chart.biz;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mksgroup.konva2d.chart.model.ItemKine;
import mksgroup.konva2d.chart.model.out.AxisModel;
import mksgroup.konva2d.chart.model.out.CalDataModel;
/**
 * @author lengocthach
 *
 */
public class CamBiz {
    final static protected Log LOG = LogFactory.getLog(CamBiz.class);
//    private static final Logger LOG = Logger.getLogger(CamBiz.class);

    /**
     * Tính toán thông số hệ trục đối với đồ thì "List".
     * Đồ thì "List" là đồ thị của hàm số y=f(x).
     * Trong đó: x chỉ số của phần tử List; y là giá trị của phần tử thứ x.
     * 
     * @param listData
     * @return
     */
    public static AxisModel calAxisFromList(List<Double> listData) {
        AxisModel axis = new AxisModel();

        // Tính toán trục X
        int len = listData.size();

        LOG.debug("Number of items in transpose data:" + len);
        Double unitsPerTickX = new Double(len / 6);
        
        LOG.debug("unitsPerTickX=" + unitsPerTickX);
        axis.setUnitsPerTickX(unitsPerTickX);
        axis.setMinX(0 - unitsPerTickX * 1.5);
        axis.setMaxX(len + unitsPerTickX);

        // Tính toán trục Y
        double minData = Collections.min(listData);
        double maxData = Collections.max(listData);

        LOG.debug("minData=" + minData + ";maxData=" + maxData);
        
        // Cần tính toán lại unitsPerTickY.
        double unitsPerTickY = (maxData - minData) / 6;
        LOG.debug("unitsPerTickY=" + unitsPerTickY);
        
        axis.setUnitsPerTickY(unitsPerTickY);
        axis.setMinY(minData - unitsPerTickY * 1.5);
        axis.setMaxY(maxData + unitsPerTickY * 1.5);

        return axis;
    }

    /**
     * Tính toán thông số hệ trục đối với đồ thị gồm 2 bộ dữ liệu X và Y.
     * @param listX
     * @param listY
     * @return
     */
    public static AxisModel calAxisFromXY(List<Double> listX, List<Double> listY) {
        AxisModel axis;

        if ((listX == null) || (listY == null)) {
            // axis = null;
            return null;
        } else {
            axis = new AxisModel();
        }

        Double minX = Collections.min(listX);
        Double maxX = Collections.max(listX);
        Double minY = Collections.min(listY);
        Double maxY = Collections.max(listY);

        // Cần tính toán lại unitsPerTickY.
        Double unitsPerTickX = (maxX - minX) / 5;
        LOG.debug("unitsPerTicX=" + unitsPerTickX);
        
        axis.setUnitsPerTickX(unitsPerTickX);
        axis.setMinX(minX - unitsPerTickX / 2);
        axis.setMaxX(maxX + unitsPerTickX / 2);
        
        // Cần tính toán lại unitsPerTickY.
        Double unitsPerTickY = (maxY - minY) / 5;
        LOG.debug("unitsPerTickY=" + unitsPerTickY);
        
        axis.setUnitsPerTickY(unitsPerTickY);
        axis.setMinY(minY - unitsPerTickY);
        axis.setMaxY(maxY + unitsPerTickY / 2);

        return axis;
    }
        
    /**
     * Tính toán bảng "Dữ liệu tính toán" để chuẩn bị vẽ hình thiết bị chuẩn.
     * @param e khoảng lệch tâm
     * @param alpha góc áp lực cho phép
     * @param motionRules bảng quy luật chuyển động. Mỗi phần tử tương ứng 1
     * cột trong file Excel (Co-cau-chuan_Cam.xlsm)
     * @return Bảng dữ liệu tính toán
     * - dữ liệu Chuyển vị dựa vào bảng Quy luật chuyển động.
     */
    public static CalDataModel calData(double e, double alpha, List<ItemKine> motionRules) {
        CalDataModel calData = new CalDataModel();
        
        int phi0;    // Góc pha (của phần tử i khi quét bảng quy luật chuyển động)
        int phi1;    // Góc pha (của phần tử i+1 khi quét bảng quy luật chuyển động)
        double h0;      // Độ cao (của phần tử i khi quét bảng quy luật chuyển động)
        double h1;      // Độ cao (của phần tử i+1 khi quét bảng quy luật chuyển động)

        int endPhase = motionRules.size();  // Số pha lớn nhất
        double k;        // Nếu khai báo k là int thì chú ý khi tính toán phép chia int / double.
        int ddelta;

        List<Double> listS = new ArrayList<Double>(); // Bảng dữ liệu kết quả chuyển vị
        List<Double> listV = new ArrayList<Double>(); // Bảng dữ liệu kết quả vận tốc
        List<Double> listA = new ArrayList<Double>(); // Bảng dữ liệu kết quả gia tốc
        List<Double> listAlphaGraphData = new ArrayList<Double>();
        
        List<Double> listX = new ArrayList<Double>(); // Bảng dữ liệu kết quả X
        List<Double> listY = new ArrayList<Double>(); // Bảng dữ liệu kết quả Y
        List<Double> listAlphaCalData = new ArrayList<Double>();
        
        double alphaValue;
        double s;                            // Giá trị chuyển vị
        double v;                            // Giá trị vận tốc
        double a;                            // Giá trị gia tốc
        
        double R = e + 1;                            // 
        double p = 0.0;

        double pmax = 0.0;
        double radPhi;
        double d;
        double psi;
        double xPhi;
        double yPhi;
        double alphaMax;
        // Tính h max
        
        // Vòng lặp thứ nhất: Tính bán kính cơ sở
        ItemKine currMotionArg; // Thông số chuyển động (góc, độ cao) của pha i
        ItemKine nextMotionArg; // Thông số chuyển động (góc, độ cao) của pha i + 1
        for (int i = 0; i < endPhase - 1; i++) {
            currMotionArg = motionRules.get(i);
            nextMotionArg = motionRules.get(i + 1);
            
            phi0 = currMotionArg.getPhaseEndAngle();

            phi1 = nextMotionArg.getPhaseEndAngle();

            h0 = currMotionArg.getHeight();

            h1 = nextMotionArg.getHeight();
            
            alphaMax = Double.MIN_VALUE;
            
            for (double j = phi0; j < phi1; j++) {
                pmax = 0.0;  // [TODO] Cần xác nhận lại pmax được tính như thế nào?
                radPhi = j * PI / 180.0;
                
                if (h0 < h1) {
                    /// Tinh s, v, a để tính alpha_max
                    s = h0 + (h1 - h0) * ((j / phi1) - ((sin(2 * PI * j / phi1)) / (2 * PI)));
                    v = (h1 - h0) * ((1.0 / (phi1 * PI / 180)) - ((cos(2 * PI * j / phi1)) / (phi1 * PI / 180)));
                    a = (h1 - h0) * ((2.0 * PI * sin(2 * PI * j / phi1)) / pow(phi1 * PI / 180, 2));
                } else if (h0 == h1) {
                    s = h0;
                    v = 0.0;
                    a = 0.0;
                } else {
                    ///
                    s = h0 - (h0 - h1) * ((j / phi1) - ((sin(2 * PI * j / phi1)) / (2 * PI)));
                    v = -(h0 - h1) * ((1.0 / (phi1 * PI / 180)) - ((cos(2 * PI * j / phi1)) / (phi1 * PI / 180)));
                    a = -(h0 - h1) * ((2.0 * PI * sin(2 * PI * j / phi1)) / pow(phi1 * PI / 180, 2));
                }
                // Tính p, alpha dựa vào s1, v1, a1
//                p = pow(pow(R + s, 2) + (v * v), 1.5) /
//                    (pow(R + s, 2) + 2 * (v * v) - a * (R + s));
                p = pow((R + s) * (R + s) + (v * v), 1.5) /
                        ((R + s) * (R + s) + 2 * (v * v) - a * (R + s));
                
                // alpha của Dữ liệu tính toán và Dữ liệu vẽ đồ thị (giống nhau)
                alphaValue = atan((v - e) / (s + sqrt(R * R - e * e))) * 180.0 / PI;
                
             // Cập nhật giá trị lớn nhất của alpha
                alphaMax = Math.max(alphaMax, alphaValue);
                 
                // Cập nhật dữ liệu vẽ đồ thị (Chuyển vị, Vận tốc, Gia tốc)
                listAlphaGraphData.add(alphaValue);
            }
            
            // Ra khỏi vòng lặp phi
            // Tính Kết quả Bán kính cơ sở
            if ((p > pmax) && (alphaMax < alpha - 5)) {
                calData.setRp(R);
            } else {
                R++;
                calData.setRp(R);
                i--;  // [TODO] Kiểm tra lại vì sao giảm i
            }
        }
        LOG.debug("listAlphaGraphData=" + listAlphaGraphData);
        // Kết thúc vòng lặp thứ nhất
        
        
        // Vòng lặp thứ hai
        // Quét từng pha 1 ~ n 

        for (int i = 0; i < endPhase - 1; i++) {
            currMotionArg = motionRules.get(i);
            nextMotionArg = motionRules.get(i + 1);
            
            phi0 = currMotionArg.getPhaseEndAngle();

            phi1 = nextMotionArg.getPhaseEndAngle();

            h0 = currMotionArg.getHeight();

            h1 = nextMotionArg.getHeight();
            
            for (double j = phi0; j < phi1; j++) {    
                radPhi = j * PI / 180.0;
                
                if (h0 < h1) {
                    k = j - phi0;
                    ddelta = phi1 - phi0;
                    
                    s = h0 + (h1 - h0) * ((k / ddelta) - ((sin(2 * PI * k / ddelta)) / (2 * PI)));
                    v = (h1 - h0) * ((1.0 / (ddelta * PI / 180)) - ((cos(2 * PI * k / ddelta)) / (ddelta * PI / 180)));
                    a = (h1 - h0) * ((2.0 * PI * sin(2 * PI * k / ddelta)) / pow(ddelta * PI / 180, 2));

                } else if (h0 == h1) {
                    s = h0;
                    v = 0.0;
                    a = 0.0;
                } else {
                    k = j - phi0;
                    ddelta = phi1 - phi0;
                    
                    s = h0 - (h0 - h1) * ((k / ddelta) - ((sin(2 * PI * k / ddelta)) / (2 * PI)));
                    v = -(h0 - h1) * ((1 / (ddelta * PI / 180)) - ((cos(2 * PI * k / ddelta)) / (ddelta * PI / 180)));
                    a = -(h0 - h1) * ((2 * PI * sin(2 * PI * k / ddelta)) / pow(ddelta * PI / 180, 2));
                    
                }
                // alpha của Dữ liệu tính toán và Dữ liệu vẽ đồ thị (giống nhau)
                alphaValue = atan((v - e) / (s + sqrt(R * R - e * e))) * 180 / PI;
                listAlphaCalData.add(alphaValue);

                 
                // Cập nhật dữ liệu vẽ đồ thị (Chuyển vị, Vận tốc, Gia tốc)
                listS.add(s);
                listV.add(v);
                listA.add(a);
                
                // Tính toán Dữ liệu tính toán cho vẽ đề thị Cam
                d = Math.sqrt(R * R - e * e);
                psi = 2.5 * Math.PI - radPhi - Math.atan(e / (s + d));
                xPhi = cos(psi) * sqrt(pow((s + d), 2) + pow(e, 2));
                yPhi = sin(psi) * sqrt(pow((s + d), 2) + pow(e, 2));
                listX.add(xPhi);
                listY.add(yPhi);
                
                // Lưu kết quả
                calData.setD(d);
            }

        }
        LOG.debug("listAlphaCalData=" + listAlphaCalData);
        
        // Lưu kết quả
        calData.setAlphaMax(Collections.max(listAlphaCalData));
        calData.setAlphaMin(Collections.min(listAlphaCalData));
        
        calData.setListS(listS);
        calData.setListV(listV);
        calData.setListA(listA);
        
        calData.setListX(listX);
        calData.setListY(listY);
        
        return calData;
    }
}
