package us.mn.state.health.eh.hep.dss.domain;

import lombok.Data;

@Data
public class BucketUser {

    private String userId;
    private String bucketId;

    // Allowed Permissions
    private Boolean upload;
    private Boolean list;
    private Boolean delete;
    private Boolean download;

}
