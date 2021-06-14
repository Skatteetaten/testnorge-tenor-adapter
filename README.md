# eksempel-adapter

Eksempel integrasjon til Tenor kildeeier API for java automatisk generert ut fra open api dokumentasjon med eksempel konfigurasjon og eksempel testdata.

Denne koden legger ingen føringer for integrasjoner mot Tenor Kildetilbyder API og kildeiere oppfordres til å utvikle sine egne løsninger.

For tredjeparts-verktøy for integrasjon mot maskinporten henvises det til https://oauth.net/code/.
 
## Krav

For å kjøre eksempel adapteren trenger du:

- Java 11+
- Virksomhetssertifikat i en JKS
- En maskinportenintegrasjon tildelt scopet `skatteetaten:testnorge/testdata.write`
- En datasettidentifikator tildelt din virksomhet. 


## Moduler
* `./api` - Automatiske genererte integrasjonen mot API-et.
* `./integrasjon` - Eksempelintegrasjon mot maskinporten.
* `./datasett-generator` - Verktøy for å generere eksempel data og konfigurasjon.   
  
Klassen `DatasetConfiguration` under `./datasett-generator` er et eksempel på hvordan du kan lage en konfigurasjon ved hjelp av java.  
Klassen `Testdata` er et eksempel på en java-representasjon av dataen du som kide-eier kan tilby.    
Se [dokumentasjonen](https://skatteetaten.github.io/testnorge-tenor-dokumentasjon/xsd/) for ytterligere informasjon om krav og muligheter rundt opplastning av testdata. 

## TL;DR;
```shell script
# Kompiler
./mvnw clean install
# Sett konfigurasjon
export TENOR_URL=https://api-utv.sits.no/api/testnorge/v2
export TENOR_ORGNO=
export TENOR_IDENTIFIKATOR=
export TENOR_MASKINPORTEN_CLIENT_ID=
export TENOR_MASKINPORTEN_JKS_PATH=
export TENOR_MASKINPORTEN_JKS_ALIAS=
export TENOR_MASKINPORTEN_JKS_PASSWORD=
# Generer datasett
java -jar datasett-generator/target/testdata-1.0-jar-with-dependencies.jar $TENOR_IDENTIFIKATOR <synetisk fødselsnummer> [...<synetisk fødselsnummer>]
# Inspiser datasett
ls ${TENOR_IDENTIFIKATOR}_datasett
# Opprett datasett konfigurasjon
cat ${TENOR_IDENTIFIKATOR}_datasett/konfigurasjon.json | java -jar integrasjon/target/eksempel-adapter-1.0-jar-with-dependencies.jar CONFIG create
# Last inn/Oppdater testdata
cat ${TENOR_IDENTIFIKATOR}_datasett/testdata_*.json | java -jar integrasjon/target/eksempel-adapter-1.0-jar-with-dependencies.jar TESTDATA id
# Erstatt datasett
rm ${TENOR_IDENTIFIKATOR}_datasett -rf
java -jar datasett-generator/target/testdata-1.0-jar-with-dependencies.jar $TENOR_IDENTIFIKATOR <synetisk fødselsnummer> [...<synetisk fødselsnummer>]
cat ${TENOR_IDENTIFIKATOR}_datasett/testdata_*.json | java -jar integrasjon/target/eksempel-adapter-1.0-jar-with-dependencies.jar ERSTATT id
```

## Miljøvariabler
For å kjøre integrasjonstestene må man sette følgende miljøvariabler.

|Navn|Beskrivelse|Eksempel
|----|-----------|--------
|`TENOR_URL`|Rot URL til Tenor API|*https://api-utv.sits.no/api/testnorge/v2*
|`TENOR_ORGNO`|Organisasjonsnummer til din virksomhet|*0192:974761076*
|`TENOR_IDENTIFIKATOR`|Identifikator tildelt i tenor.|*freg*
|`TENOR_MASKINPORTEN_CLIENT_ID`|`client_id` til integrasjon registrert i maskinporten. Må være tildelt scopet `skatteetaten:testnorge/testdata.write`.|*tenor_integrasjon*
|`TENOR_MASKINPORTEN_JKS_PATH`|Sti til `JKS` som inneholder virksomhetssertifikat.|*`$PWD`*/virksomhetssertifikat.jks
|`TENOR_MASKINPORTEN_JKS_ALIAS`|Alias til sertifikat i jks.|*virksomhetssertifikat*
|`TENOR_MASKINPORTEN_JKS_PASSWORD`|Passord til JKS/ALIAS.|*changeit*
