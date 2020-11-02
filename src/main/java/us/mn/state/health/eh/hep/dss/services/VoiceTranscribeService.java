package us.mn.state.health.eh.hep.dss.services;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClient;
import com.amazonaws.services.transcribe.model.*;
import com.amazonaws.services.transcribe.model.transform.GetTranscriptionJobResultJsonUnmarshaller;
import us.mn.state.health.eh.hep.dss.domain.TranscribeJob;
import us.mn.state.health.eh.hep.dss.domain.VoiceTranscribeRequest;
import us.mn.state.health.eh.hep.dss.domain.*;
import us.mn.state.health.eh.hep.dss.parsers.transcribe.AWSTranscript;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class VoiceTranscribeService {

    private AmazonTranscribe getClient() {
        //https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/transcribe/AmazonTranscribeClient.html
       return  AmazonTranscribeClient.builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public void submitJob(VoiceTranscribeRequest request) {
        System.out.println(request);

        StartTranscriptionJobRequest job = new StartTranscriptionJobRequest();
        job.setLanguageCode(LanguageCode.EnUS.toString());

        Media media = new Media();
        media.setMediaFileUri("s3://mdh-cd-test-001/Audio_Files.MP3");
        job.setMedia(media);

        job.setOutputBucketName("mdh-cd-test-001");
        //job.setOutputEncryptionKMSKeyId();
        job.setTranscriptionJobName(buildJobName("eversd1", "Audio_Files.MP3"));

        Settings settings = new Settings();
        settings.setShowSpeakerLabels(true);
        settings.setMaxSpeakerLabels(5);

        job.setSettings(settings);

        getClient().startTranscriptionJob(job);
        //tagging of ac2code
    }

    public Object getTranscribeJobs(String userId) {
        return null;
    }

    public AWSTranscript getTranscribeResultsAsJson(TranscribeJob job) {

        GetTranscriptionJobRequest jobRequest = new GetTranscriptionJobRequest();
        jobRequest.setTranscriptionJobName(job.getJobName());

        GetTranscriptionJobResult jobResponse = getClient().getTranscriptionJob(jobRequest);

        String URI = jobResponse.getTranscriptionJob().getTranscript().getTranscriptFileUri();
        AmazonS3URI s3ObjectURI = new AmazonS3URI(URI);
        S3Object transcript = RequestService.getClient().getObject(s3ObjectURI.getBucket(), s3ObjectURI.getKey());

        AWSTranscript t = AWSTranscript.createFromFile(transcript);
        System.out.println(t.getResults());

        return t;
    }

    private String buildJobName(String userName, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(userName).append("_");
        String datePattern = "yyyy-MM-dd-HH-mm";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        sb.append(dateFormatter.format(LocalDateTime.now())).append("_");
        sb.append(fileName);
        return sb.toString();
    }



    public void parse(String value) {
        GetTranscriptionJobResultJsonUnmarshaller x = new
                GetTranscriptionJobResultJsonUnmarshaller();

        // https://stackoverflow.com/questions/50301033/access-results-of-aws-transcribe-job-with-java-sdk

    }
}
