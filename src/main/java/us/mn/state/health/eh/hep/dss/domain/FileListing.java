package us.mn.state.health.eh.hep.dss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class FileListing {

    private String name;
    private String bucketName;
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;

    private Long sizeInBytes;

    @JsonProperty("formattedFileSize")
    public String formattedFileSize() {
        String hrSize = null;

        double b = this.sizeInBytes;
        double k = this.sizeInBytes/1024.0;
        double m = ((this.sizeInBytes/1024.0)/1024.0);
        double g = (((this.sizeInBytes/1024.0)/1024.0)/1024.0);
        double t = ((((this.sizeInBytes/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t > 1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g > 1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m > 1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k > 1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<MetaTag> tags;

    public FileListing() {
        this.tags = new ArrayList<>();
    }

}
