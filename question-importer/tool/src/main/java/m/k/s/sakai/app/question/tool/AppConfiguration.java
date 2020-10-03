/**
 * 
 */
package m.k.s.sakai.app.question.tool;

import java.io.Serializable;
import java.util.List;

/**
 * @author hiepnhse61627
 *
 */
public class AppConfiguration implements Serializable {
    private List<String> listPatterns;

    /**
     * @return the listPatterns
     */
    public List<String> getListPatterns() {
        return listPatterns;
    }

    /**
     * @param listPatterns the listPatterns to set
     */
    public void setListPatterns(List<String> listPatterns) {
        this.listPatterns = listPatterns;
    }
}
