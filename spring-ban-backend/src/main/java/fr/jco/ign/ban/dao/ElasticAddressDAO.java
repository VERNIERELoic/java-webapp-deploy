package fr.jco.ign.ban.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.jco.ign.ban.dto.AddressDTO;
import fr.jco.ign.ban.model.ElasticAddress;
import fr.jco.ign.ban.repository.ElasticAddressRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ElasticAddressDAO implements AbstractAddressDAO {

    private final ElasticAddressRepository repository;

    @Override
    public Page<AddressDTO> findAll(Pageable pageable, String terms) {
        return this.repository.findBySearchNameContains(terms, pageable).map(ElasticAddress::toDto);
    }
}
