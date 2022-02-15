package fr.jco.ign.ban.dto;


import lombok.Data;

/**
 * Représentation du CSV provenant de la BAN
 */
@Data
public class AddressCSV {

    private String id;
    private String idFantoir;
    private String numero;
    private String rep;
    private String nomVoie;
    private String codePostal;
    private String codeInsee;
    private String nomCommune;
    private String codeInseeAncienneCommune;
    private String nomAncienneCommune;
    private String x;
    private String y;
    private String lon;
    private String lat;
    private String typePosition;
    private String alias;
    private String nomLd;
    private String libelleAcheminement;
    private String nomAfnor;
    private String sourcePosition;
    private String sourceNomVoie;
    private String certificationCommune;
}
