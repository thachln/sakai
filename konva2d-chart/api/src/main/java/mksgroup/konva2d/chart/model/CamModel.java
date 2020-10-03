/**
 * Licensed to MKS Group under one or more contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Open-Ones Group licenses this file to you under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package mksgroup.konva2d.chart.model;

import java.util.List;

/**
 * @author ThachLN
 *
 */
public class CamModel {

    /** Bán kính cơ sở . */
    private Integer rp;
    
    private Integer e;
    
    private Integer alpha;
    
    private String motionFunc;
    
    private Integer phaseNo;
    
    private Integer endAnglePhase;
    
    private Integer high;
    
    /** Danh sách quy luật chuyển động. */
    private List<ItemKine> listItemKine;

    /**
     * @return the rp
     */
    public Integer getRp() {
        return rp;
    }

    /**
     * @param rp the rp to set
     */
    public void setRp(Integer rp) {
        this.rp = rp;
    }

    /**
     * @return the e
     */
    public Integer getE() {
        return e;
    }

    /**
     * @param e the e to set
     */
    public void setE(Integer e) {
        this.e = e;
    }

    /**
     * @return the alpha
     */
    public Integer getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the motionFunc
     */
    public String getMotionFunc() {
        return motionFunc;
    }

    /**
     * @param motionFunc the motionFunc to set
     */
    public void setMotionFunc(String motionFunc) {
        this.motionFunc = motionFunc;
    }

    /**
     * @return the phaseNo
     */
    public Integer getPhaseNo() {
        return phaseNo;
    }

    /**
     * @param phaseNo the phaseNo to set
     */
    public void setPhaseNo(Integer phaseNo) {
        this.phaseNo = phaseNo;
    }

    /**
     * @return the endAnglePhase
     */
    public Integer getEndAnglePhase() {
        return endAnglePhase;
    }

    /**
     * @param endAnglePhase the endAnglePhase to set
     */
    public void setEndAnglePhase(Integer endAnglePhase) {
        this.endAnglePhase = endAnglePhase;
    }

    /**
     * @return the high
     */
    public Integer getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(Integer high) {
        this.high = high;
    }

    /**
     * @return the listItemKine
     */
    public List<ItemKine> getListItemKine() {
        return listItemKine;
    }

    /**
     * @param listItemKine the listItemKine to set
     */
    public void setListItemKine(List<ItemKine> listItemKine) {
        this.listItemKine = listItemKine;
    } 
 }
