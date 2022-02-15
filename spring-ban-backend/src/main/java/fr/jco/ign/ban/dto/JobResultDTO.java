package fr.jco.ign.ban.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class JobResultDTO {

    private String file;

    private String details;

    private String duration;

    private int writeCount;

    private int skipReadCount;

    private List<String> errors;

    private String exitStatus;

}
