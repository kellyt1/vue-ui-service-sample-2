package us.mn.state.health.eh.hep.dss.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Data
@Introspected
public class SigningRequest {
    private String bucketName;
    private String filePath;
    private String contentType;
    private String fileContent;
}
