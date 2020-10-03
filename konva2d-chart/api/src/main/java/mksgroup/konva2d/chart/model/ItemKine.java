/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mksgroup.konva2d.chart.model;

/**
 * Bộ dữ liệu: Góc kết thúc pha, Độ cao tương ứng.
 *
 * @author danie
 */
public class ItemKine {
    /**
     * Góc kết thúc pha: Tính bằng độ (deg)
     */
    private int phaseEndAngle;

    /**
     * Độ cao tương ứng: Tính bằng millimeter (mm)
     */
    private int height;

    /**
     *
     * @param phaseEndAngle
     * @param height
     */
    public ItemKine(int phaseEndAngle, int height) {
//        this.phaseNo = phaseNo;
        this.phaseEndAngle = phaseEndAngle;
        this.height = height;
    }

    /**
     *
     * @return the phaseEndAngle
     */
    public int getPhaseEndAngle() {
        return phaseEndAngle;
    }

    /**
     *
     * @param phaseEndAngle the phaseEndAngle to set
     */
    public void setPhaseEndAngle(int phaseEndAngle) {
        this.phaseEndAngle = phaseEndAngle;
    }

    /**
     * 
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * 
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
