package fr.jco.ign.ban;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.SneakyThrows;

/**
 * Télécharge et importe le fichier courant de l'IGN pour le département du 69 et recherche ensuite une adresse unique
 */
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {IGNDownloadFileTests.Initializer.class})
@Testcontainers
public class IGNDownloadFileTests {

    private final static String IGN_URL = "https://adresse.data.gouv.fr/data/ban/adresses/latest/csv/adresses-69.csv.gz";
    private final static String workingFile = "/tmp/adresses-69";

    @Autowired
    MockMvc mockMvc;

    @SneakyThrows
    @BeforeAll
    static void downloadIgnFile() {
        // Download du fichier
        try (final ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(IGN_URL).openStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(getGzFile())) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        // Décompresse le fichier
        try (final FileOutputStream csvFile = new FileOutputStream(getCsvFile());
            final InputStream gzipInputStream = new GZIPInputStream(new FileInputStream(getGzFile()))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                csvFile.write(buffer, 0, len);
            }
        }
    }

    @SneakyThrows
    @Test
    void contextLoads() {
        File resource = new File(getCsvFile());
        MockMultipartFile file = new MockMultipartFile("file", resource.getName(), "multipart/csv", new FileInputStream(getCsvFile()));

        // Import du fichier
        mockMvc.perform(MockMvcRequestBuilders.multipart("/address").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].exitStatus").value("COMPLETED"))
            .andExpect(jsonPath("$[0].skipReadCount").value("0"))
            .andDo(print());

        // Doit trouver CPE
        mockMvc.perform(MockMvcRequestBuilders.get("/address").queryParam("terms", "46 boulevard niels bohr"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].longitude").value("4.869243"))
            .andExpect(jsonPath("$[0].latitude").value("45.784495"))
            .andDo(print());
    }

    private static String getCsvFile() {
        return workingFile.concat(".csv");
    }

    private static String getGzFile() {
        return workingFile.concat(".csv.gz");
    }

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14")
        .withDatabaseName("integration-tests-db")
        .withUsername("sa")
        .withPassword("sa");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
