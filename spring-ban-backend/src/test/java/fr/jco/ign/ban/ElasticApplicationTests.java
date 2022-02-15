package fr.jco.ign.ban;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("elastic")
@ContextConfiguration(initializers = {ElasticApplicationTests.Initializer.class})
@Testcontainers
class ElasticApplicationTests extends AbstractApplicationTests {

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.12.1");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of("elastic.host=" + container.getHttpHostAddress())
                .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
