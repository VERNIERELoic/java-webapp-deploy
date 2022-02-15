package fr.jco.ign.ban.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fr.jco.ign.ban.model.Address;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {

    Page<Address> findBySearchNameContains(String terms, Pageable pageable);
}
