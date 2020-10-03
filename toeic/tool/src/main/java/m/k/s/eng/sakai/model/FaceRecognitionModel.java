package m.k.s.eng.sakai.model;

import java.util.List;

public class FaceRecognitionModel {
    private boolean startRecognition;
    private boolean endRecognition;
    private List<Integer> partRecognition;
    public boolean isStartRecognition() {
        return startRecognition;
    }
    public boolean isEndRecognition() {
        return endRecognition;
    }
    public List<Integer> getPartRecognition() {
        return partRecognition;
    }
    public void setStartRecognition(boolean startRecognition) {
        this.startRecognition = startRecognition;
    }
    public void setEndRecognition(boolean endRecognition) {
        this.endRecognition = endRecognition;
    }
    public void setPartRecognition(List<Integer> partRecognition) {
        this.partRecognition = partRecognition;
    }

}
