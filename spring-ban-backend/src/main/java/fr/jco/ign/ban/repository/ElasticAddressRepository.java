package fr.jco.ign.ban.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.jco.ign.ban.model.ElasticAddress;

public interface ElasticAddressRepository extends ElasticsearchRepository<ElasticAddress, Long> {

    Page<ElasticAddress> findBySearchNameContains(String terms, Pageable pageable);
}
