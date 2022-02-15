package fr.jco.ign.ban.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.jco.ign.ban.dto.AddressDTO;
import fr.jco.ign.ban.model.Address;
import fr.jco.ign.ban.repository.AddressRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostgresAddressDAO implements AbstractAddressDAO {

    private final AddressRepository repository;

    @Override
    public Page<AddressDTO> findAll(Pageable pageable, String terms) {
        return this.repository.findBySearchNameContains(terms, pageable).map(Address::toDto);
    }

}
