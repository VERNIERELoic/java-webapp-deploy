package fr.jco.ign.ban.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import fr.jco.ign.ban.dto.AddressDTO;
import fr.jco.ign.ban.dto.AddressToSaveDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "address")
@Getter
@Setter
@NoArgsConstructor
public class ElasticAddress {

    @Id
    private String id;

    private String idFantoir;

    @Field(type = FieldType.Text)
    private String numero;

    @Field(type = FieldType.Text)
    private String nomVoie;

    @Field(type = FieldType.Text)
    private String zipCode;

    @Field(type = FieldType.Text)
    private String inseeCode;

    @Field(type = FieldType.Text)
    private String cityName;

    @GeoPointField
    private GeoPoint geoPoint;

    @Field(type = FieldType.Text)
    private String labelForwarding;

    @Field(type = FieldType.Text)
    private String searchName;

    public ElasticAddress(AddressToSaveDTO dto) {
        this.id = dto.getId();
        this.idFantoir = dto.getIdFantoir();
        this.numero = dto.getNumero();
        this.nomVoie = dto.getNomVoie();
        this.zipCode = dto.getZipCode();
        this.inseeCode = dto.getInseeCode();
        this.cityName = dto.getCityName();
        this.geoPoint = new GeoPoint(dto.getLatitude(), dto.getLongitude());
        this.labelForwarding = dto.getLabelForwarding();
        this.searchName = dto.getSearchName();
    }


    public AddressDTO toDto() {
        return AddressDTO.builder()
            .id(id)
            .idFantoir(idFantoir)
            .numero(numero)
            .nomVoie(nomVoie)
            .zipCode(zipCode)
            .inseeCode(inseeCode)
            .cityName(cityName)
            .longitude(geoPoint.getLon())
            .latitude(geoPoint.getLat())
            .labelForwarding(labelForwarding).build();
    }
}
