/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
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
package mksgroup.konva2d.chart.model.out;

import java.util.List;

/**
 * Dữ liệu được tính toán để vẽ đồ thị cơ cấu chuẩn.
 * @author lengocthach
 */
public class CalDataModel {
    /** Bán kính cơ sở. */
    double rp;
    double d;

    /** Góc áp lực max. */
    double alphaMax;

    /** Góc áp lực min. */
    double alphaMin;
    
    /** Danh sách liệu chuyển vị. */
    private List<Double> listS;
    
    /** Danh sách dữ liệu Vận tốc. */
    private List<Double> listV;
    
    /** Danh sách dữ liệu Gia tốc . */
    private List<Double> listA;

    private List<Double> listX;
    private List<Double> listY;
    /**
     * Get value of rp.
     * @return the rp
     */
    public double getRp() {
        return rp;
    }
    /**
     * Set the value for rp.
     * @param rp the rp to set
     */
    public void setRp(double rp) {
        this.rp = rp;
    }
    /**
     * Get value of d.
     * @return the d
     */
    public double getD() {
        return d;
    }
    /**
     * Set the value for d.
     * @param d the d to set
     */
    public void setD(double d) {
        this.d = d;
    }
    /**
     * Get value of alphaMax.
     * @return the alphaMax
     */
    public double getAlphaMax() {
        return alphaMax;
    }
    /**
     * Set the value for alphaMax.
     * @param alphaMax the alphaMax to set
     */
    public void setAlphaMax(double alphaMax) {
        this.alphaMax = alphaMax;
    }
    /**
     * Get value of alphaMin.
     * @return the alphaMin
     */
    public double getAlphaMin() {
        return alphaMin;
    }
    /**
     * Set the value for alphaMin.
     * @param alphaMin the alphaMin to set
     */
    public void setAlphaMin(double alphaMin) {
        this.alphaMin = alphaMin;
    }
    /**
     * Get value of listS.
     * @return the listS
     */
    public List<Double> getListS() {
        return listS;
    }
    /**
     * Set the value for listS.
     * @param listS the listS to set
     */
    public void setListS(List<Double> listS) {
        this.listS = listS;
    }
    /**
     * Get value of listV.
     * @return the listV
     */
    public List<Double> getListV() {
        return listV;
    }
    /**
     * Set the value for listV.
     * @param listV the listV to set
     */
    public void setListV(List<Double> listV) {
        this.listV = listV;
    }
    /**
     * Get value of listA.
     * @return the listA
     */
    public List<Double> getListA() {
        return listA;
    }
    /**
     * Set the value for listA.
     * @param listA the listA to set
     */
    public void setListA(List<Double> listA) {
        this.listA = listA;
    }
    /**
     * Get value of listX.
     * @return the listX
     */
    public List<Double> getListX() {
        return listX;
    }
    /**
     * Set the value for listX.
     * @param listX the listX to set
     */
    public void setListX(List<Double> listX) {
        this.listX = listX;
    }
    /**
     * Get value of listY.
     * @return the listY
     */
    public List<Double> getListY() {
        return listY;
    }
    /**
     * Set the value for listY.
     * @param listY the listY to set
     */
    public void setListY(List<Double> listY) {
        this.listY = listY;
    }

}
