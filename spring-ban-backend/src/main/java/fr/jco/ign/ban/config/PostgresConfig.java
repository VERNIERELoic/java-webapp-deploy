package fr.jco.ign.ban.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import fr.jco.ign.ban.dao.AbstractAddressDAO;
import fr.jco.ign.ban.dao.PostgresAddressDAO;
import fr.jco.ign.ban.repository.AddressRepository;

@Configuration
@Profile("!elastic")
public class PostgresConfig {

    @Bean
    AbstractAddressDAO abstractAddressDAO(AddressRepository repository) {
        return new PostgresAddressDAO(repository);

    }
}
