package ch.nexusnet.postmanager.aws.dynamodb.model.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
//TODO Make it run only in dev or prod environment, else delete
public class ApplicationStartupRunner implements CommandLineRunner {

    private final DynamoDBTableInitializer tableInitializer;

    @Autowired
    public ApplicationStartupRunner(DynamoDBTableInitializer tableInitializer) {
        this.tableInitializer = tableInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        // The init method of DynamoDBTableInitializer will be called automatically by Spring.
    }
}
