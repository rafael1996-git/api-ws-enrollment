package com.trader.api.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.trader.api.utils.enums.ContentTypeEnum;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class S3Utils {
    private static final String UTF8_CONTENT_TYPE_ENCODING = "UTF-8";
    private static AmazonS3 amazonS3Client = null;

    private static void initializeAmazons3Client() throws Exception {
        if (amazonS3Client == null) {
            amazonS3Client = AmazonS3ClientBuilder.standard().build();
        }
    }

    public static InputStream downloadS3FileToInputStream(String bucket,
                                                          String key) throws Exception {
        initializeAmazons3Client();
        S3Object xFile = amazonS3Client.getObject(bucket, key);
        return new BufferedInputStream(xFile.getObjectContent());
    }

    public static String getTextFromS3File(String bucket,
                                           String key) throws Exception {
        initializeAmazons3Client();
        S3Object xFile = amazonS3Client.getObject(bucket, key);
        InputStream contents = xFile.getObjectContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, UTF8_CONTENT_TYPE_ENCODING);
        return writer.toString();
    }

    public static String createS3FileFromString(String bucket,
                                                String key,
                                                String text) throws Exception {
        initializeAmazons3Client();
        PutObjectRequest putObjectRequest = generatePutObjectRequestForCreateS3FileFromString(bucket, key, text);
        amazonS3Client.putObject(putObjectRequest);
        return getUrlFromS3File(bucket, key);
    }

    public static String createS3FileFromString(String bucket,
                                                String key,
                                                String text,
                                                CannedAccessControlList accessControlList) throws Exception {
        initializeAmazons3Client();
        PutObjectRequest putObjectRequest = generatePutObjectRequestForCreateS3FileFromString(bucket, key, text);
        putObjectRequest.setCannedAcl(accessControlList);
        amazonS3Client.putObject(putObjectRequest);
        return getUrlFromS3File(bucket, key);
    }

    private static PutObjectRequest generatePutObjectRequestForCreateS3FileFromString(String bucket,
                                                                                      String key,
                                                                                      String text) throws Exception {
        String contentType = null;
        if (text.toLowerCase().endsWith(".txt")) {
            contentType = ContentTypeEnum.TEXT_PLAIN.getValue();
        } else if (text.toLowerCase().endsWith(".json")) {
            contentType = ContentTypeEnum.APPLICATION_JSON.getValue();
        } else {
            throw new Exception("El key debe tener extension .txt o .json obligatoriamente");
        }
        ObjectMetadata md = new ObjectMetadata();
        InputStream contentAsBytes = new ByteArrayInputStream(text.getBytes());
        byte[] bytes = com.amazonaws.util.IOUtils.toByteArray(contentAsBytes);
        md.setContentLength((long)bytes.length);
        md.setContentType(contentType);
        md.setContentEncoding(UTF8_CONTENT_TYPE_ENCODING);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return new PutObjectRequest(bucket, key, byteArrayInputStream, md);
    }

    public static String uploadFileToS3(String bucket,
                                        String key,
                                        InputStream inputStream,
                                        ContentTypeEnum contentTypeEnum) throws Exception {
        initializeAmazons3Client();
        PutObjectRequest putObjectRequest = generatePutObjectRequestForUploadFileToS3(bucket, key, inputStream, contentTypeEnum);
        amazonS3Client.putObject(putObjectRequest);
        return getUrlFromS3File(bucket, key);
    }

    public static String uploadFileToS3(String bucket,
                                        String key,
                                        InputStream inputStream,
                                        ContentTypeEnum contentTypeEnum,
                                        CannedAccessControlList accessControlList) throws Exception {
        initializeAmazons3Client();
        PutObjectRequest putObjectRequest = generatePutObjectRequestForUploadFileToS3(bucket, key, inputStream, contentTypeEnum);
        putObjectRequest.setCannedAcl(accessControlList);
        amazonS3Client.putObject(putObjectRequest);
        return getUrlFromS3File(bucket, key);
    }

    private static PutObjectRequest generatePutObjectRequestForUploadFileToS3(String bucket,
                                                                              String key,
                                                                              InputStream inputStream,
                                                                              ContentTypeEnum contentTypeEnum) throws Exception  {
        ObjectMetadata md = new ObjectMetadata();
        byte[] bytes = com.amazonaws.util.IOUtils.toByteArray(inputStream);
        md.setContentLength((long)bytes.length);
        md.setContentType(contentTypeEnum.getValue());
        md.setContentEncoding(UTF8_CONTENT_TYPE_ENCODING);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return new PutObjectRequest(bucket, key, byteArrayInputStream, md);
    }

    public static String getUrlFromS3File(String bucket,
                                          String key) throws Exception {
        return amazonS3Client.getUrl(bucket, key).toExternalForm();
    }
}
