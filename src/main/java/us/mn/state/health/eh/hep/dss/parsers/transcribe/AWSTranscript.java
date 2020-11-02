package us.mn.state.health.eh.hep.dss.parsers.transcribe;

import java.io.*;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringUtils;
import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Data
public class AWSTranscript{
    String jobName;
    String accountId;
    AWSTranscriptResults results;
    String status;

    public AWSTranscript(JSONObject transcriptJSON) {
        jobName = (String) transcriptJSON.get("jobName");
        accountId = (String) transcriptJSON.get("accountId");
        status = (String) transcriptJSON.get("status");

        JSONObject resultsJSON = (JSONObject) transcriptJSON.get("results");
        results = new AWSTranscriptResults(resultsJSON);
    }

    static public AWSTranscript createFromFile(S3Object s3Object) {
        JSONParser parser = new JSONParser();

        JSONObject fileAsJSON = null;
        try {
            fileAsJSON = (JSONObject) parser.parse(getAsString(s3Object.getObjectContent().getDelegateStream()));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new AWSTranscript(fileAsJSON);
    }

    static private String getAsString(InputStream is) throws IOException {
        if (is == null)
            return "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StringUtils.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }


}
