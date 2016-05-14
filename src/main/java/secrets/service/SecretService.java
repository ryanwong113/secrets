package secrets.service;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import secrets.model.Secret;

@Service
public class SecretService {

    private static final Logger logger = Logger.getLogger(SecretService.class);

    @Inject
    private Environment environment;

    private DB mongoDb;

    @PostConstruct
    public void setup() throws UnknownHostException {
        MongoClientURI mongoClientURI = new MongoClientURI(getMongoDbURI());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        mongoDb = mongoClient.getDB(mongoClientURI.getDatabase());
    }

    private String getMongoDbURI() {
        return String.format("mongodb://%s:%s@%s:%s/%s",
                getMongoDbUsername(),
                getMongoDbPassword(),
                getMongoDbHost(),
                getMongoDbPort(),
                getMongoDbDatabase());
    }

    private String getMongoDbUsername() {
        return environment.getProperty("spring.data.mongodb.username");
    }

    private String getMongoDbPassword() {
        return environment.getProperty("spring.data.mongodb.password");
    }

    private String getMongoDbHost() {
        return environment.getProperty("spring.data.mongodb.host");
    }

    private String getMongoDbPort() {
        return environment.getProperty("spring.data.mongodb.port");
    }

    private String getMongoDbDatabase() {
        return environment.getProperty("spring.data.mongodb.database");
    }

    public void addSecret(Secret newSecret) {
        logger.info("[addSecret] Adding new secret...");

        DBCollection secrets = mongoDb.getCollection("secrets");
        secrets.insert(getBasicDBObject(newSecret));
    }

    public void addSecrets(List<Secret> newSecrets) {
        newSecrets.forEach(this::addSecret);
    }

    private BasicDBObject getBasicDBObject(Secret secret) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("title", secret.getTitle());
        basicDBObject.put("tag", secret.getTag());
        basicDBObject.put("message", secret.getMessage());
        basicDBObject.put("createdBy", secret.getCreatedBy());
        basicDBObject.put("createdAt", secret.getCreatedAt());

        logger.info(basicDBObject);

        return basicDBObject;
    }

}
