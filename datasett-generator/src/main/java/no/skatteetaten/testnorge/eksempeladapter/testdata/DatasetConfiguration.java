package no.skatteetaten.testnorge.eksempeladapter.testdata;

import no.skatteetaten.testnorge.tenor.dto.Datasett;
import no.skatteetaten.testnorge.tenor.dto.Enhet;
import no.skatteetaten.testnorge.tenor.dto.Felt;
import no.skatteetaten.testnorge.tenor.dto.Konfigurasjon;
import no.skatteetaten.testnorge.tenor.dto.Relasjon;
import no.skatteetaten.testnorge.tenor.dto.Relasjoner;
import no.skatteetaten.testnorge.tenor.dto.Soek;
import no.skatteetaten.testnorge.tenor.dto.SoekeKriterie;
import no.skatteetaten.testnorge.tenor.dto.SoekeKriterier;
import no.skatteetaten.testnorge.tenor.dto.Soekedokument;

public class DatasetConfiguration {

    private final String identifikator;
    private final TenorConfiguration tenorConfiguration;

    public DatasetConfiguration(String identifikator, TenorConfiguration tenorConfiguration) {
        this.identifikator = identifikator;
        this.tenorConfiguration = tenorConfiguration;
    }

    public Konfigurasjon konfigurasjon() {
        return new Konfigurasjon()
            .datasett(new Datasett()
                .identifikator(identifikator)
                .datasettOverskrift("Mitt datasett")
                .datasettEier(new Enhet()
                    .organisasjonsnummer(tenorConfiguration.orgno())
                    .organisasjonsnavn("Test")
                )
            )
            .relasjoner(new Relasjoner()
                .addRelasjonerItem(new Relasjon()
                    .felt(new Felt().feltnavn("pid").feltOverskrift("Personnummer"))
                    .referertDatasett("freg")
                    .relasjon("test")
                )
            )
            .soekevisning(new Soekedokument()
                .addDetaljertListeItem(new Felt().feltnavn("id").feltOverskrift("Vår id"))
                .addDetaljertListeItem(new Felt().feltnavn("felt").feltOverskrift("Bool felt"))
            )
            .soekekriterier(
                new SoekeKriterier().addSoekItem(
                    new Soek()
                        .soekeTema("Tema")
                        .erPrimaerKildeSoek(false)
                        .addSoekekriterierItem(
                            new SoekeKriterie()
                                .felt(new Felt().feltnavn("felt").feltOverskrift("Bool søk"))
                                .typeSoek(SoekeKriterie.TypeSoekEnum.BOOL)
                        )
                )
            );
    }

}
