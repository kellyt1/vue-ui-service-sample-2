package us.mn.state.health.eh.hep.dss.parsers.transcribe;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class AWSTranscripts{
    String transcript;

    public AWSTranscripts(JSONObject transcriptJSON) {
        transcript = (String)transcriptJSON.get("transcript");
    }

    public AWSTranscripts() {
    }
}
