package uk.nhs.digital.nhsconnect.nhais.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;

@Slf4j
public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

    public static final int MONGODB_PORT = 27017;
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:3.2.4";
    private static MongoDbContainer container;

    private MongoDbContainer() {
        super(DEFAULT_IMAGE_AND_TAG);
        addExposedPort(MONGODB_PORT);
    }

    public static MongoDbContainer getInstance() {
        if (container == null) {
            container = new MongoDbContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        var newMongoUri = "mongodb://" + getContainerIpAddress() + ":" + getMappedPort(MONGODB_PORT);
        LOGGER.info("Changing Mongo URI (NHAIS_MONGO_URI) to {}", newMongoUri);
        System.setProperty("NHAIS_MONGO_URI", newMongoUri);
        LOGGER.info("Setting NHAIS_MONGO_AUTO_INDEX_CREATION to true");
        System.setProperty("NHAIS_MONGO_AUTO_INDEX_CREATION", String.valueOf(true));
    }
}