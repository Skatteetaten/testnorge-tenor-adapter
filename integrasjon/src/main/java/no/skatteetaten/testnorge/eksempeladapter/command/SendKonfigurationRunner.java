package no.skatteetaten.testnorge.eksempeladapter.command;

import no.skatteetaten.testnorge.tenor.ApiResponse;
import no.skatteetaten.testnorge.tenor.dto.Konfigurasjon;

public class SendKonfigurationRunner extends CommandRunner {

    @Override
    public void usage() {
        System.out.println("create - opprett konfigurasjonen første gang");
        System.out.println("relasjoner - oppdater relasjonene til datasettet");
        System.out.println("visning - oppdater nøkkelinformasjon visning i GUI");
        System.out.println("kriterier - oppdater søkekriteriene i enkelt søk i GUI");
        System.out.println("metadata - oppdater metadata til datasett");
    }

    @Override
    public void run(String[] args) throws Exception {
        ApiResponse<Konfigurasjon> response;
        if (args[1].equals("create")) {
            response = tenorApi().datasettApi().opprettWithHttpInfo(stdin(Konfigurasjon.class));
        } else if (args[1].equals("relasjoner")) {
            response = tenorApi().datasettApi().relasjonerWithHttpInfo(tenorConfiguration().tenorIdentifikator(),
                    stdin(Konfigurasjon.class).getRelasjoner());
        } else if (args[1].equals("visning")) {
            response = tenorApi().datasettApi().visningWithHttpInfo(tenorConfiguration().tenorIdentifikator(),
                stdin(Konfigurasjon.class).getSoekevisning());
        } else if (args[1].equals("kriterier")) {
            response = tenorApi().datasettApi().kriterierWithHttpInfo(tenorConfiguration().tenorIdentifikator(),
                stdin(Konfigurasjon.class).getSoekekriterier());
        } else if (args[1].equals("metadata")) {
            response = tenorApi().datasettApi().metadataWithHttpInfo(tenorConfiguration().tenorIdentifikator(),
                stdin(Konfigurasjon.class).getDatasett());
        } else {
            usage();
            return;
        }

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: ");
        System.out.println(objectMapper().writeValueAsString(response.getData()));
    }
}
