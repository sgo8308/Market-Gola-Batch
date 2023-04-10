package org.example;

import java.util.List;

public interface S3ProductNameRepository {

    List<String> selectKeys();
}
