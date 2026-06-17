package com.project.code.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "reviews";
    }
    @Value("${MONGODB_PASSWORD}")
    private String MONGODB_PASSWORD;

    @Value("${MONGODB_HOST}")
    private String MONGODB_HOST;

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb://admin:" +
                    MONGODB_PASSWORD +'@' + MONGODB_HOST +
                    "/reviews"
        );
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

}
