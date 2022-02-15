package fr.jco.ign.ban.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import fr.jco.ign.ban.dao.AbstractAddressDAO;
import fr.jco.ign.ban.dao.ElasticAddressDAO;
import fr.jco.ign.ban.repository.ElasticAddressRepository;

@Configuration
@Profile("elastic")
public class ElasticConfig {

    @Value("${elastic.host}")
    private String elasticHost;

    @Bean
    AbstractAddressDAO abstractAddressDAO(ElasticAddressRepository repository) {
        return new ElasticAddressDAO(repository);
    }

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(elasticHost).build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
