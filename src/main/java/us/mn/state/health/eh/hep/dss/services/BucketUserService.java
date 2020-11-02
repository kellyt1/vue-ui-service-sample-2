package us.mn.state.health.eh.hep.dss.services;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClient;
import com.amazonaws.services.transcribe.model.*;
import com.amazonaws.services.transcribe.model.transform.GetTranscriptionJobResultJsonUnmarshaller;
import us.mn.state.health.eh.hep.dss.domain.Bucket;
import us.mn.state.health.eh.hep.dss.domain.BucketUser;
import us.mn.state.health.eh.hep.dss.domain.TranscribeJob;
import us.mn.state.health.eh.hep.dss.domain.VoiceTranscribeRequest;
import us.mn.state.health.eh.hep.dss.parsers.transcribe.AWSTranscript;
import us.mn.state.health.eh.hep.dss.utils.KeyIdUtils;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class BucketUserService {

    private static String TABLE_NAME = "CloudDrive";

    private AmazonDynamoDB getClient() {
        return  AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    private DynamoDB getDynamoDB() {
        return new DynamoDB(getClient());
    }

    private Table getTable() {
        return getDynamoDB().getTable(TABLE_NAME);
    }

    public void saveBucket(Bucket bucket) {
        Item item = new Item()
                .withPrimaryKey("PK", KeyIdUtils.getBucketPK(bucket.getId()))
                .withString("SK", KeyIdUtils.getBucketSK())
                .withString("businessName", bucket.getBusinessName())
                .withString("bucketName", bucket.getBucketName())
                .withString("contentType", bucket.getContentType())
                .withString("ac2code", bucket.getAc2Code())
                .withString("ownership", bucket.getOwnership());

        PutItemOutcome outcome = getTable().putItem(item);
        //TODO - Exception handling
    }

    public void saveBucketUser(BucketUser bucketUser) {
        Item item = new Item()
                .withPrimaryKey("PK", KeyIdUtils.getUserPK(bucketUser.getUserId()))
                .withString("SK", KeyIdUtils.getBucketPK(bucketUser.getBucketId()))
                .withBoolean("upload", bucketUser.getUpload())
                .withBoolean("list", bucketUser.getList())
                .withBoolean("delete", bucketUser.getDelete())
                .withBoolean("download", bucketUser.getDownload());

        PutItemOutcome outcome = getTable().putItem(item);
        //TODO - Exception handling
    }

    public List<Bucket> getBuckets() {
        List<Bucket> results = new ArrayList<>();
        QuerySpec querySpec = new QuerySpec();
        Index index = getTable().getIndex("SK-PK-index");

        querySpec.withKeyConditionExpression("SK = :v_sk")
                .withValueMap(new ValueMap().withString(":v_sk", KeyIdUtils.getBucketSK()));
        ItemCollection<QueryOutcome> items = index.query(querySpec);

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Bucket bucket = new Bucket();
            bucket.setId(KeyIdUtils.stripBucketPrefix(item.getString("PK")));
            bucket.setBusinessName(item.getString("businessName"));
            bucket.setBucketName(item.getString("bucketName"));
            bucket.setAc2Code(item.getString("ac2code"));
            bucket.setContentType(item.getString("contentType"));
            bucket.setOwnership(item.getString("ownership"));
            results.add(bucket);
        }
        return results;
    }

    public List<BucketUser> getBucketUsers(String bucketId) {
        List<BucketUser> results = new ArrayList<>();
        QuerySpec querySpec = new QuerySpec();

        querySpec.withKeyConditionExpression("PK = :v_pk and begins_with(SK, :v_sk)")
                .withValueMap(
                        new ValueMap()
                                .withString(":v_pk", KeyIdUtils.getBucketPK(bucketId))
                                .withString(":v_sk", "USER#"));

        ItemCollection<QueryOutcome> items = getTable().query(querySpec);

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            BucketUser bucketUser = new BucketUser();
            bucketUser.setBucketId(KeyIdUtils.stripBucketPrefix(item.getString("PK")));
            bucketUser.setUserId(KeyIdUtils.stripUserPrefix(item.getString("SK")));
            bucketUser.setList(item.getBoolean("list"));
            bucketUser.setUpload(item.getBoolean("upload"));
            bucketUser.setDownload(item.getBoolean("download"));
            bucketUser.setDelete(item.getBoolean("delete"));
            results.add(bucketUser);
        }
        return results;
    }

    public Bucket getBucketById(String bucketId) {
        //getTable().getItem("PK", )
        return null;
    }

    public String uploadFile() {
        System.out.println("hello world from ping controller");
        return "File stored in S3";
    }


}
