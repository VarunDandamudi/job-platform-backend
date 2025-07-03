// src/main/java/com/example/job_platform/config/MongoGridFsConfig.java
package com.example.job_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * MongoDB GridFS configuration for storing large files like resumes.
 * This configuration class provides the GridFsTemplate bean.
 */
@Configuration
public class MongoGridFsConfig {

    /**
     * Configures and provides a GridFsTemplate bean for interacting with MongoDB GridFS.
     * This is used for storing and retrieving large files like resumes.
     * It relies on the auto-configured MongoDatabaseFactory and MappingMongoConverter.
     *
     * @param mongoDatabaseFactory The auto-configured MongoDatabaseFactory.
     * @param mongoMappingConverter The auto-configured MappingMongoConverter.
     * @return A GridFsTemplate instance.
     */
    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory mongoDatabaseFactory,
                                         MappingMongoConverter mongoMappingConverter) {
        return new GridFsTemplate(mongoDatabaseFactory, mongoMappingConverter);
    }
}
