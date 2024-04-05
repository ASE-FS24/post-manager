package ch.nexusnet.postmanager.aws.dynamodb.model.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class ApplicationStartupRunner implements CommandLineRunner {

    private final DynamoDBTableInitializer tableInitializer;

    public ApplicationStartupRunner(DynamoDBTableInitializer tableInitializer) {
        this.tableInitializer = tableInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        // The init method of DynamoDBTableInitializer will be called automatically by Spring.
    }
}
