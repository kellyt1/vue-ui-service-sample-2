package us.mn.state.health.eh.hep.dss.parsers.transcribe;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class AWSTranscriptAlternatives{
    String confidence = null;
    String content = null;

    AWSTranscriptAlternatives(String confidence, String content){
        this.confidence = confidence;
        this.content = content;
    }

    public AWSTranscriptAlternatives(JSONObject alt) {
        confidence = (String) alt.getOrDefault("confidence", null);
        content = (String) alt.getOrDefault("content", null);
    }

    public AWSTranscriptAlternatives() {
    }
}
