package fr.jco.ign.ban.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.jco.ign.ban.dto.AddressDTO;

/**
 * Facade vers les méthodes du DAO pour permettre le switch entre Postgres / Elasticsearch facilement
 */
public interface AbstractAddressDAO {

    Page<AddressDTO> findAll(Pageable pageable, String terms);

}
