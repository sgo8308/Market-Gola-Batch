package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LambdaApplication implements RequestHandler<Map<String, String>, Void> {

    @Override
    public Void handleRequest(Map<String, String> input, Context context) {
        S3ProductNameService s3ProductNameService = new S3ProductNameService();

        List<String> imageNamesInS3 = s3ProductNameService.getAllImageNames();
        Set<String> imageNamesInDb = DbProductNameService.getAllImageNames();

        List<String> orphanedImageNames = new ArrayList<>();

        for (String imageNameInS3 : imageNamesInS3) {
            if (!imageNamesInDb.contains(imageNameInS3)) {
                orphanedImageNames.add(imageNameInS3);
            }
        }

        s3ProductNameService.deleteImages(orphanedImageNames);

        return null;
    }
}
