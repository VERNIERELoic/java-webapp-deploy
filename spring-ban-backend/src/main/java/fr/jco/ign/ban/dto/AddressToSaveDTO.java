package fr.jco.ign.ban.dto;

import java.util.Locale;

import lombok.Getter;
import lombok.ToString;

/**
 * Représentation de l'objet provenant du CSV valide pour une sauvegarde
 */
@Getter
@ToString
public class AddressToSaveDTO {

    private final String id;

    private final String idFantoir;

    private final String numero;

    private final String nomVoie;

    private final String zipCode;

    private final String inseeCode;

    private final String cityName;

    private final Double longitude;

    private final Double latitude;

    private final String labelForwarding;

    private final String searchName;

    public AddressToSaveDTO(AddressCSV csv) {
        this.id = csv.getId();
        this.idFantoir = csv.getIdFantoir();
        this.numero = csv.getNumero();
        this.nomVoie = csv.getNomVoie();
        this.zipCode = csv.getCodePostal();
        this.inseeCode = csv.getCodeInsee();
        this.cityName = csv.getNomCommune();
        this.latitude = Double.parseDouble(csv.getLat());
        this.longitude = Double.parseDouble(csv.getLon());
        this.labelForwarding = csv.getLibelleAcheminement();
        this.searchName = String.format("%s %s (%s %s)", numero, nomVoie, zipCode, cityName).toLowerCase(Locale.ROOT);
    }
}
