package m.k.s.eng.sakai.model;

import java.util.List;

import org.apache.commons.math3.util.Precision;

/**
 * Description: Customized Part Bean For TOEIC
 * @author MINH MAN
 *
 */
public class PartDataToeic {

    public PartDataToeic() {

    }

    private String text;
    private List<QuestionDataToeic> itemContents;
    private String partId;
    private double maxPoints;
    private String description;
    private String title;
    private Long poolId;
    private String poolName;

    public String getText() {
        return text;
    }

    public String getNonDefaultText() {

        if ("Default".equals(text) || "default".equals(text)) {
            return "";
        }
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }



    /**
     * Total points the part is worth.
     * 
     * @return max total points for part
     */
    public double getMaxPoints() {
        return maxPoints;
    }

    public double getRoundedMaxPoints() {
        // only show 2 decimal places

        return Precision.round(maxPoints, 2);
    }

    /**
     * Total points the part is worth.
     * 
     * @param maxPoints
     *            points the part is worth.
     */
    public void setMaxPoints(double maxPoints) {
        this.maxPoints = maxPoints;
    }

    /**
     * Contents of part.
     * 
     * @return item contents of part.
     */
    public List<QuestionDataToeic> getItemContents() {
        return itemContents;
    }

    /**
     * Contents of part.
     * 
     * @param itemContents
     *            item contents of part.
     */
    public void setItemContents(List<QuestionDataToeic> itemContents) {
        this.itemContents = itemContents;
    }

    /**
     * Get the size of the contents
     */
    public String getItemContentsSize() {
        if (itemContents == null) {
            return "0";
        }
        return Integer.toString(itemContents.size());
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDesc) {
        description = newDesc;
    }

    /**
     * poolId to be drawn
     * 
     * @return
     */
    public Long getPoolId() {
        return poolId;
    }

    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    /**
     * get pool name to be drawn
     * 
     * @return
     */
    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

}
