package us.mn.state.health.eh.hep.dss.domain;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class Bucket {

    private String id;
    private String businessName;
    private String bucketName;

    private String ac2Code;

    private String contentType;
        // 01 = File Share
        // 02 = Audio Transcribe
        // 03 = Image Object Detection
        // 04 = Video Transcribe

    private String ownership;

    /* TODO: Define Retention Period Information here. */

    public Bucket() {
        this.id = RandomStringUtils.random(14, "0123456789abcdef");
    }
}
