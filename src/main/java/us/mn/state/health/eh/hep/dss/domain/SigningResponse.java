package us.mn.state.health.eh.hep.dss.domain;

import lombok.Data;

@Data
public class SigningResponse {
    private String url;
    private String message;
}
