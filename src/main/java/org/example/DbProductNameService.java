package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DbProductNameService {

    public static Set<String> getAllImageNames() {
        String resource = "mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException("mybatis-config.xml 불러오는 도중 문제 발생", e);
        }

        Properties props = new Properties();
        props.putAll(System.getenv());
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);

        try (SqlSession session = sessionFactory.openSession()) {
            DbProductNameRepository repo = session.getMapper(DbProductNameRepository.class);

            return repo.selectNames();
        }
    }
}
