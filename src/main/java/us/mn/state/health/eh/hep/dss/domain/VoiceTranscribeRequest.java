package us.mn.state.health.eh.hep.dss.domain;

import lombok.Data;

@Data
public class VoiceTranscribeRequest {

    private String bucketName;
    private String objectName;

}
