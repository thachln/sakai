/**
 * 
 */
package mksgroup.konva2d.chart.model.out;

import mksgroup.konva2d.chart.model.CamModel;

/**
 * @author lengocthach
 */
public class OutCamModel extends CamModel {
    /** . */
    private AxisModel transposeAxis;

    private AxisModel speedAxis;

    private AxisModel accelerationAxis;

    private AxisModel camAxis;

    /** Dữ liệu tính toán. */
    private CalDataModel calData;

    public OutCamModel(CamModel model) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Get value of transposeAxis.
     * @return the transposeAxis
     */
    public AxisModel getTransposeAxis() {
        return transposeAxis;
    }

    /**
     * Set the value for transposeAxis.
     * @param transposeAxis the transposeAxis to set
     */
    public void setTransposeAxis(AxisModel transposeAxis) {
        this.transposeAxis = transposeAxis;
    }

    /**
     * Get value of speedAxis.
     * @return the speedAxis
     */
    public AxisModel getSpeedAxis() {
        return speedAxis;
    }

    /**
     * Set the value for speedAxis.
     * @param speedAxis the speedAxis to set
     */
    public void setSpeedAxis(AxisModel speedAxis) {
        this.speedAxis = speedAxis;
    }

    /**
     * Get value of accelerationAxis.
     * @return the accelerationAxis
     */
    public AxisModel getAccelerationAxis() {
        return accelerationAxis;
    }

    /**
     * Set the value for accelerationAxis.
     * @param accelerationAxis the accelerationAxis to set
     */
    public void setAccelerationAxis(AxisModel accelerationAxis) {
        this.accelerationAxis = accelerationAxis;
    }

    /**
     * Get value of camAxis.
     * @return the camAxis
     */
    public AxisModel getCamAxis() {
        return camAxis;
    }

    /**
     * Set the value for camAxis.
     * @param camAxis the camAxis to set
     */
    public void setCamAxis(AxisModel camAxis) {
        this.camAxis = camAxis;
    }

    /**
     * Get value of calData.
     * @return the calData
     */
    public CalDataModel getCalData() {
        return calData;
    }

    /**
     * Set the value for calData.
     * @param calData the calData to set
     */
    public void setCalData(CalDataModel calData) {
        this.calData = calData;
    }

}
