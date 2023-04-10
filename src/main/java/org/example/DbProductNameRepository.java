package org.example;

import java.util.HashSet;

public interface DbProductNameRepository {

    HashSet<String> selectNames();
}
