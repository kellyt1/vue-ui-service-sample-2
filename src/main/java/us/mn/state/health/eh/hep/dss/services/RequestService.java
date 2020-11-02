package us.mn.state.health.eh.hep.dss.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.thymeleaf.util.StringUtils;
import us.mn.state.health.eh.hep.dss.domain.FileListing;
import us.mn.state.health.eh.hep.dss.domain.MetaTag;
import us.mn.state.health.eh.hep.dss.domain.SigningRequest;
import us.mn.state.health.eh.hep.dss.domain.SigningResponse;
import us.mn.state.health.eh.hep.dss.domain.*;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class RequestService {

    public static AmazonS3 getClient() {
       return  AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public FileUploadResponse generateSignedUrl(SigningRequest request){
        System.out.println("hello file upload");
        System.out.println("bucketName: "+request.getBucketName());
        System.out.println("file path: "+ request.getFilePath());
        System.out.println("file type: "+ request.getContentType());
        System.out.println("file content: "+request.getFileContent());
        String url = "";
        String message = "invalid";
        try {
            if (validateFile(request.getFilePath())) {

                java.util.Date expiration = new java.util.Date();
                long expTimeMillis = expiration.getTime();
                expTimeMillis += 1000 * 60 * 60;
                expiration.setTime(expTimeMillis);

                GeneratePresignedUrlRequest generatePresignedUrlRequest =
                        new GeneratePresignedUrlRequest(request.getBucketName(), request.getFilePath(), HttpMethod.PUT);
                generatePresignedUrlRequest.setContentType(request.getContentType());
                generatePresignedUrlRequest.setExpiration(expiration);
                generatePresignedUrlRequest.setZeroByteContent(true);

                url = getClient().generatePresignedUrl(generatePresignedUrlRequest).toString();
                message = "valid";
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
        FileUploadResponse response = new FileUploadResponse();
        response.setUrl(url);
        response.setMessage(message);
        return response;
    }

    public SigningResponse generateSignedUrl2(SigningRequest request) {
        System.out.println("hello signed url2");
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(request.getBucketName(), request.getFilePath(), HttpMethod.PUT);
        generatePresignedUrlRequest.setContentType(request.getContentType());
        generatePresignedUrlRequest.setExpiration(expiration);
        generatePresignedUrlRequest.setZeroByteContent(true);

        URL url = getClient().generatePresignedUrl(generatePresignedUrlRequest);

        SigningResponse response = new SigningResponse();
        response.setUrl(url.toString());
        return response;
    }

    public SigningResponse generateSignedUrlOld(SigningRequest request) {
        System.out.println("hello signed url");
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(request.getBucketName(), request.getFilePath(), HttpMethod.PUT);
        generatePresignedUrlRequest.setContentType(request.getContentType());
        generatePresignedUrlRequest.setExpiration(expiration);
        generatePresignedUrlRequest.setZeroByteContent(true);

        URL url = getClient().generatePresignedUrl(generatePresignedUrlRequest);

        SigningResponse response = new SigningResponse();
        response.setUrl(url.toString());
        return response;
    }

    public SigningResponse generateSignedDownloadUrl(SigningRequest request) {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(request.getBucketName(), request.getFilePath(), HttpMethod.GET);
        //generatePresignedUrlRequest.setContentType(request.getContentType());
        generatePresignedUrlRequest.setExpiration(expiration);
        generatePresignedUrlRequest.setZeroByteContent(true);

        URL url = getClient().generatePresignedUrl(generatePresignedUrlRequest);

        SigningResponse response = new SigningResponse();
        response.setUrl(url.toString());
        return response;
    }

    public List<FileListing> getBucketList(String bucketName) {
        List<FileListing> files = new ArrayList<>();

        ListObjectsRequest lor = new ListObjectsRequest()
                .withBucketName(bucketName);

        ObjectListing objectListing = getClient().listObjects(lor);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            FileListing file = new FileListing();
            file.setName(os.getKey());
            file.setBucketName(bucketName);
            SigningRequest signingRequest = new SigningRequest();
            signingRequest.setBucketName(bucketName);
            signingRequest.setFilePath(os.getKey());
            SigningResponse signingResponse = generateSignedDownloadUrl(signingRequest);
            file.setUrl(signingResponse.getUrl());

            Instant instant = Instant.ofEpochMilli(os.getLastModified().getTime());
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("America/Chicago"));

            file.setLastModified(ldt);
            file.setSizeInBytes(os.getSize());

            GetObjectTaggingRequest otr = new GetObjectTaggingRequest(bucketName, os.getKey());
            GetObjectTaggingResult objectTagging = getClient().getObjectTagging(otr);
            file.setTags(new ArrayList<>());
            for (Tag tag : objectTagging.getTagSet()) {
                MetaTag metaTag = new MetaTag();
                metaTag.setKey(tag.getKey());
                metaTag.setValue(tag.getValue());
                file.getTags().add(metaTag);
            }
            files.add(file);
        }
        return files;
    }

    public FileListing updateTags(FileListing file) {
        List<Tag> newTags = new ArrayList<>();
        for (MetaTag metaTag : file.getTags()) {
            newTags.add(new Tag(metaTag.getKey(), metaTag.getValue()));
        }
        getClient().setObjectTagging(
                new SetObjectTaggingRequest(file.getBucketName(), file.getName(),
                        new ObjectTagging(newTags)));
        return file;
    }

    private boolean validateFile(String filePath) throws IOException {
        System.out.println("validate upload file");
        boolean validator = false;

        File myFile = new File(filePath);
        new File
        FileInputStream fis = new FileInputStream(myFile);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        //XSSFWorkbook workBook = new XSSFWorkbook();
        //Reading sheet at number 0 in spreadsheet(image attached for reference
        Sheet sheet = myWorkBook.getSheet("load_script");

        //creating a Sheet object to retrieve object
        Iterator<Row> itr = sheet.iterator();//iterating over excel file
        //Cell cell = cellIterator.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            Iterator<Cell> cellIterator = row.cellIterator();//iterating over each column
            //Reading cell in my case column name is ppm
            Cell ppmEx = row.getCell(0);
            String ID = "";
            Cell cell = cellIterator.next();
            if (cell.getCellType() == CellType.NUMERIC) {
                ID = StringUtils.toString(cell.getNumericCellValue());
                System.out.println("numeric value: " + ID);

            }
            ;
            if (cell.getCellType() == CellType.STRING) {
                ID = cell.getRichStringCellValue().toString();
                System.out.println("String value: " + ID);
            }

            System.out.println("-");

            System.out.println("validation is " + validator);

        }
        return validator;
    }

}
