package fr.jco.ign.ban.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import fr.jco.ign.ban.dto.AddressDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"id"})
@Table(indexes = {
    @Index(columnList = Address.COLUMN_SEARCH_NAME, name = "search_name_idx")
})
public class Address {

    public static final String COLUMN_SEARCH_NAME = "search_name";

    @Id
    private String id;

    private String idFantoir;

    private String numero;

    private String nomVoie;

    private String zipCode;

    private String inseeCode;

    private String cityName;

    private Double longitude;

    private Double latitude;

    private String labelForwarding;

    @Column(name = COLUMN_SEARCH_NAME)
    private String searchName;

    public AddressDTO toDto() {
        return AddressDTO.builder()
            .id(id)
            .idFantoir(idFantoir)
            .numero(numero)
            .nomVoie(nomVoie)
            .zipCode(zipCode)
            .inseeCode(inseeCode)
            .cityName(cityName)
            .longitude(longitude)
            .latitude(latitude)
            .labelForwarding(labelForwarding).build();
    }
}
