package us.mn.state.health.eh.hep.dss;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import us.mn.state.health.eh.hep.dss.domain.*;
import us.mn.state.health.eh.hep.dss.parsers.transcribe.AWSTranscript;
import us.mn.state.health.eh.hep.dss.services.BucketUserService;
import us.mn.state.health.eh.hep.dss.services.RequestService;
import us.mn.state.health.eh.hep.dss.services.VoiceTranscribeService;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Controller("/")
public class MainController {

    @Inject
    private RequestService requestService;

    @Inject
    private VoiceTranscribeService voiceTranscribeService;

    @Inject
    private BucketUserService bucketUserService;


    @Post("/fileupLoad")
    public FileUploadResponse upload(@Valid SigningRequest payload) {
        System.out.println("get signed url"+ payload);
        return requestService.generateSignedUrl(payload);
    }



    @Post("/fileupLoadold")
    public SigningResponse post2(@Valid SigningRequest payload) {
        return requestService.generateSignedUrl2(payload);
    }


    @Post("/getSignedUrl")
    public SigningResponse post(@Valid SigningRequest payload) {
        return requestService.generateSignedUrlOld(payload);
    }

    @Post("/getBucketFileList")
    public List<FileListing> getBucketFileList(@Valid FileListRequest payload) {
        return requestService.getBucketList(payload.getBucketName());
    }

    @Post("/updateTags")
    public FileListing updateTags(@Valid FileListing payload) {
        return requestService.updateTags(payload);
    }

    @Post("/transcribe/voice")
    public HttpStatus transcribeVoice(@Valid VoiceTranscribeRequest payload) {
        voiceTranscribeService.submitJob(payload);
        return HttpStatus.OK;
    }

    @Post("/transcribe/results/json")
    public AWSTranscript getTranscribeResults(@Valid TranscribeJob payload) {
        return voiceTranscribeService.getTranscribeResultsAsJson(payload);
    }

    @Get("/transcribe/jobs")
    public HttpStatus getTranscribeJobs(HttpRequest httpRequest) {
        voiceTranscribeService.getTranscribeJobs("eversd1");
        return HttpStatus.OK;
    }

    @Post("/bucket")
    public HttpStatus saveBucket(@Valid Bucket payload) {
        bucketUserService.saveBucket(payload);
        return HttpStatus.OK;
    }

    @Get("/buckets")
    public List<Bucket> getBuckets(HttpRequest httpRequest) {
        return bucketUserService.getBuckets();
    }

    @Post("/bucketuser")
    public HttpStatus saveBucketUser(@Valid BucketUser payload) {
        bucketUserService.saveBucketUser(payload);
        return HttpStatus.OK;
    }

    @Get("/bucketusers/{id}")
    public List<BucketUser> getBucketUsers(String id, HttpRequest httpRequest) {
        return bucketUserService.getBucketUsers(id);
    }

    @Get("/ping")
    public String ping() {
        System.out.println("hello world from ping controller");
        return "ok"; }

     @Get("/uploadfile")
    public String uploadFile(@Valid SigningRequest payload){
        return bucketUserService.uploadFile();
     }
}
