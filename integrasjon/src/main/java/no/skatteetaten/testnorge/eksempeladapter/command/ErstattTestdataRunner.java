package no.skatteetaten.testnorge.eksempeladapter.command;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import no.skatteetaten.testnorge.tenor.ApiResponse;
import no.skatteetaten.testnorge.tenor.dto.BatchTestdataJobbRequest;
import no.skatteetaten.testnorge.tenor.dto.OppdaterTestdataDokumentOperasjon;
import no.skatteetaten.testnorge.tenor.dto.TestdataDokumentOperasjon;

public class ErstattTestdataRunner extends CommandRunner {

    @Override
    public void usage() {
        System.out.println("<id felt> <versjon> - Bruk <id felt> for å identifisere testdata. Versjon er et heltall, default er timestamp");

        System.out.println();
        System.out.println("Denne kommandoen erstatter all testdata i løsningen.");
    }

    @Override
    public void run(String[] args) throws Exception {
        Long versjon = args.length == 2 ? ZonedDateTime.now().toEpochSecond() : Integer.parseInt(args[2]);

        List<TestdataDokumentOperasjon> operasjoner = new ArrayList<>();
        Iterator<JsonNode> jsonNodeIterator = stdinBulk();

        while (jsonNodeIterator.hasNext()) {
            JsonNode node = jsonNodeIterator.next();
            operasjoner.add(new OppdaterTestdataDokumentOperasjon()
                .id(node.get(args[1]).textValue())
                .soekdata(objectMapper().convertValue(node, Object.class))
                .versjon(versjon)
                .type(TestdataDokumentOperasjon.TypeEnum.OPPDATER));
        }

        if (operasjoner.isEmpty()) {
            System.err.println("Du må sende minst ett dokument");

            return;
        }

        ApiResponse<Long> longApiResponse =
            tenorApi().datasettVersjonApi().nyVersjonWithHttpInfo(tenorConfiguration().tenorIdentifikator());

        System.out.println("Ny versjon:" + longApiResponse.getData());

        tenorApi().datasettVersjonApi().oppdater(
            tenorConfiguration().tenorIdentifikator(),
            longApiResponse.getData(),
            new BatchTestdataJobbRequest().operasjoner(operasjoner)
        );

        System.out.println("Lastet opp data");

        tenorApi().datasettVersjonApi()
            .erstattWithHttpInfo(tenorConfiguration().tenorIdentifikator(), longApiResponse.getData());

        System.out.println("Datasett erstattet");
    }
}
