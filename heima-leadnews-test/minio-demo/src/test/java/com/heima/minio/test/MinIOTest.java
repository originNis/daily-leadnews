package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
        fileStorageService.uploadHtmlFile("", "list-1.html", fileInputStream);
    }
//    public static void main(String[] args) {
//        try {
//            FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
//
//            MinioClient minioClient = MinioClient.builder()
//                    .credentials("rybin", "12345678")
//                    .endpoint("http://124.223.106.233:9000/")
//                    .build();
//
//            PutObjectArgs object = PutObjectArgs.builder()
//                    .object("list.html")
//                    .contentType("text/html")
//                    .bucket("leadnews")
//                    .stream(fileInputStream, fileInputStream.available(), -1)
//                    .build();
//            minioClient.putObject(object);
//
//            System.out.println("http://124.223.106.233:9000/leadnews/list.html");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
