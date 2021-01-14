package no.skatteetaten.testnorge.eksempeladapter.testdata;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.skatteetaten.testnorge.tenor.dto.Konfigurasjon;

public class Application {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        Konfigurasjon konfigurasjon = new DatasetConfiguration(args[0], new TenorEnvironmentConfiguration()).konfigurasjon();

        Path theDir = Paths.get(args[0]+ "_datasett");
        if (Files.exists(theDir)) {
            Files.delete(theDir);
        }
        Files.createDirectories(theDir);

        write(args[0]+ "_datasett/konfigurasjon.json", konfigurasjon);

        for (int i = 1; i < args.length; i++) {
            Testdata testdata = new Testdata(args[i], args[i]);
            write(args[0]+ "_datasett/testdata_" + args[i] + ".json", testdata);
        }

    }

    private static <T> void write(String filnavn, T data) {
        try (FileWriter fileWriter = new FileWriter(filnavn)) {
            fileWriter.write(objectMapper.writeValueAsString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
