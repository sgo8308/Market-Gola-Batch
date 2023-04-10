package org.example;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.Region;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class S3ProductNameService {

    private static final String BUCKET_NAME = "market-gola";

    private AmazonS3 s3;

    public S3ProductNameService() {
        s3 = AmazonS3ClientBuilder.standard()
                .withRegion(Region.AP_Seoul.toString())
                .build();
    }

    public List<String> getAllImageNames() {
        String resource = "mybatis-config-athena.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException("mybatis-config-athena.xml 불러오는 도중 문제 발생", e);
        }

        Properties props = new Properties();
        props.putAll(System.getenv());
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);

        try (SqlSession session = sessionFactory.openSession()) {
            S3ProductNameRepository repo = session.getMapper(S3ProductNameRepository.class);

            List<String> keys = repo.selectKeys();

            return convertToImageNames(keys);
        }
    }

    private List<String> convertToImageNames(List<String> keys) {
        return keys.stream()
                .map(key -> Paths.get(key).getFileName().toString())
                .collect(Collectors.toList());
    }

    public void deleteImages(List<String> imageNames) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(BUCKET_NAME)
                .withKeys(convertToKeys(imageNames));

        s3.deleteObjects(request);
    }

    private String[] convertToKeys(List<String> imageNames) {
        return imageNames.stream()
                .map(imageName -> "products/" + imageName)
                .toArray(String[]::new);
    }
}