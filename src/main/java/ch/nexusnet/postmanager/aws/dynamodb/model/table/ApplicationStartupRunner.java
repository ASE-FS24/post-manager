package ch.nexusnet.postmanager.aws.dynamodb.model.table;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
//@Profile({"dev", "test"}) TODO Reenable this line when the init-dynamodb-localstack.sh works
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
