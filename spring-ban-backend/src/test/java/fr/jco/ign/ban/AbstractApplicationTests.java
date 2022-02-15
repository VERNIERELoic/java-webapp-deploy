package fr.jco.ign.ban;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.SneakyThrows;

/**
 * Contient un test de toutes les fonctionnalités de l'application (import CSV, puis recherche, puis import CSV en mode update, puis recherche)
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @SneakyThrows // Failed si execption
    @Test
    void contextLoads() {
        ClassPathResource resource = new ClassPathResource("/file.csv");
        MockMultipartFile file = new MockMultipartFile("file", resource.getFile().getName(), "multipart/csv", resource.getInputStream());

        // Import d'un premier fichier
        mockMvc.perform(MockMvcRequestBuilders.multipart("/address").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].exitStatus").value("COMPLETED"))
            .andDo(print());

        // Vérifie qu'il y a bien 5 imports pour brun et terasses
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "brun"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andDo(print());
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "Terrasses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andDo(print());

        // Vérifie que la ligne STRING_LON_LAT n'existe pas (Skip car Double.parseLong(STRING_LON_LAT) ne fonctionne pas)
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "STRING_LON_LAT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)))
            .andDo(print());

        // Update des données BRUN => TEST
        ClassPathResource otherResource = new ClassPathResource("/after_file.csv");
        MockMultipartFile otherFile = new MockMultipartFile("file", otherResource.getFile().getName(), "multipart/csv",
            otherResource.getInputStream());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/address").file(otherFile))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].exitStatus").value("COMPLETED"))
            .andDo(print());

        // Vérifie que les bruns ont été mis à jour
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "brun")) // Pas de brun
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)))
            .andDo(print());
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "test")) // Les nouvelles données
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andDo(print());
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "Terrasses")) // les données intactes
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andDo(print());
    }
}
