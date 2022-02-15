package fr.jco.ign.ban.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

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
}
